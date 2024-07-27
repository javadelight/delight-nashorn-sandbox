package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import com.mifmif.common.regex.Generex;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue117 {

  public static String getMatchStr(String regex, int repl) {
    Generex generex = new Generex(regex);

    String randomStr = generex.random();
    StringBuffer sb = new StringBuffer();
    sb.append(randomStr);
    sb.append(";\n");
    for (int i = 0; i < repl; i++) {
      sb.append("/");
    }

    return sb.toString();
  }

  @Test(expected = ScriptCPUAbuseException.class)
  public void test() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();

    for (int i = 4800; i <= 4820; i++) {
      long startTime = System.currentTimeMillis();
      String js_script = getMatchStr("(([^;]+;){9}[^;]+)", i);
      try {
        sandbox.eval(js_script);
      } catch (Exception e) {
        throw e;
      }
      long endTime = System.currentTimeMillis();
      long costTime = endTime - startTime;
      Assert.assertTrue("RegEx attack successful. Script ran for longer 120000 ms. Time required: "+costTime, costTime <= 120000); 
    }
  }
}
