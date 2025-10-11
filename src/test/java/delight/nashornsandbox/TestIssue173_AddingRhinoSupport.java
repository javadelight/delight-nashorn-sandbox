import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;
import delight.nashornsandbox.internal.RhinoSandboxImpl;

@SuppressWarnings("all")
public class TestIssue173_AddingRhinoSupport {

	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
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
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_evil_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
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
			sandbox.eval(js);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_nice_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
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

	@Test
	public void test_only_while() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "while (true);\n";
			sandbox.eval(badScript);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_only_while_allowed_bad_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		sandbox.setMaxCPUTime(50);

		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "while (true);\n";
			sandbox.eval(badScript);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_only_while_good_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String goodScript = "while (true); {i=1;}";
			sandbox.eval(goodScript);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_while_plus_iteration_bad_script() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String badScirpt = "var x=0;\nwhile (true) x++;\n";
			sandbox.eval(badScirpt);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_while_plus_iteration_bad_scrip_allowed() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String badScirpt = "var x=0;\nwhile (true) x++;\n";
			sandbox.eval(badScirpt);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_while_plus_iteration() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String goodScript = "var x=0;\nwhile (true) {x++;}\n";
			sandbox.eval(goodScript);
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_do_while() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("do {\n");
			_builder.append("\t\n");
			_builder.append("} while (true);\n");
			sandbox.eval(_builder.toString());
		} catch (ScriptCPUAbuseException e) {
			assertFalse(e.isScriptKilled());
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

			NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
			sandbox.setMaxCPUTime(100); // in millis
			sandbox.setMaxMemory(1000 * 1000); // 1 MB
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
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
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

	private static final int MEMORY_LIMIT = 700 * 1024 * 20;

	@Test
	public void test_memory() throws ScriptCPUAbuseException, ScriptMemoryAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxMemory(MEMORY_LIMIT);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String js = "var o={},i=0; while (true) {o[i++] = 'abc'}";
			sandbox.eval(js);
			fail("Exception should be thrown");
		} catch (ScriptMemoryAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_noexpectedbraces_memory() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxMemory(MEMORY_LIMIT);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String js = "var o={},i=0; while (true) o[i++] = 'abc'";
			sandbox.eval(js);
			fail("Exception should be thrown");
		} catch (ScriptMemoryAbuseException e) {
			assertFalse(e.isScriptKilled());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_no_abuse_memory() throws ScriptCPUAbuseException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		try {
			sandbox.setMaxMemory(MEMORY_LIMIT);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final String js = "var o={},i=0; while(i<10) {o[i++] = 'abc';}";
			sandbox.eval(js);
		} catch (final Exception e) {
			throw new RuntimeException("No exception should be thrown", e);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_issue_86_continued_use_memory() throws Exception {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		sandbox.setMaxMemory(1024 * 8000);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String js = "var o={},i=0; while (i < 10000) {o[i++] = 'abc'}";

			for (int i = 1; i <= 1000; i++) {
				javax.script.Bindings bindings = sandbox.createBindings();
				bindings.put("a", "b");
				sandbox.eval(js, bindings);
			}
		} catch (final ScriptMemoryAbuseException e) {
			fail();
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_simple() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		final Object res = sandbox.eval("var x = 1 + 1; x;");
		Assert.assertEquals(Integer.valueOf(2), res);
	}

	@Test
	public void test_file_access() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = new RhinoSandboxImpl(new ScriptEngineManager().getEngineByName("rhino"));
		sandbox.allow(File.class);
		sandbox.eval("var File = Java.type('java.io.File'); File;");
	}

}
