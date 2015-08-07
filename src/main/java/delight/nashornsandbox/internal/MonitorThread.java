package delight.nashornsandbox.internal;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class MonitorThread extends Thread {
  private final long maxCPUTime;
  
  private final AtomicBoolean stop;
  
  private final AtomicBoolean operationInterrupted;
  
  private final Thread threadToMonitor;
  
  private final Runnable onInvalid;
  
  @Override
  public void run() {
    try {
      while ((!this.stop.get())) {
        {
          final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
          long _id = this.threadToMonitor.getId();
          final long threadCPUTime = bean.getThreadCpuTime(_id);
          InputOutput.<Long>println(Long.valueOf(threadCPUTime));
          if ((threadCPUTime > this.maxCPUTime)) {
            this.stop.set(true);
            this.onInvalid.run();
            Thread.sleep(50);
            boolean _get = this.operationInterrupted.get();
            boolean _equals = (_get == false);
            if (_equals) {
              String _plus = (this + ": Thread hard shutdown!");
              InputOutput.<String>println(_plus);
              this.threadToMonitor.stop();
            }
            return;
          }
          Thread.sleep(5);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void stopMonitor() {
    this.stop.set(true);
  }
  
  public void notifyOperationInterrupted() {
    this.operationInterrupted.set(true);
  }
  
  public MonitorThread(final long maxCPUTimne, final Thread threadToMonitor, final Runnable onInvalid) {
    this.maxCPUTime = maxCPUTimne;
    this.threadToMonitor = threadToMonitor;
    this.onInvalid = onInvalid;
    AtomicBoolean _atomicBoolean = new AtomicBoolean(false);
    this.stop = _atomicBoolean;
    AtomicBoolean _atomicBoolean_1 = new AtomicBoolean(false);
    this.operationInterrupted = _atomicBoolean_1;
  }
}
