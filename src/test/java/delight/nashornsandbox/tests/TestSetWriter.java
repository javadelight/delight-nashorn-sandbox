package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestSetWriter {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.allowPrintFunctions(true);
    final StringWriter writer = new StringWriter();
    sandbox.setWriter(writer);
    sandbox.eval("print(\"Hi there!\");");
    Assert.assertEquals("Hi there!\n", writer.toString());
  }
}
