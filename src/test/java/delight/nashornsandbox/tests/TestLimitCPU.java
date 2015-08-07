package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class TestLimitCPU {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(5);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("var x = 1;");
    _builder.newLine();
    _builder.append("while (true) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("x=x+1;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    sandbox.eval(_builder.toString());
  }
  
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_evil_script() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(500);
    ExecutorService _newSingleThreadExecutor = Executors.newSingleThreadExecutor();
    sandbox.setExecutor(_newSingleThreadExecutor);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("var x = 1;");
    _builder.newLine();
    _builder.append("while (true) { }");
    _builder.newLine();
    sandbox.eval(_builder.toString());
    ExecutorService _executor = sandbox.getExecutor();
    _executor.shutdown();
  }
}
