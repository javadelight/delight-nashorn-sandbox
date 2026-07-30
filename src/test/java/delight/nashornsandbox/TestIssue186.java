package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue186 {

	@Test
	public void test_eval_return_value_not_null_when_injection_inserts_at_end() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create("--language=es6");
		ExecutorService executor = Executors.newSingleThreadExecutor();
		sandbox.setExecutor(executor);

		try {
			sandbox.setMaxCPUTime(5000);
			sandbox.setMaxMemory(100 * 1024 * 1024);
			sandbox.setMaxPreparedStatements(30);

			Object obj = sandbox.eval(
				"function func() {\n" +
				"    let a = 1;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a+=1;\n" +
				"    return a;\n" +
				"}\n" +
				"func();"
			);

			Assert.assertNotNull("eval should not return null", obj);
			Assert.assertEquals(8, ((Number) obj).intValue());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_eval_return_value_when_extra_statement_avoids_injection_at_end() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create("--language=es6");
		ExecutorService executor = Executors.newSingleThreadExecutor();
		sandbox.setExecutor(executor);

		try {
			sandbox.setMaxCPUTime(5000);
			sandbox.setMaxMemory(100 * 1024 * 1024);
			sandbox.setMaxPreparedStatements(30);

			Object obj = sandbox.eval(
				"function func() {\n" +
				"    let a = 1;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a++;\n" +
				"    a+=1;\n" +
				"    a++;\n" +
				"    return a;\n" +
				"}\n" +
				"func();"
			);

			Assert.assertNotNull("eval should not return null", obj);
			Assert.assertEquals(9, ((Number) obj).intValue());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_eval_return_value_without_limits_works() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create("--language=es6");

		Object obj = sandbox.eval(
			"function func() {\n" +
			"    let a = 1;\n" +
			"    a++;\n" +
			"    a++;\n" +
			"    a++;\n" +
			"    a++;\n" +
			"    a++;\n" +
			"    a++;\n" +
			"    a+=1;\n" +
			"    return a;\n" +
			"}\n" +
			"func();"
		);

		Assert.assertNotNull("eval should not return null", obj);
		Assert.assertEquals(8, ((Number) obj).intValue());
	}
}
