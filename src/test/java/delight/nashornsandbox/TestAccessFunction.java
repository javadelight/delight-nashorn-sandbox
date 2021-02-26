package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.internal.NashornDetection;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class TestAccessFunction {

    @Test
  public void test_access_variable() throws ScriptCPUAbuseException, ScriptException, InvocationTargetException, IllegalAccessException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("function callMe() { return 42; };");
    final Object _get = sandbox.get("callMe");
    Assert.assertEquals(Integer.valueOf(42), findAndCall(_get));
    final Object _eval = sandbox.eval("callMe");
    Assert.assertEquals(Integer.valueOf(42), findAndCall(_get));
  }

    private Object findAndCall(Object _get) {
        if (NashornDetection.isJDKNashornScriptObjectMirror(_get)) {
            jdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (jdk.nashorn.api.scripting.ScriptObjectMirror) _get;
            return scriptObjectMirror.call(_get);
        }

        if (NashornDetection.isStandaloneNashornScriptObjectMirror(_get)) {
            org.openjdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (org.openjdk.nashorn.api.scripting.ScriptObjectMirror) _get;
            return scriptObjectMirror.call(_get);
        }
        throw new IllegalStateException("Neither JDK nor standalone Nashorn has been found");
    }
}
