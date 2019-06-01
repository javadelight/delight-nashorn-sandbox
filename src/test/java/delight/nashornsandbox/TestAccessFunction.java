package delight.nashornsandbox;

import java.util.function.Function;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

@SuppressWarnings("all")
public class TestAccessFunction {

  @Test
  public void test_access_variable() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("function callMe() { return 42; };");
    final Object _get = sandbox.get("callMe");
    Assert.assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _get).call(this));
    final Object _eval = sandbox.eval("callMe");
    Assert.assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _eval).call(this));
  }

  
}
