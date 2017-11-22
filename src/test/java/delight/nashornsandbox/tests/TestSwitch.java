package delight.nashornsandbox.tests;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestSwitch {
  @Test
  public void test() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.allowPrintFunctions(true);
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      final StringConcatenation _builder = new StringConcatenation();
      _builder.append("var expr = \"one\";");
      _builder.newLine();
      _builder.newLine();
      _builder.append("switch (expr) {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("case \"one\":");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("// ok");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("break;");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("case \"two\":");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("// ok");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("break;");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("default:");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("print(\"Unknown expression\");");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
}
