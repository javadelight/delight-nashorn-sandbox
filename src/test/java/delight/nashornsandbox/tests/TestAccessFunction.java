package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestAccessFunction {
  @Test
  public void test_access_variable() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("function callMe() { return 42; };");
    Object _get = sandbox.get("callMe");
    Assert.assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _get).call(this));
    Object _eval = sandbox.eval("callMe");
    Assert.assertEquals(Integer.valueOf(42), ((ScriptObjectMirror) _eval).call(this));
  }
}
