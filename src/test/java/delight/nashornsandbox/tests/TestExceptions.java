package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
      ExecutorService _newSingleThreadExecutor = Executors.newSingleThreadExecutor();
      sandbox.setExecutor(_newSingleThreadExecutor);
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
}
