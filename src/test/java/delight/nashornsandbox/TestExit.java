package delight.nashornsandbox;

import javax.script.ScriptException;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
public class TestExit {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("exit();");
  }
}
