package delight.nashornsandbox.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for ThreadMonitor.
 */
public class ThreadMonitorTest {

	/**
	 * This is a simple test that verifies that if the thread to monitor is already set, the ThreadMonitor does not
	 * wait unnecessarily when it is run.
	 */
	@Test
	public void when_run_and_threadToMonitor_set_then_do_not_wait() {
		final ThreadMonitor threadMonitor = new ThreadMonitor(1000, 0);
		Thread threadToMonitor = new Thread();
		threadMonitor.registerThreadToMonitor(threadToMonitor);
		threadMonitor.stopMonitor();

		long startTime = System.currentTimeMillis();
		threadMonitor.run();
		Assert.assertTrue(System.currentTimeMillis() - startTime < 500);
	}
}
