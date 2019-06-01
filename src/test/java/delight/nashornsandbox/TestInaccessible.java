package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestInaccessible {
  @Test(expected = Exception.class)
  public void test_system_exit() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("java.lang.System.exit(0);");
  }
  
  @Test(expected = Exception.class)
  public void test_file() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("var File = Java.type(\"java.io.File\"); File;");
  }
  
}
