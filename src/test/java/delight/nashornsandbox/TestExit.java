package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

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

  @Test
  @Ignore("This will fail as there is no confirmation on Script Contexts")
  public void testQuitWithScriptContext() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("quit();", new SimpleScriptContext());
  }
}
