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
    Object _call = ((ScriptObjectMirror) _get).call(this);
    Assert.assertEquals(Integer.valueOf(42), _call);
    Object _eval = sandbox.eval("callMe");
    Object _call_1 = ((ScriptObjectMirror) _eval).call(this);
    Assert.assertEquals(Integer.valueOf(42), _call_1);
  }
}
