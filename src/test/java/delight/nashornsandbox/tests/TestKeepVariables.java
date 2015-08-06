package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Test;

@SuppressWarnings("all")
public class TestKeepVariables {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("var window={};");
    sandbox.eval("window.val1 = \"myval\";");
    final Object res = sandbox.eval("window.val1;");
    InputOutput.<Object>println(res);
  }
}
