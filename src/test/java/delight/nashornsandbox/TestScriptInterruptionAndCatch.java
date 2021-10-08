package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;
import org.junit.Test;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public class TestScriptInterruptionAndCatch {
	@Test(expected = ScriptCPUAbuseException.class)
	public void test_catch() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("try {\n");
			_builder.append("\t");
			_builder.append("var x = 1;\n");
			_builder.append("\t");
			_builder.append("while (true) {\n");
			_builder.append("\t\t");
			_builder.append("x=x+1;\n");
			_builder.append("\t");
			_builder.append("}\n");
			_builder.append("} catch (e) {\n");
			_builder.append("\t");
			_builder.append("// if this is called, test does not succeed.\n");
			_builder.append("}\n");
			sandbox.eval(_builder.toString());
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	@Test(expected = ScriptCPUAbuseException.class)
	public void test_catch_compiled() throws ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		try
		{
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("try {\n");
			_builder.append("\t");
			_builder.append("var x = 1;\n");
			_builder.append("\t");
			_builder.append("while (true) {\n");
			_builder.append("\t\t");
			_builder.append("x=x+1;\n");
			_builder.append("\t");
			_builder.append("}\n");
			_builder.append("} catch (e) {\n");
			_builder.append("\t");
			_builder.append("// if this is called, test does not succeed.\n");
			_builder.append("}\n");
			CompiledScript compiled = sandbox.compile(_builder.toString());
			sandbox.eval(compiled);
		}
		finally
		{
			sandbox.getExecutor().shutdown();
		}
	}

	@Test
	public void test_engine_working_after_crash() throws ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		CompiledScript validScript = null;
		validScript = sandbox.compile("1+1;");
		try {
			sandbox.setMaxCPUTime(50);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			final StringBuilder _builder = new StringBuilder();
			_builder.append("try {\n");
			_builder.append("\t");
			_builder.append("var x = 1;\n");
			_builder.append("\t");
			_builder.append("while (true) {\n");
			_builder.append("\t\t");
			_builder.append("x=x+1;\n");
			_builder.append("\t");
			_builder.append("}\n");
			_builder.append("} catch (e) {\n");
			_builder.append("\t");
			_builder.append("// if this is called, test does not succeed.\n");
			_builder.append("}\n");
			CompiledScript compiled = sandbox.compile(_builder.toString());
			sandbox.eval(compiled);
		} catch (Exception e) {
			Assert.assertEquals(ScriptCPUAbuseException.class, e.getClass());
		} finally {
			sandbox.getExecutor().shutdown();
		}
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		Assert.assertEquals(2, validScript.eval());
	}
}
