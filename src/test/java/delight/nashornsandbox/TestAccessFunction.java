package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class TestAccessFunction {

    private static final Class<?> JDK_NASHORN_ScriptObjectMirror_CLASS;
    private static final Class<?> STANDALONE_NASHORN_ScriptObjectMirror_CLASS;

    static {
        Class<?> tmp_JDK_NASHORN_ScriptObjectMirror_CLASS = null;
        // TODO what behavior do we want here?
        try {
            tmp_JDK_NASHORN_ScriptObjectMirror_CLASS = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
        } catch (ClassNotFoundException e) {
            System.out.println("JDK Nashorn not found");
        }
        JDK_NASHORN_ScriptObjectMirror_CLASS= tmp_JDK_NASHORN_ScriptObjectMirror_CLASS;
        Class<?> tmp_STANDALONE_NASHORN_ScriptObjectMirror_CLASS = null;
        try {
            tmp_STANDALONE_NASHORN_ScriptObjectMirror_CLASS = Class.forName("org.openjdk.nashorn.api.scripting.ScriptObjectMirror");
        } catch (ClassNotFoundException e) {
            System.out.println("Standalone Nashorn not found");
        }
        STANDALONE_NASHORN_ScriptObjectMirror_CLASS = tmp_STANDALONE_NASHORN_ScriptObjectMirror_CLASS;
    }

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
        if (JDK_NASHORN_ScriptObjectMirror_CLASS != null && JDK_NASHORN_ScriptObjectMirror_CLASS.isInstance(_get)) {
            jdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (jdk.nashorn.api.scripting.ScriptObjectMirror) _get;
            return scriptObjectMirror.call(_get);
        }

        if (STANDALONE_NASHORN_ScriptObjectMirror_CLASS != null && STANDALONE_NASHORN_ScriptObjectMirror_CLASS.isInstance(_get)) {
            org.openjdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (org.openjdk.nashorn.api.scripting.ScriptObjectMirror) _get;
            return scriptObjectMirror.call(_get);
        }
        throw new IllegalStateException("Neither JDK nor standalone Nashorn has been found");
    }
}
