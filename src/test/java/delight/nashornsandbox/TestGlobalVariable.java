package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestGlobalVariable {
  @Test
  public void test_java_variable() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    final Object _object = new Object();
    sandbox.inject("fromJava", _object);
    sandbox.allow(String.class);
    sandbox.allow(Class.class);
    sandbox.eval("fromJava.toString();");
  }
  
  
}
