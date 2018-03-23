package delight.nashornsandbox;

import javax.script.ScriptException;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
public class TestAccessFunction {
  @Test
  public void test_access_variable() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("function callMe() { return 42; };");
    final Object _get = sandbox.get("callMe");
    assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _get).call(this));
    final Object _eval = sandbox.eval("callMe");
    assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _eval).call(this));
  }
}
