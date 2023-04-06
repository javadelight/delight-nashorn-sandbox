package delight.nashornsandbox;

import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Ignore;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestExit {
  @Test
  public void testExit() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("exit();");
  }
  
  

  @Test
  public void testQuit() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("quit();");
  }
  
  

  @Test
  public void testQuitWithBindings() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("quit();", sandbox.createBindings());
  }
  
  

  @Test(expected = javax.script.ScriptException.class)
  public void testQuitWithScriptContext() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("quit();", sandbox.createScriptContext());
  }
  
 
}
