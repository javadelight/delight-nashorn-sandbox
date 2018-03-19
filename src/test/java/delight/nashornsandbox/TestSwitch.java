package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestSwitch {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      sandbox.allowPrintFunctions(true);
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(executor);
      final StringBuilder _builder = new StringBuilder();
      _builder.append("var expr = \"one\";\n\n");
      _builder.append("switch (expr) {\n");
      _builder.append("  ");
      _builder.append("case \"one\":\n");
      _builder.append("    ");
      _builder.append("// ok\n");
      _builder.append("    ");
      _builder.append("break;\n");
      _builder.append("  ");
      _builder.append("case \"two\":\n");
      _builder.append("    ");
      _builder.append("// ok\n");
      _builder.append("    ");
      _builder.append("break;\n");
      _builder.append("  ");
      _builder.append("default:\n");
      _builder.append("    ");
      _builder.append("print(\"Unknown expression\");\n");
      _builder.append("}\n\n");
      sandbox.eval(_builder.toString());
    } finally {
      executor.shutdown();
    }
  }
}
