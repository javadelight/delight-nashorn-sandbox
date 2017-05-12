package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestIfElse {
  @Test
  public void testIfElse() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    StringConcatenation _builder = new StringConcatenation();
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
  }
}
