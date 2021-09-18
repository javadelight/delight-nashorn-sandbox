package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

	private final int stageOffset;

	private final AtomicBoolean stop;

	/** Check if interrupted script has finished. */
	private final AtomicBoolean scriptFinished;

	/** Check if script should be killed to stop it when abusive. */
	private final AtomicBoolean scriptKilled;

	private final AtomicBoolean cpuLimitExceeded;

	private final AtomicInteger memoryLimitExceededStage;

	private final Object monitor;

	private Thread threadToMonitor;

	private boolean timedOutWaitingForThreadToMonitor = false;

	private ThreadMXBean threadBean;

	private final com.sun.management.ThreadMXBean memoryCounter;

	public ThreadMonitor(final long maxCPUTime, final long maxMemory) {
		this.maxMemory = maxMemory;
		this.maxCPUTime = maxCPUTime * 1000000;
		stop = new AtomicBoolean(false);
		scriptFinished = new AtomicBoolean(false);
		scriptKilled = new AtomicBoolean(false);
		cpuLimitExceeded = new AtomicBoolean(false);
		memoryLimitExceededStage = new AtomicInteger(0);

		monitor = new Object();

		// if maxMemory is larger than 100M, split heap memory count into 4 stage, and offset will be 2
		// 4 stage count will lose accuracy, only works in large allowed heap memory allocation
		// this could fix the mis-killing sub-thread behavior, which happened one or two time during about 3 Mill execution ( with thread reusing ).
		if(maxMemory>1024*1024*100){
			stageOffset=2;
		}else {
			stageOffset=0;
		}
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
		memoryLimitExceededStage.set(0);
		threadToMonitor = null;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			// wait, for threadToMonitor to be set in JS evaluator thread
			synchronized (monitor) {
				if (threadToMonitor == null) {
					monitor.wait((maxCPUTime + 500) / MILI_TO_NANO);
				}
				if (threadToMonitor == null) {
					timedOutWaitingForThreadToMonitor = true;
					throw new IllegalStateException("Executor thread not set after " + maxCPUTime / MILI_TO_NANO + " ms，usually this means the sub-thread not started properly");
				}
			}

			final long startCPUTime = getCPUTime();

			//one stage start
			long stageMemory = getCurrentMemory();

			while (!stop.get()) {
				final long runtime = getCPUTime() - startCPUTime;

				long currentMemory = getCurrentMemory();
				final long memory = currentMemory - stageMemory;

				boolean stageExided = isStageMemoryExided(memory);

				if(stageExided){
					//exceeded once , and record it
					memoryLimitExceededStage.incrementAndGet();
					//start next stage counting
					stageMemory=currentMemory;
				}

				if (isCpuTimeExided(runtime) || isMemoryLimitExceeded()) {

					cpuLimitExceeded.set(isCpuTimeExided(runtime));
					threadToMonitor.interrupt();
					synchronized (monitor) {
						//wait less
						monitor.wait(5);
					}
					if (stop.get()) {
						return;
					}
					if (!scriptFinished.get()) {
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
		//wait less to reduce execution cost
		return Math.min((maxCPUTime - runtime) / MILI_TO_NANO, 5);
	}

	private boolean isCpuTimeExided(final long runtime) {
		if (maxCPUTime == 0) {
			return false;
		}
		return runtime > maxCPUTime;
	}


	private boolean isStageMemoryExided(final long memory) {
		if (maxMemory == 0) {
			return false;
		}
		return memory > (maxMemory>>stageOffset);
	}

	/**
	 * Obtain current evaluation thread memory usage.
	 *
	 * it must be empathises that the method 'getThreadAllocatedBytes' is not strictly accurate
	 * so mis killing might happen.
	 *
	 * @return current memory usage
	 */
	private long getCurrentMemory() throws InterruptedException {
		if ((maxMemory > 0) && (memoryCounter != null)) {
			synchronized (monitor) {
				return memoryCounter.getThreadAllocatedBytes(threadToMonitor.getId());
			}
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
		synchronized (monitor) {
			stop.set(true);
			monitor.notifyAll();
		}
	}

	public boolean registerThreadToMonitor(final Thread t) {
		synchronized (monitor) {
			if (timedOutWaitingForThreadToMonitor) {
				return false;
			}
			reset();
			threadToMonitor = t;
			monitor.notifyAll();
			return true;
		}
	}

	public void scriptFinished() {
		scriptFinished.set(false);
	}

	public boolean isCPULimitExceeded() {
		return cpuLimitExceeded.get();
	}

	public boolean isMemoryLimitExceeded() {
		// return true stage max reached
		return memoryLimitExceededStage.get()>=(1<<stageOffset);
	}

	public boolean isScriptKilled() {
		return scriptKilled.get();
	}

}
