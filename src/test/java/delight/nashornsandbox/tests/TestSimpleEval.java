package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Test;

@SuppressWarnings("all")
public class TestSimpleEval {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    final Object res = sandbox.eval("var x = 1 + 1; x;");
    InputOutput.<Object>println(res);
  }
}
