package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Test;

@SuppressWarnings("all")
public class TestExit {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("exit();");
    InputOutput.<String>println("hello");
  }
}
