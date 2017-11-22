package delight.nashornsandbox.internal;

import java.io.Writer;
import java.util.concurrent.ExecutorService;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 * Nashorn sandbox implementation.
 *
 * <p>Created on 2015-08-07</p>
 *
 * @author <a href="mailto:mxro@nowhere.com">mxro</a> 
 * @author <a href="mailto:mellster2000@yahoo.com">Marco Ellwanger</a>
 * @author <a href="mailto:dev@youness.org">Youness SAHOUANE</a> 
 * @author <a href="mailto:eduveks@gmail.com">Eduardo Velasques</a> 
 * @author <a href="mailto:philip.borgstrom@gmail.com">philipborg</a> 
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class NashornSandboxImpl implements NashornSandbox {
    
  static final Logger LOG = Logger.getLogger(NashornSandbox.class);
  
  private final SandboxClassFilter sandboxClassFilter;
  
  private final ScriptEngine scriptEngine;
  
  /** Maximum CPU time in miliseconds.*/
  private long maxCPUTime = 0L;
  
  private ExecutorService executor;
  
  private boolean allowPrintFunctions = false;
  
  private boolean allowReadFunctions = false;
  
  private boolean allowLoadFunctions = false;
  
  private boolean allowExitFunctions = false;
  
  private boolean allowGlobalsObjects = false;
  
  private JsEvaluator evaluator;
  
  private JsSanitizer sanitizer;
  
  /**The size of the LRU cache of prepared statemensts.*/
  private int maxPreparedStatements;
  
  public NashornSandboxImpl() {
    this.maxPreparedStatements = 0;
    this.sandboxClassFilter = new SandboxClassFilter();
    final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    this.scriptEngine = factory.getScriptEngine(this.sandboxClassFilter);
    this.allow(InterruptTest.class);
    this.assertScriptEngine();
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
    } 
    catch (final Exception e) {
      throw Exceptions.sneakyThrow(e);
    }
  }
  
  @Override
  public Object eval(final String js) throws ScriptCPUAbuseException, ScriptException {
    try {
      if (this.maxCPUTime == 0) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("--- Running JS ---");
          LOG.debug(js);
          LOG.debug("--- JS END ---");
        }
        return this.scriptEngine.eval(js);
      }
      synchronized (this) {
        checkExecutorPresence();
        final JsSanitizer sanitizer = getSanitizer();
        final String securedJs = sanitizer.secureJs(js);
        final JsEvaluator evaluator = getEvaluator();
        evaluator.setJs(securedJs);
        executor.execute(evaluator);
        evaluator.runMonitor();
        if (evaluator.isCPULimitExceeded()) {
          String notGraceful = "";
          if (!evaluator.isEvalAborted()) {
            notGraceful = " The operation could not be gracefully interrupted.";
          }
          throw new ScriptCPUAbuseException(
                "Script used more than the allowed [" + maxCPUTime + 
                  " ms] of CPU time. " + notGraceful, evaluator.getException());
        }
        if (evaluator.getException() != null) {
          throw evaluator.getException();
        }
        return evaluator.getResult();
      }
    } 
    catch (ScriptCPUAbuseException | ScriptException e) {
      throw e;
    }
    catch (final Exception e) {
      throw Exceptions.sneakyThrow(e);
    }
  }

  private JsEvaluator getEvaluator() {
    if(evaluator == null) {
      evaluator = new JsEvaluator(scriptEngine, this, maxCPUTime);
    }
    return evaluator;
  }

  private void checkExecutorPresence() {
    if (executor == null) {
      throw new IllegalStateException("When a CPU time limit is set, an executor " +
              "needs to be provided by calling .setExecutor(...)");
    }
  }

  @Override
  public void setMaxCPUTime(final long limit) {
      maxCPUTime = limit;
  }

  JsSanitizer getSanitizer() {
    if(sanitizer==null) {
      sanitizer = new JsSanitizer(scriptEngine, maxPreparedStatements);
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
    if(object != null && !sandboxClassFilter.contains(object.getClass())) {
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
    allowPrintFunctions = v;
  }
  
  @Override
  public void allowReadFunctions(final boolean v) {
    allowReadFunctions = v;
  }
  
  @Override
  public void allowLoadFunctions(final boolean v) {
    allowLoadFunctions = v;
  }
  
  @Override
  public void allowExitFunctions(final boolean v) {
    allowExitFunctions = v;
  }
  
  @Override
  public void allowGlobalsObjects(final boolean v) {
    allowGlobalsObjects = v;
  }
  
  @Override
  public void setWriter(final Writer writer) {
    scriptEngine.getContext().setWriter(writer);
  }
  
  @Override
  public void setMaxPerparedStatements(final int max) {
    if(maxPreparedStatements != max) {
        sanitizer = null;
    }
    maxPreparedStatements = max;
  }

  String getJsInterruptedError() {
    return getSanitizer().getJsInterruptedError();
  }
  
}
