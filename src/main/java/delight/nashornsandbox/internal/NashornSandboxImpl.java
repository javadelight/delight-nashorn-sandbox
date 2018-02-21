package delight.nashornsandbox.internal;

import java.io.Writer;
import java.util.concurrent.ExecutorService;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 * Nashorn sandbox implementation.
 *
 * <p>
 * Created on 2015-08-07
 * </p>
 *
 * @author <a href="mailto:mxro@nowhere.com">mxro</a>
 * @author <a href="mailto:mellster2000@yahoo.com">Marco Ellwanger</a>
 * @author <a href="mailto:dev@youness.org">Youness SAHOUANE</a>
 * @author <a href="mailto:eduveks@gmail.com">Eduardo Velasques</a>
 * @author <a href="mailto:philip.borgstrom@gmail.com">philipborg</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class NashornSandboxImpl implements NashornSandbox {

	static final Logger LOG = LoggerFactory.getLogger(NashornSandbox.class);

	protected final SandboxClassFilter sandboxClassFilter;

	protected final ScriptEngine scriptEngine;

	/** Maximum CPU time in milliseconds. */
	protected long maxCPUTime = 0L;

	/** Maximum memory of executor thread used. */
	protected long maxMemory = 0L;

	protected ExecutorService executor;

	protected boolean allowPrintFunctions = false;

	protected boolean allowReadFunctions = false;

	protected boolean allowLoadFunctions = false;

	protected boolean allowExitFunctions = false;

	protected boolean allowGlobalsObjects = false;

	protected boolean allowNoBraces = false;

	protected JsEvaluator evaluator;

	protected JsSanitizer sanitizer;

	protected boolean engineAsserted;

	protected Invocable lazyInvocable;

	/** The size of the LRU cache of prepared statemensts. */
	protected int maxPreparedStatements;

	public NashornSandboxImpl() {
		this(new String[0]);
	}
	
	public NashornSandboxImpl(String... params) {
		this.maxPreparedStatements = 0;
		this.sandboxClassFilter = new SandboxClassFilter();
		final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		
		this.scriptEngine = factory.getScriptEngine(params, this.getClass().getClassLoader(), this.sandboxClassFilter);
		
		this.allow(InterruptTest.class);
	}

	private void assertScriptEngine() {
		try {
			final StringBuilder sb = new StringBuilder();
			if (!allowExitFunctions) {
				sb.append("quit=function(){};exit=function(){};");
			}
			if (!allowPrintFunctions) {
				sb.append("print=function(){};echo = function(){};");
			}
			if (!allowReadFunctions) {
				sb.append("readFully=function(){};").append("readLine=function(){};");
			}
			if (!allowLoadFunctions) {
				sb.append("load=function(){};loadWithNewGlobal=function(){};");
			}
			if (!allowGlobalsObjects) {
				sb.append("$ARG=null;$ENV=null;$EXEC=null;");
				sb.append("$OPTIONS=null;$OUT=null;$ERR=null;$EXIT=null;");
			}
			scriptEngine.eval(sb.toString());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object eval(final String js) throws ScriptCPUAbuseException, ScriptException {
		return eval(js, null);
	}

	@Override
	public Object eval(final String js, final ScriptContext scriptContext)
			throws ScriptCPUAbuseException, ScriptException {
		final JsSanitizer sanitizer = getSanitizer();
		final String securedJs = sanitizer.secureJs(js);
		EvaluateOperation op = new EvaluateOperation(securedJs, scriptContext);
		return executeSandboxedOperation(op);
	}

	private Object executeSandboxedOperation(ScriptEngineOperation op) throws ScriptCPUAbuseException, ScriptException {
		if (!engineAsserted) {
			engineAsserted = true;
			assertScriptEngine();
		}
		try {
			if (maxCPUTime == 0 && maxMemory == 0) {
				return op.executeScriptEngineOperation(scriptEngine);
			}
			checkExecutorPresence();
			final JsEvaluator evaluator = getEvaluator(op);
			executor.execute(evaluator);
			evaluator.runMonitor();
			if (evaluator.isCPULimitExceeded()) {
				throw new ScriptCPUAbuseException(
						"Script used more than the allowed [" + maxCPUTime + " ms] of CPU time.",
						evaluator.isScriptKilled(), evaluator.getException());
			} else if (evaluator.isMemoryLimitExceeded()) {
				throw new ScriptMemoryAbuseException(
						"Script used more than the allowed [" + maxMemory + " B] of memory.",
						evaluator.isScriptKilled(), evaluator.getException());
			}
			if (evaluator.getException() != null) {
				throw evaluator.getException();
			}
			return evaluator.getResult();
		} catch (RuntimeException | ScriptException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JsEvaluator getEvaluator(ScriptEngineOperation op) {
		return new JsEvaluator(scriptEngine, maxCPUTime, maxMemory, op);
	}

	private void checkExecutorPresence() {
		if (executor == null) {
			throw new IllegalStateException("When a CPU time or memory limit is set, an executor "
					+ "needs to be provided by calling .setExecutor(...)");
		}
	}

	@Override
	public void setMaxCPUTime(final long limit) {
		maxCPUTime = limit;
	}

	@Override
	public void setMaxMemory(final long limit) {
		maxMemory = limit;
	}

	JsSanitizer getSanitizer() {
		if (sanitizer == null) {
			sanitizer = new JsSanitizer(scriptEngine, maxPreparedStatements, allowNoBraces);
		}
		return sanitizer;
	}

	@Override
	public void allow(final Class<?> clazz) {
		sandboxClassFilter.add(clazz);
	}

	@Override
	public void disallow(final Class<?> clazz) {
		sandboxClassFilter.remove(clazz);
	}

	@Override
	public boolean isAllowed(final Class<?> clazz) {
		return sandboxClassFilter.contains(clazz);
	}

	@Override
	public void disallowAllClasses() {
		sandboxClassFilter.clear();
	}

	@Override
	public void inject(final String variableName, final Object object) {
		if (object != null && !sandboxClassFilter.contains(object.getClass())) {
			allow(object.getClass());
		}
		scriptEngine.put(variableName, object);
	}

	@Override
	public void setExecutor(final ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public ExecutorService getExecutor() {
		return executor;
	}

	@Override
	public Object get(final String variableName) {
		return scriptEngine.get(variableName);
	}

	@Override
	public void allowPrintFunctions(final boolean v) {
		if (engineAsserted) {
			throw new IllegalStateException("Please set this property before calling eval.");
		}
		allowPrintFunctions = v;
	}

	@Override
	public void allowReadFunctions(final boolean v) {
		if (engineAsserted) {
			throw new IllegalStateException("Please set this property before calling eval.");
		}
		allowReadFunctions = v;
	}

	@Override
	public void allowLoadFunctions(final boolean v) {
		if (engineAsserted) {
			throw new IllegalStateException("Please set this property before calling eval.");
		}
		allowLoadFunctions = v;
	}

	@Override
	public void allowExitFunctions(final boolean v) {
		if (engineAsserted) {
			throw new IllegalStateException("Please set this property before calling eval.");
		}
		allowExitFunctions = v;
	}

	@Override
	public void allowGlobalsObjects(final boolean v) {
		if (engineAsserted) {
			throw new IllegalStateException("Please set this property before calling eval.");
		}
		allowGlobalsObjects = v;
	}

	@Override
	public void allowNoBraces(final boolean v) {
		if (allowNoBraces != v) {
			sanitizer = null;
		}
		allowNoBraces = v;
	}

	@Override
	public void setWriter(final Writer writer) {
		scriptEngine.getContext().setWriter(writer);
	}

	@Override
	public void setMaxPreparedStatements(final int max) {
		if (maxPreparedStatements != max) {
			sanitizer = null;
		}
		maxPreparedStatements = max;
	}

	@Override
	public Bindings createBindings() {
		return scriptEngine.createBindings();
	}

	@Override
	public Invocable getSandboxedInvocable() {
		if (maxMemory == 0 && maxCPUTime == 0) {
			return (Invocable)scriptEngine;
		}
		return getLazySandboxedInvocable();
	}

	private Invocable getLazySandboxedInvocable() {
		if (lazyInvocable == null) {
			Invocable sandboxInvocable = new Invocable() {

				@Override
				public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
					InvokeOperation op = new InvokeOperation(thiz, name, args);
					try {
						return executeSandboxedOperation(op);
					} catch (ScriptException e) {
						throw e;
					} catch (Exception e) {
						throw new ScriptException(e);
					}
				}

				@Override
				public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
					InvokeOperation op = new InvokeOperation(null, name, args);
					try {
						return executeSandboxedOperation(op);
					} catch (ScriptException e) {
						throw e;
					} catch (Exception e) {
						throw new ScriptException(e);
					}
				}

				@Override
				public <T> T getInterface(Object thiz, Class<T> clasz) {
					// TODO add proxy wrapper for proper sandboxing
					throw new IllegalStateException("Not yet implemented");
				}

				@Override
				public <T> T getInterface(Class<T> clasz) {
					// TODO add proxy wrapper for proper sandboxing
					throw new IllegalStateException("Not yet implemented");
				}
			};
			lazyInvocable = sandboxInvocable;
		}
		return lazyInvocable;
	}

}
