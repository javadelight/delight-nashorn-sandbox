package delight.nashornsandbox.internal;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.concurrent.*;

public class TestExecutorThreadNotSet {

    private ThreadPoolExecutor executor;

    @Before
    public void setUp() {
        // We create an executor with a queue
        executor = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()
        );
    }

    @After
    public void tearDown() {
        executor.shutdownNow();
    }

    @Test
    public void executor_not_set_should_prevent_invocation_from_running_indefinitely() throws Exception {
        // We use NashornSandbox to prevent the script from running indefinitely
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(1000); // in millis
        sandbox.setMaxMemory(1000 * 1000 * 10); // 10 MB
        sandbox.allowNoBraces(false);
        sandbox.allowPrintFunctions(true);
        sandbox.setMaxPreparedStatements(30);
        sandbox.setExecutor(executor);

        // This is our infinite script
        final String script = "function x(){while(true){}}\n";
        sandbox.eval(script);

        // We simulate a slow down that will prevent the JsEvaluator to register itself in the thread monitor in time
        // The task will stay in the queue until the ThreadMonitor times out waiting for the JsEvaluator to register itself
        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // The invocation will return an error as the JsEvaluator was not set in time
        Invocable invocable = sandbox.getSandboxedInvocable();
        Throwable t = null;
        try {
            invocable.invokeFunction("x");
        } catch (ScriptException se) {
            t = se;
        }
        Assert.assertNotNull(t);
        Assert.assertTrue(t.getMessage().contains("Executor thread not set after"));
        // the ThreadMonitor timed out, we can now let the JsEvaluator execute
        latch.countDown();

        // Since the thread monitor has stopped monitoring the JsEvaluator, we need to make sure the invoke was not performed
        // Before this PR, the script could run indefinitely since it was not monitored anymore

        // Shutdown the executor so we can easily wait for it to finish running all tasks
        executor.shutdown();
        boolean terminatedWithNoWaitingTasks = executor.awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertTrue(terminatedWithNoWaitingTasks);
    }
}
