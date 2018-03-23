package delight.nashornsandbox;

import javax.script.ScriptException;


import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
public class TestSimpleEval {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    final Object res = sandbox.eval("var x = 1 + 1; x;");
    assertEquals(Integer.valueOf(2), res);
  }
}
