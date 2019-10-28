package delight.nashornsandbox.internal;

import java.util.concurrent.ExecutorService;

import javax.script.ScriptEngine;

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
  private final ThreadMonitor threadMonitor;
  private final ScriptEngine scriptEngine;
    
  private Object result = null;
  private Exception exception = null;
  private final ScriptEngineOperation operation;

  JsEvaluator(final ScriptEngine scriptEngine, final long maxCPUTime, final long maxMemory, ScriptEngineOperation operation) {
    this.scriptEngine = scriptEngine;
    this.threadMonitor = new ThreadMonitor(maxCPUTime, maxMemory);
    this.operation = operation;
  }

  boolean isScriptKilled() {
    return threadMonitor.isScriptKilled();
  }

  boolean isCPULimitExceeded() {
    return threadMonitor.isCPULimitExceeded();
  }
  
  boolean isMemoryLimitExceeded() {
    return threadMonitor.isMemoryLimitExceeded();
  }

  /**
   * Enter the monitor method. It should be called from main thread. 
   */
  void runMonitor() {
    threadMonitor.run();
  }
    
  @Override
  public void run() {
    try {
      boolean registered = threadMonitor.registerThreadToMonitor(Thread.currentThread());
      if (registered) {
        result = operation.executeScriptEngineOperation(scriptEngine);
      }
    }
    catch (final RuntimeException e) {
      // InterruptedException means script was successfully interrupted,
      // so no exception should be propagated
      if(!(e.getCause() instanceof InterruptedException)) {
        exception = e;
      }
    }
    catch (final Exception e) {
      exception = e;
    } 
    finally {
      threadMonitor.scriptFinished();
      threadMonitor.stopMonitor();
    }
  }

  Exception getException() {
    return exception;
  }
  
  Object getResult() {
    return result;
  }
}
