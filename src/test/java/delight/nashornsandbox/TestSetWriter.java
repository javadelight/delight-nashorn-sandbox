package delight.nashornsandbox;

import java.io.StringWriter;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
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
    // javascript adds an extra carrige return.
    Assert.assertEquals("Hi there!\r\n", writer.toString());
  }
}
