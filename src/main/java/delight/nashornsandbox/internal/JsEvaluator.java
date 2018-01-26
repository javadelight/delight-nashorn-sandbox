package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import java.util.concurrent.ExecutorService;

import javax.script.ScriptContext;
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
  private String js;
  private final ScriptEngine scriptEngine;
    
  private Object result = null;
  private Exception exception = null;
  private ScriptContext scriptContext = null;
    
  JsEvaluator(final ScriptEngine scriptEngine, final long maxCPUTime, final long maxMemory) {
    this.scriptEngine = scriptEngine;
    this.threadMonitor = new ThreadMonitor(maxCPUTime, maxMemory);
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
      threadMonitor.setThreadToMonitor(Thread.currentThread());
      if (LOG.isDebugEnabled()) {
        LOG.debug("--- Running JS ---");
        LOG.debug(js);
        LOG.debug("--- JS END ---");
      }
      
      if (scriptContext != null) {
    	  result = scriptEngine.eval(js, scriptContext);
      } else {
    	  result = scriptEngine.eval(js);
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
  
  /** Set ScriptContext to set set different scopes to evaluate */
  void setScriptContext(ScriptContext scriptContext) {
		this.scriptContext = scriptContext;
  }
  
}