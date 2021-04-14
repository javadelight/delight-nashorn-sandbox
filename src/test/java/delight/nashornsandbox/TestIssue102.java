
package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue102 {

  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    String function = "function execute() {\n" +
        "var output = {};\n" +
        "var addOutput = function(name, value) {\n" +
        " output[name] = value;\n" +
        "};\n" +
        "a = '\"'\n" +
        "//something for testing\n" +
        "addOutput(\"x\", a);" +
        "return output;\n" +
        "}";
    sandbox.eval(function);
    Invocable invocable = sandbox.getSandboxedInvocable();
    Object result = invocable.invokeFunction("execute");
    Assert.assertNotNull(result);
    sandbox.getExecutor().shutdown();
  }

}