package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class TestScriptInterruptionAndCatch {
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_catch() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      ExecutorService _newSingleThreadExecutor = Executors.newSingleThreadExecutor();
      sandbox.setExecutor(_newSingleThreadExecutor);
      StringConcatenation _builder = new StringConcatenation();
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
      ExecutorService _executor = sandbox.getExecutor();
      _executor.shutdown();
    }
  }
}
