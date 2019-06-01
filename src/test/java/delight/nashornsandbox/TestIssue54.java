package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue54 {

	@Test
	public void test_valid() throws ScriptCPUAbuseException, ScriptException {

		String js = "var x = 1;\nwhile (true) { }\n";

		NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(100);
		sandbox.setMaxMemory(1000 * 1000);
		sandbox.allowNoBraces(false);
		sandbox.disallowAllClasses();

		try {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			sandbox.setExecutor(executor);
			sandbox.eval(js);
			sandbox.getExecutor().shutdown();
		} catch (final Exception e) {
			Assert.assertEquals(ScriptCPUAbuseException.class, e.getClass());
		}

	}

}
