package delight.nashornsandbox.tests;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestIfElse {
  @Test
  public void testIfElse() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(500);
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    final StringConcatenation _builder = new StringConcatenation();
    _builder.append("if (true)");
    _builder.newLine();
    _builder.append("var x=1;");
    _builder.newLine();
    _builder.append("else");
    _builder.newLine();
    _builder.append("var y=2;\t\t");
    _builder.newLine();
    sandbox.eval(_builder.toString());
    Assert.assertEquals(Integer.valueOf(1), sandbox.eval("x"));
    final StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("for (var i=0;i<10;i++) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (false)");
    _builder_1.newLine();
    _builder_1.append("    \t");
    _builder_1.append("var x=3;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("else");
    _builder_1.newLine();
    _builder_1.append("    \t");
    _builder_1.append("var y=4;\t");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    sandbox.eval(_builder_1.toString());
    Assert.assertEquals(Integer.valueOf(4), sandbox.eval("y"));
    sandbox.getExecutor().shutdown();
  }
}
