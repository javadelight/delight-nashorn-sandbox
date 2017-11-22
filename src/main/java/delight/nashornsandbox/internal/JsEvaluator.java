package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import java.util.concurrent.ExecutorService;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * The JavaScript evaluator. It is designed to run Nashorn engine in separate
 * thread (using provided {@link ExecutorService}), to allow limit cpu time 
 * consumed. 
 *
 * <p>Created on 2017.11.22</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
class JsEvaluator implements Runnable {
  private final NashornSandboxImpl sandbox;
  private final ThreadMonitor threadMonitor;
  private String js;
  private final ScriptEngine scriptEngine;
    
  private Object result = null;
  private Exception exception = null;
    
  JsEvaluator(final ScriptEngine scriptEngine, final NashornSandboxImpl sandbox, 
      final long maxCPUTime) {
    this.scriptEngine = scriptEngine;
    this.sandbox = sandbox;
    this.threadMonitor = new ThreadMonitor(maxCPUTime);
  }

  boolean isEvalAborted() {
    return threadMonitor.isEvalAborted();
  }

  boolean isCPULimitExceeded() {
    return threadMonitor.isCPULimitExceeded();
  }

  /**
   * Enter the monitor mehtod. It should be called from main thread. 
   */
  void runMonitor() {
    threadMonitor.run();
  }
    
  @Override
  public void run() {
    try {
      threadMonitor.setThreadToMonitor(Thread.currentThread());
      if (LOG.isDebugEnabled()) {
        LOG.debug("--- Running JS ---");
        LOG.debug(js);
        LOG.debug("--- JS END ---");
      }
      result = scriptEngine.eval(js);
    } 
    catch (final ScriptException e) {
      if (e.getMessage().contains(sandbox.getJsInterruptedError())) {
        threadMonitor.evalAborted();
      } 
      else {
        exception = e;
      }
    }
    catch (final Exception e) {
      exception = e;
    } 
    finally {
      threadMonitor.stopMonitor();
    }
  }
    
  /**Set JavaScrip text to be evaluated. */
  void setJs(final String js) {
    this.js = js;
  }

  Exception getException() {
    return exception;
  }
  
  Object getResult() {
    return result;
  }
  
}