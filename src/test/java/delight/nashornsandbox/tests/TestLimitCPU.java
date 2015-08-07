package delight.nashornsandbox.tests;

import delight.async.Operation;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class TestLimitCPU {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setMaxCPUTime(5);
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
  }
  
  @Test
  public void test_evil_script() {
    final Operation<Object> _function = new Operation<Object>() {
      @Override
      public void apply(final ValueCallback<Object> cb) {
        final Runnable _function = new Runnable() {
          @Override
          public void run() {
            final NashornSandbox sandbox = NashornSandboxes.create();
            sandbox.setMaxCPUTime(5);
            StringConcatenation _builder = new StringConcatenation();
            _builder.append("var x = 1;");
            _builder.newLine();
            _builder.append("while (true) { }");
            _builder.newLine();
            sandbox.eval(_builder.toString());
          }
        };
        Thread _thread = new Thread(_function);
        _thread.start();
      }
    };
    Async.<Object>waitFor(_function);
  }
}
