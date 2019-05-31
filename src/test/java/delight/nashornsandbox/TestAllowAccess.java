package delight.nashornsandbox;

import java.io.File;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestAllowAccess {

  @Test
  public void test_file() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.allow(File.class);
    sandbox.eval("var File = Java.type('java.io.File'); File;");
  }

  @Test
  public void test_file_graal() throws ScriptCPUAbuseException, ScriptException {
    final NashornSandbox sandbox = GraalSandboxes.create();
    sandbox.allow(File.class);
    sandbox.eval("var File = Java.type('java.io.File'); File;");
  }
}
