package delight.nashornsandbox.tests;

import com.google.common.base.Objects;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.io.File;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestAllowAndDisallowClasses {
  @Test
  public void test_file() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    final String testClassScript = "var File = Java.type(\"java.io.File\"); File;";
    sandbox.allow(File.class);
    sandbox.eval(testClassScript);
    boolean _isAllowed = sandbox.isAllowed(File.class);
    boolean _not = (!_isAllowed);
    if (_not) {
      Assert.fail("Expected class File is allowed.");
    }
    sandbox.disallow(File.class);
    try {
      sandbox.eval(testClassScript);
      Assert.fail("When disallow the File class expected a ClassNotFoundException!");
    } catch (final Throwable _t) {
      if (_t instanceof RuntimeException) {
        final RuntimeException e = (RuntimeException)_t;
        if (((!(e.getCause() instanceof ClassNotFoundException)) || (!Objects.equal(e.getCause().getMessage(), "java.io.File")))) {
          throw e;
        }
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    sandbox.allow(File.class);
    sandbox.eval(testClassScript);
    sandbox.disallowAllClasses();
    try {
      sandbox.eval(testClassScript);
      Assert.fail("When disallow all classes expected a ClassNotFoundException!");
    } catch (final Throwable _t_1) {
      if (_t_1 instanceof RuntimeException) {
        final RuntimeException e_1 = (RuntimeException)_t_1;
        if (((!(e_1.getCause() instanceof ClassNotFoundException)) || (!Objects.equal(e_1.getCause().getMessage(), "java.io.File")))) {
          throw e_1;
        }
      } else {
        throw Exceptions.sneakyThrow(_t_1);
      }
    }
  }
}
