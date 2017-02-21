package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.util.concurrent.Executors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class TestSwitch {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.allowPrintFunctions(true);
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
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
