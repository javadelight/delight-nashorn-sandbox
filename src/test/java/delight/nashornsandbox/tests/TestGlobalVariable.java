package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.junit.Test;

@SuppressWarnings("all")
public class TestGlobalVariable {
  @Test
  public void test_java_variable() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    Object _object = new Object();
    sandbox.inject("fromJava", _object);
    sandbox.allow(String.class);
    sandbox.allow(Class.class);
    sandbox.eval("fromJava.toString();");
  }
}
