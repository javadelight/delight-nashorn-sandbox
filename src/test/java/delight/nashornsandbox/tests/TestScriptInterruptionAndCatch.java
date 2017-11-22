package delight.nashornsandbox.tests;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestScriptInterruptionAndCatch {
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_catch() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      final StringConcatenation _builder = new StringConcatenation();
      _builder.append("try {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("var x = 1;");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("while (true) {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("x=x+1;");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("} catch (e) {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// if this is called, test does not succeed.");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
}
