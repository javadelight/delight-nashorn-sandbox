package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    assertEquals("Hi there!\r\n", writer.toString());
  }
}
