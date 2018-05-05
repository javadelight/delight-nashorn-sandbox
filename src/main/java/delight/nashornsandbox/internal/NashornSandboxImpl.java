package delight.nashornsandbox.internal;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.Writer;
import java.util.concurrent.ExecutorService;

/**
 * Nashorn sandbox implementation.
 * <p>
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

    /**
     * Maximum CPU time in milliseconds.
     */
    protected long maxCPUTime = 0L;

    /**
     * Maximum memory of executor thread used.
     */
    protected long maxMemory = 0L;

    protected ExecutorService executor;

    //made these package private, so they can be tested
    boolean allowPrintFunctions = false;

    boolean allowReadFunctions = false;

    boolean allowLoadFunctions = false;

    boolean allowExitFunctions = false;

    boolean allowGlobalsObjects = false;

    boolean allowNoBraces = false;

    protected JsEvaluator evaluator;

    protected JsSanitizer sanitizer;

    protected boolean engineAsserted;

    protected Invocable lazyInvocable;

    /**
     * The size of the LRU cache of prepared statemensts.
     */
    protected int maxPreparedStatements;

    public NashornSandboxImpl() {
        this(new SandboxClassFilter(), new String[0]);
    }

    //constructor modified for testing
     private NashornSandboxImpl(SandboxClassFilter filter, String... params) {
        this.maxPreparedStatements = 0;
        this.sandboxClassFilter = filter;
        final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

        this.scriptEngine = factory.getScriptEngine(params, this.getClass().getClassLoader(), this.sandboxClassFilter);

        this.allow(InterruptTest.class);
    }

    public NashornSandboxImpl(String... params) {
        this(new SandboxClassFilter(), params);
    }

    NashornSandboxImpl(SandboxClassFilter filter) {
        this(filter, new String[0]);

    }

    private void assertScriptEngine() {
        try {
            final StringBuilder sb = new StringBuilder();
            Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            if (!allowExitFunctions) {

                bindings.remove("quit");
                bindings.remove("exit");
                sb.append("var quit=function(){};var exit=function(){};");
            }
            if (!allowPrintFunctions) {
                bindings.remove("print");
                bindings.remove("echo");
                sb.append("var print=function(){};var echo = function(){};");
            }
            if (!allowReadFunctions) {
                bindings.remove("readFully");
                bindings.remove("readLine");
                sb.append("var readFully=function(){};").append("var readLine=function(){};");
            }
            if (!allowLoadFunctions) {
                bindings.remove("load");
                bindings.remove("loadWithNewGlobal");
                sb.append("var load=function(){};var loadWithNewGlobal=function(){};");
            }
            if (!allowGlobalsObjects) {
                // Max 22nd of Feb 2018: I don't think these are strictly necessary since they are only available in scripting mode
                sb.append("var $ARG=null;var $ENV=null;var $EXEC=null;");
                sb.append("var $OPTIONS=null;var $OUT=null;var $ERR=null;var $EXIT=null;");
            }
            scriptEngine.eval(sb.toString());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object eval(final String js) throws ScriptCPUAbuseException, ScriptException {
        return eval(js, null, null);
    }

    @Override
    public Object eval(String js, Bindings bindings) throws ScriptCPUAbuseException, ScriptException {
        return eval(js, null, bindings);
    }

    @Override
    public Object eval(String js, ScriptContext scriptContext) throws ScriptCPUAbuseException, ScriptException {
        return eval(js, scriptContext, null);
    }

    @Override
    public Object eval(final String js, final ScriptContext scriptContext, final Bindings bindings)
            throws ScriptCPUAbuseException, ScriptException {
        final JsSanitizer sanitizer = getSanitizer();
        final String securedJs = sanitizer.secureJs(js);
        EvaluateOperation op = new EvaluateOperation(securedJs, scriptContext, bindings);
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
        if (engineAsserted) {
            throw new IllegalStateException("Please set this property before calling eval.");
        }
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
            return (Invocable) scriptEngine;
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
