package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * JS executor thread monitor. It is designed to be run in main thread (the
 * JS script is executed in other thread).
 *
 * <p>Created on 2015-08-07</p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class ThreadMonitor {
  private static final int MILI_TO_NANO = 1000000;

  private final long maxCPUTime;
  
  private final AtomicBoolean stop;
  
  private final AtomicBoolean evalAborted;
  
  private final AtomicBoolean cpuLimitExceeded;
  
  private final Object monitor;
  
  private Thread threadToMonitor;
  
  ThreadMonitor(final long maxCPUTimne) {
    this.maxCPUTime = maxCPUTimne * 1000000;
    this.stop = new AtomicBoolean(false);
    this.evalAborted = new AtomicBoolean(false);
    this.cpuLimitExceeded = new AtomicBoolean(false);
    this.monitor = new Object();
  }
  
  private void reset() {
    stop.set(false);
    evalAborted.set(false);
    cpuLimitExceeded.set(false);
    threadToMonitor = null;
  }
  
  @SuppressWarnings("deprecation")
  void run() {
    try {
      // wait, for threadToMonitor to be set in JS eveluator thread
      synchronized (monitor) {
        monitor.wait(maxCPUTime/MILI_TO_NANO);
      }
      if(threadToMonitor == null) {
          throw new IllegalStateException("Executor thread not set after " + 
                  maxCPUTime/MILI_TO_NANO + " ms");
      }
      final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
      final long startCPUTime = bean.getThreadCpuTime(threadToMonitor.getId());
      while (!stop.get()) {
          final long threadCPUTime = bean.getThreadCpuTime(threadToMonitor.getId());
          final long runtime = threadCPUTime-startCPUTime;
          if (runtime > maxCPUTime) {
            cpuLimitExceeded.set(true);
            stop.set(true);
            
            threadToMonitor.interrupt();
            Thread.sleep(50);
            if (!evalAborted.get()) {
              LOG.warn(this.getClass().getSimpleName() + ": Thread hard shutdown!");
              threadToMonitor.stop();
            }
            return;
          }
          synchronized (monitor) {
            monitor.wait(Math.max((maxCPUTime-runtime)/MILI_TO_NANO,5));
          }
      }
    } catch (final Exception e) {
      throw Exceptions.sneakyThrow(e);
    }
  }
  
  public void stopMonitor() {
    this.stop.set(true);
    notifyMonitorThread();
  }
  
  public void setThreadToMonitor(final Thread t) {
    reset();
    this.threadToMonitor = t;
    notifyMonitorThread();
  }
  
  public void evalAborted() {
    this.evalAborted.set(true);
  }
  
  public boolean isCPULimitExceeded() {
    return this.cpuLimitExceeded.get();
  }
  
  public boolean isEvalAborted() {
    return this.evalAborted.get();
  }
  
  private void notifyMonitorThread() {
    synchronized(monitor) {
      monitor.notifyAll();
    }
  }
  
}
