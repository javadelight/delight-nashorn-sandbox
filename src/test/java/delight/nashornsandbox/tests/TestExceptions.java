package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.util.concurrent.Executors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestExceptions {
  @Test(expected = Exception.class)
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.eval("blah_blah_blah();");
  }
  
  @Test
  public void test_with_catch() {
    try {
      final NashornSandbox sandbox = NashornSandboxes.create();
      sandbox.eval("blah_blah_blah();");
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable t = (Throwable)_t;
        return;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    Assert.fail("Exception not thrown!");
  }
  
  @Test
  public void test_with_thread() {
    try {
      final NashornSandbox sandbox = NashornSandboxes.create();
      sandbox.setMaxCPUTime(100);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      sandbox.eval("blah_blah_blah();");
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable t = (Throwable)_t;
        return;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    Assert.fail("Exception not thrown!");
  }
  
  @Test
  public void test_with_line_number() {
    NashornSandbox sandbox = null;
    try {
      sandbox = NashornSandboxes.create();
      sandbox.setMaxCPUTime(5000);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("var in_the_first_line_all_good;");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("var so_is_the_second;");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("var and_the_third;");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("blah_blah_blah();");
      sandbox.eval(_builder.toString());
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable t = (Throwable)_t;
        sandbox.getExecutor().shutdown();
        Assert.assertTrue(t.getMessage().contains("4"));
        return;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    Assert.fail("Exception not thrown!");
  }
}
