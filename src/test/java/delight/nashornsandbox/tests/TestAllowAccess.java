package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.io.File;
import org.junit.Test;

@SuppressWarnings("all")
public class TestAllowAccess {
  @Test
  public void test_file() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.allow(File.class);
    sandbox.eval("var File = Java.type(\"java.io.File\"); File;");
  }
}
