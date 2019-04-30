package delight.nashornsandbox;

import java.io.File;
import java.util.Objects;

import javax.script.ScriptException;

import org.graalvm.polyglot.PolyglotException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestAllowAndDisallowClasses {

  @Test
  public void test_file() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    final String testClassScript = "var File = Java.type('java.io.File'); File;";
    sandbox.allow(File.class);
    sandbox.eval(testClassScript);
    if (!sandbox.isAllowed(File.class)) {
      Assert.fail("Expected class File is allowed.");
    }
    sandbox.disallow(File.class);
    try {
      sandbox.eval(testClassScript);
      Assert.fail("When disallow the File class expected a ClassNotFoundException!");
    } 
    catch (final RuntimeException e) {
      if (((!(e.getCause() instanceof ClassNotFoundException)) || (!Objects.equals(e.getCause().getMessage(), "java.io.File")))) {
        throw e;
      }
    }
    sandbox.allow(File.class);
    sandbox.eval(testClassScript);
    sandbox.disallowAllClasses();
    try {
      sandbox.eval(testClassScript);
      Assert.fail("When disallow all classes expected a ClassNotFoundException!");
    } catch (final RuntimeException e) {
      if (((!(e.getCause() instanceof ClassNotFoundException)) || (!Objects.equals(e.getCause().getMessage(), "java.io.File")))) {
        throw e;
      }
    }
  }

  @Test
  public void test_file_graal() throws ScriptCPUAbuseException, ScriptException {
    NashornSandbox sandbox = GraalSandboxes.create();
    final String testClassScript = "var File = Java.type('java.io.File'); File;";
    sandbox.allow(File.class);
    sandbox.eval(testClassScript);
    if (!sandbox.isAllowed(File.class)) {
      Assert.fail("Expected class File is allowed.");
    }
    sandbox = GraalSandboxes.create();
    sandbox.disallow(File.class);
    try {
      Object obj = sandbox.eval(testClassScript);
      Assert.fail("When disallow the File class expected a PolyglotException!");
    }
    catch (final javax.script.ScriptException e) {
      if (((!(e.getCause() instanceof PolyglotException)) || (!Objects.equals(e.getCause().getMessage(), "TypeError: Access to host class java.io.File is not allowed or does not exist.")))) {
        throw e;
      }
    }
    sandbox = GraalSandboxes.create();
    sandbox.allow(File.class);
    sandbox.disallowAllClasses();
    try {
      sandbox.eval(testClassScript);
      Assert.fail("When disallow all classes expected a PolyglotException!");
    } catch (final javax.script.ScriptException e) {
      if (((!(e.getCause() instanceof PolyglotException)) || (!Objects.equals(e.getCause().getMessage(), "TypeError: Access to host class java.io.File is not allowed or does not exist.")))) {
        throw e;
      }
    }
  }
}
