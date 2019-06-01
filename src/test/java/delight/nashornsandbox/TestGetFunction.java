package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

public class TestGetFunction {
	
	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(100);
		sandbox.setMaxMemory(50 * 1024);
		sandbox.allowNoBraces(false);
		sandbox.setMaxPreparedStatements(30); // because preparing scripts for
		// execution is expensive
		ExecutorService executor = Executors.newSingleThreadExecutor();
		sandbox.setExecutor(executor);
		
		sandbox.eval("function callMe() { return 42; };");
		final Object _get = sandbox.get("callMe");
		
		Assert.assertTrue(_get != null);
		
		executor.shutdown();
	}
	
	
	
}
