package delight.nashornsandbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

@SuppressWarnings("all")
public class TestLimitCPU {

	@Test(expected = ScriptCPUAbuseException.class)
	public void test() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("var x = 1;\n");
			_builder.append("while (true) {\n");
			_builder.append("\t");
			_builder.append("x=x+1;\n");
			_builder.append("}\n");
			sandbox.eval(_builder.toString());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_evil_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		final String js = "var x = 1;\nwhile (true) { }\n";
		try {
			// try evaluate bad js
			try {
				sandbox.eval(js);
				fail("Should exception be thrown");
			} catch (final BracesException e) {
				// nothing to do
			}
			// allow evaluate bad js
			sandbox.allowNoBraces(true);
			sandbox.eval(js);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_nice_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(500);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		final StringBuilder _builder = new StringBuilder();
		_builder.append("var x = 1;\n");
		_builder.append("for (var i=0;i<=1000;i++) {\n");
		_builder.append("\t");
		_builder.append("x = x + i\n");
		_builder.append("}\n");
		sandbox.eval(_builder.toString());
		sandbox.getExecutor().shutdown();
	}

	@Test(expected = BracesException.class)
	public void test_only_while() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "while (true);\n";
			sandbox.eval(badScript);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_only_while_allowed_bad_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);

		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "while (true);\n";
			sandbox.allowNoBraces(true);
			sandbox.eval(badScript);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_only_while_good_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String goodScript = "while (true); {i=1;}";
			sandbox.allowNoBraces(true);
			sandbox.eval(goodScript);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = BracesException.class)
	public void test_while_plus_iteration_bad_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String badScirpt = "var x=0;\nwhile (true) x++;\n";
			sandbox.eval(badScirpt);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_while_plus_iteration_bad_scrip_allowed() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String badScirpt = "var x=0;\nwhile (true) x++;\n";
			sandbox.allowNoBraces(true);
			sandbox.eval(badScirpt);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_while_plus_iteration() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String goodScript = "var x=0;\nwhile (true) {x++;}\n";
			sandbox.eval(goodScript);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_do_while() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("do {\n");
			_builder.append("\t\n");
			_builder.append("} while (true);\n");
			sandbox.eval(_builder.toString());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	/**
	 * See issue <a href=
	 * 'https://github.com/javadelight/delight-nashorn-sandbox/issues/30'>#30</a>
	 */
	@Test
	public void testIsMatchCpuAbuseDirect() {
		String js = "while (true) {};";
		for (int i = 0; i < 20; i++) {

			NashornSandbox sandbox = NashornSandboxes.create();
			sandbox.setMaxCPUTime(100); // in millis
			sandbox.setMaxMemory(1000 * 1000); // 1 MB
			sandbox.allowNoBraces(false);
			sandbox.setMaxPreparedStatements(30);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			Exception exp = null;
			try {

				Object result = sandbox.eval(js);

			} catch (Exception e) {

				exp = e;
			}
			sandbox.getExecutor().shutdown();
			Assert.assertNotNull(exp);
		}
	}

	@Test
	public void testCpuLmitInInvocable() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "function x(){while (true){};}\n";
			try {
				sandbox.eval(badScript);
			} catch (ScriptCPUAbuseException e) {
				fail("we want to test invokeFunction(), but we failed too early");
			}
			Invocable invocable = sandbox.getSandboxedInvocable();
			try {
				invocable.invokeFunction("x");
				fail("expected an exception for the infinite loop");
			} catch (ScriptException e) {
				assertEquals(ScriptCPUAbuseException.class, e.getCause().getClass());
			}
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

}
