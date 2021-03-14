package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue47 {

	@Test
	public void test_valid() throws ScriptCPUAbuseException, ScriptException {

		String js = "function preProcessor()\n" + "{\n" + "var map =  { \"inputparam\": \" for \" };\n" + "}\n"
				+ "preProcessor();";

		NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(100);
		sandbox.setMaxMemory(2000 * 1000);
		sandbox.allowNoBraces(false);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		sandbox.setExecutor(executor);
		sandbox.eval(js);

		sandbox.getExecutor().shutdown();

	}

	@Test(expected=BracesException.class)
	public void test_invalid() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = null;
		try {
			String js = "function preProcessor()\n" + "{\n" + "var map =  { \"inputparam\": \"l\" }; for (;;); \n"
					+ "}\n" + "preProcessor();";

			sandbox = NashornSandboxes.create();
			sandbox.setMaxCPUTime(100);
			sandbox.setMaxMemory(1000 * 1000);
			sandbox.allowNoBraces(false);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			sandbox.setExecutor(executor);
			sandbox.eval(js);
		} finally {
			sandbox.getExecutor().shutdown();
		}

	}

}
