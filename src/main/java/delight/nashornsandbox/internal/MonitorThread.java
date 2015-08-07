package delight.nashornsandbox.internal;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("all")
public class MonitorThread extends Thread {
  private final long maxCPUTime;
  
  private final AtomicBoolean stop;
  
  private final AtomicBoolean operationInterrupted;
  
  private final Thread threadToMonitor;
  
  private final Runnable onInvalid;
  
  @Override
  public void run() {
    throw new Error("Unresolved compilation problems:"
      + "\nno viable alternative at input \')\'"
      + "\nType mismatch: cannot convert from null to boolean");
  }
  
  public void stopMonitor() {
    this.stop.set(true);
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
