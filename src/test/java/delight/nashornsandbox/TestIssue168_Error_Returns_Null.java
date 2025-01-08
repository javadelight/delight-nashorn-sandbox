
package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

/**
 * <a href=
 * "https://github.com/javadelight/delight-nashorn-sandbox/issues/168">Issue
 * 168</a>
 */
public class TestIssue168_Error_Returns_Null {

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

}