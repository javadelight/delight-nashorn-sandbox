package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import java.util.concurrent.Executors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class TestLimitCPU {
  @Test(expected = ScriptCPUAbuseException.class)
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("var x = 1;");
      _builder.newLine();
      _builder.append("while (true) {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("x=x+1;");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
  
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_evil_script() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("var x = 1;");
      _builder.newLine();
      _builder.append("while (true) { }");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
  
  @Test
  public void test_nice_script() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(50);
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("var x = 1;");
    _builder.newLine();
    _builder.append("for (var i=0;i<=1000;i++) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("x = x + i");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    sandbox.eval(_builder.toString());
    sandbox.getExecutor().shutdown();
  }
  
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_only_while() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("while (true);");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
  
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_while_plus_iteration() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("var x=0;");
      _builder.newLine();
      _builder.append("while (true) x++;");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
  
  @Test(expected = ScriptCPUAbuseException.class)
  public void test_do_while() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    try {
      sandbox.setMaxCPUTime(50);
      sandbox.setExecutor(Executors.newSingleThreadExecutor());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("do {");
      _builder.newLine();
      _builder.append("\t");
      _builder.newLine();
      _builder.append("} while (true);");
      _builder.newLine();
      sandbox.eval(_builder.toString());
    } finally {
      sandbox.getExecutor().shutdown();
    }
  }
}
