package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
public class TestKeepVariables {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("var window={};");
    sandbox.eval("window.val1 = \"myval\";");
    final Object res = sandbox.eval("window.val1;");
    assertEquals("myval", res);
  }
}
