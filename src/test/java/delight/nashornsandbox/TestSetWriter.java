package delight.nashornsandbox;

import java.io.StringWriter;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestSetWriter {
	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.allowPrintFunctions(true);
		final StringWriter writer = new StringWriter();
		sandbox.setWriter(writer);
		sandbox.eval("print(\"Hi there!\");");
		// \n at the end of the string is not enough.
		// JavaScript adds an extra carriage return.
		Assert.assertTrue("Hi there!\r\n".equals(writer.toString()) || "Hi there!\n".equals(writer.toString()) || "Hi there! \n".equals(writer.toString()));
	}

}
