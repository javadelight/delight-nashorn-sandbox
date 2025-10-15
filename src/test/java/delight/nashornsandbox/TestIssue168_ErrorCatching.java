package delight.nashornsandbox;

import javax.script.ScriptException;
import java.util.concurrent.Executors;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

import static org.junit.Assert.assertEquals;

public class TestIssue168_ErrorCatching {

  public static class IFail {
    public void doIt() {
      throw new Error("I tried my best but I failed");
    }
  }

  @Test(expected = java.lang.Error.class)
  public void test() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      sandbox.inject("iFail", new IFail());
      String code = "iFail.doIt();";
      sandbox.eval(code);
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }	

  @Test(expected = java.lang.Error.class)
  public void test_stack_overflow() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      String code = "function f() { f(); } f()";
      sandbox.eval(code);
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }	

  @Test
  public void test_catch_error() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    Error e = null;
    try {
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      String code = "function f() { f(); } f()";
      try {
        sandbox.eval(code);
      } catch (Error err) {
        e = err;
      }
    } finally {
      sandbox.getExecutor().shutdown();
    }
    assertEquals(StackOverflowError.class, e.getClass());
  }	
}
