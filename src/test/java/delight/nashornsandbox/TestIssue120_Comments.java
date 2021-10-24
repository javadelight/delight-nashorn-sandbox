package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue120_Comments {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    sandbox.allow(Object.class);
    sandbox.allowPrintFunctions(true);
    String js = "\n" + "// ;\n" + "// ;\n" + "// ;\n" + "// ;\n" + "\n" + "var timestamp = new Date().getTime() + '';\n"
        + "print(\"timestamp{}\", timestamp)\n" + "\n" + "var ArrayType = Java.type(\"java.lang.Object[]\");\n"
        + "\n" + "var arr = new ArrayType(3);\n" + "var testList = [{\n" + " name: \"123\"\n" + "}];\n";
    sandbox.eval(js);
    sandbox.getExecutor().shutdown();
  }
}
