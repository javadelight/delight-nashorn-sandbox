package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.junit.Test;

@SuppressWarnings("all")
public class TestInaccessible {
  @Test(expected = Exception.class)
  public void test_system_exit() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("java.lang.System.exit(0);");
  }
  
  @Test(expected = Exception.class)
  public void test_file() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("var File = Java.type(\"java.io.File\"); File;");
  }
}
