package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JS executor thread monitor. It is designed to be run in main thread (the JS
 * script is executed in other thread).
 *
 * <p>
 * Created on 2015-08-07
 * </p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class ThreadMonitor {
	private static final int MILI_TO_NANO = 1000000;

	private final long maxCPUTime;

	private final long maxMemory;

	private final AtomicBoolean stop;

	/** Check if interrupted script has finished. */
	private final AtomicBoolean scriptFinished;

	/** Check if script should be killed to stop it when abusive. */
	private final AtomicBoolean scriptKilled;

	private final AtomicBoolean cpuLimitExceeded;

	private final AtomicBoolean memoryLimitExceeded;

	private final Object monitor;

	private Thread threadToMonitor;

	private ThreadMXBean threadBean;

	private final com.sun.management.ThreadMXBean memoryCounter;

	ThreadMonitor(final long maxCPUTime, final long maxMemory) {
		this.maxMemory = maxMemory;
		this.maxCPUTime = maxCPUTime * 1000000;
		stop = new AtomicBoolean(false);
		scriptFinished = new AtomicBoolean(false);
		scriptKilled = new AtomicBoolean(false);
		cpuLimitExceeded = new AtomicBoolean(false);
		memoryLimitExceeded = new AtomicBoolean(false);
		monitor = new Object();
		
		// ensure the ThreadMXBean is supported in the JVM
		try {
			threadBean = ManagementFactory.getThreadMXBean();
			// ensure the ThreadMXBean is enabled for CPU time measurement
			threadBean.setThreadCpuTimeEnabled(true);
		} catch (UnsupportedOperationException ex) {
			if(maxCPUTime > 0) {
				throw new UnsupportedOperationException("JVM does not support thread CPU time measurement");
			}
		}
		
		if ((threadBean != null) && (threadBean instanceof com.sun.management.ThreadMXBean)) {
			memoryCounter = (com.sun.management.ThreadMXBean) threadBean;
			// ensure this feature is enabled
			memoryCounter.setThreadAllocatedMemoryEnabled(true);
		} else {
			if (maxMemory > 0) {
				throw new UnsupportedOperationException("JVM does not support thread memory counting");
			}
			memoryCounter = null;
		}
	}

	private void reset() {
		stop.set(false);
		scriptFinished.set(false);
		scriptKilled.set(false);
		cpuLimitExceeded.set(false);
		threadToMonitor = null;
	}

	@SuppressWarnings("deprecation")
	void run() {
		try {
			// wait, for threadToMonitor to be set in JS evaluator thread
			synchronized (monitor) {
				if (threadToMonitor == null) {
					monitor.wait((maxCPUTime + 100) / MILI_TO_NANO);
				}
			}
			if (threadToMonitor == null) {
				throw new IllegalStateException("Executor thread not set after " + maxCPUTime / MILI_TO_NANO + " ms");
			}
			final long startCPUTime = getCPUTime();
			final long startMemory = getCurrentMemory();
			while (!stop.get()) {
				final long runtime = getCPUTime() - startCPUTime;
				final long memory = getCurrentMemory() - startMemory;
				
				if (isCpuTimeExided(runtime) || isMemoryExided(memory)) {
					
					cpuLimitExceeded.set(isCpuTimeExided(runtime));
					memoryLimitExceeded.set(isMemoryExided(memory));
					threadToMonitor.interrupt();
					synchronized (monitor) {
						monitor.wait(50);
					}
					if (stop.get()) {
						return;
					}
					if (!scriptFinished.get()) {
						LOG.error(this.getClass().getSimpleName() + ": Thread hard shutdown!");
						threadToMonitor.stop();
						scriptKilled.set(true);
					}
					return;
				} else {
					
				}
				synchronized (monitor) {
					long waitTime = getCheckInterval(runtime);
					
					if (waitTime == 0) {
						waitTime = 1;
					}
					monitor.wait(waitTime);
				}
				
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private long getCheckInterval(final long runtime) {
		if (maxCPUTime == 0) {
			return 10;
		}
		if (maxMemory == 0) {
			return Math.max((maxCPUTime - runtime) / MILI_TO_NANO, 5);
		}
		return Math.min((maxCPUTime - runtime) / MILI_TO_NANO, 10);
	}

	private boolean isCpuTimeExided(final long runtime) {
		if (maxCPUTime == 0) {
			return false;
		}
		return runtime > maxCPUTime;
	}

	private boolean isMemoryExided(final long memory) {
		if (maxMemory == 0) {
			return false;
		}
		return memory > maxMemory;
	}

	/**
	 * Obtain current evaluation thread memory usage.
	 * 
	 * @return current memory usage
	 */
	private long getCurrentMemory() {
		if ((maxMemory > 0) && (memoryCounter != null)) {
			return memoryCounter.getThreadAllocatedBytes(threadToMonitor.getId());
		}
		return 0L;
	}

	private long getCPUTime() {
		if ((maxCPUTime > 0) && (threadBean != null)) {
			return threadBean.getThreadCpuTime(threadToMonitor.getId());
		} else {
			return 0L;
		}
	}

	public void stopMonitor() {
		stop.set(true);
		notifyMonitorThread();
	}

	public void setThreadToMonitor(final Thread t) {
		reset();
		threadToMonitor = t;
		notifyMonitorThread();
	}

	public void scriptFinished() {
		scriptFinished.set(false);
	}

	public boolean isCPULimitExceeded() {
		return cpuLimitExceeded.get();
	}

	public boolean isMemoryLimitExceeded() {
		return memoryLimitExceeded.get();
	}

	public boolean isScriptKilled() {
		return scriptKilled.get();
	}

	private void notifyMonitorThread() {
		synchronized (monitor) {
			monitor.notifyAll();
		}
	}

}
