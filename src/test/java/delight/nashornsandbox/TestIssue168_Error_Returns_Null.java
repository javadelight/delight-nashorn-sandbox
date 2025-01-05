
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

  @Test(expected = javax.script.ScriptException.class)
  public void test() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      String code = "throw new Error('Something bad happened!!')";
      sandbox.eval(code);
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }

}