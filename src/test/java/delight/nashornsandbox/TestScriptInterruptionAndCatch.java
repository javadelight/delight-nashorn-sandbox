package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

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

}
