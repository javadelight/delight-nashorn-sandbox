package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestEvalWithScriptContext {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    ScriptContext newContext1 = new SimpleScriptContext();
    Bindings engineScope1 = newContext1.getBindings(ScriptContext.ENGINE_SCOPE);
    engineScope1.put("y", 2);
    
    ScriptContext newContext2 = new SimpleScriptContext();
    Bindings engineScope2 = newContext2.getBindings(ScriptContext.ENGINE_SCOPE);
    engineScope2.put("y", 4);
    
    final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext1);
    Assert.assertEquals(3.0, res1);
    
    final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext2);
    Assert.assertEquals(5.0, res2);
    
  }
  
  
  @Test
  public void testWithCPUAndMemory() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(100);
    sandbox.setMaxMemory(1000 * 1024);
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    ScriptContext newContext1 = new SimpleScriptContext();
    Bindings engineScope1 = newContext1.getBindings(ScriptContext.ENGINE_SCOPE);
    engineScope1.put("y", 2);
    
    ScriptContext newContext2 = new SimpleScriptContext();
    Bindings engineScope2 = newContext2.getBindings(ScriptContext.ENGINE_SCOPE);
    engineScope2.put("y", 4);
    
    final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext1);
    Assert.assertEquals(3.0, res1);
    
    final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext2);
    Assert.assertEquals(5.0, res2);
    
  }
  
}
