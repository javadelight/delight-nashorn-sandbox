package delight.nashornsandbox.tests;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
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
