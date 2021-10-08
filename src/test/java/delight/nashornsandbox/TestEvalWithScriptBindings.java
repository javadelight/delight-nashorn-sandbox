package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestEvalWithScriptBindings {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    Bindings binding1 = sandbox.createBindings();
    binding1.put("y", 2);

    Bindings binding2 = sandbox.createBindings();
    binding2.put("y", 4);
    
    final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", binding1);
    Assert.assertTrue(res1.equals(Double.valueOf("3.0")) || res1.equals(new Integer(3)));
    
    final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", binding2);
    Assert.assertTrue(res2.equals(Double.valueOf("5.0")) || res2.equals(new Integer(5)) );
    
  }

  @Test
  public void test_compiled() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    Bindings binding1 = sandbox.createBindings();
    binding1.put("y", 2);

    Bindings binding2 = sandbox.createBindings();
    binding2.put("y", 4);

    final Object res1 = sandbox.eval(sandbox.compile("function cal() {var x = y + 1; return x;} cal();"), binding1);
    Assert.assertTrue(res1.equals(Double.valueOf("3.0")) || res1.equals(new Integer(3)));

    final Object res2 = sandbox.eval(sandbox.compile("function cal() {var x = y + 1; return x;} cal();"), binding2);
    Assert.assertTrue(res2.equals(Double.valueOf("5.0")) || res2.equals(new Integer(5)) );

  }

  @Test
  public void testWithCPUAndMemory() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(100);
    sandbox.setMaxMemory(1000 * 1024);
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    Bindings binding1 = sandbox.createBindings();
    binding1.put("y", 2);

    Bindings binding2 = sandbox.createBindings();
    binding2.put("y", 4);

    final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", binding1);
    Assert.assertTrue(res1.equals(Double.valueOf("3.0")) || res1.equals(new Integer(3)));

    final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", binding2);
    Assert.assertTrue(res2.equals(Double.valueOf("5.0")) || res2.equals(new Integer(5)));
    
  }
  
  
}
