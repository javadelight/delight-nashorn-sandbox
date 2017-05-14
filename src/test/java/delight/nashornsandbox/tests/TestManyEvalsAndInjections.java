package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestManyEvalsAndInjections {
  @Test
  public void test() {
    final NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.inject("num", Integer.valueOf(10));
    sandbox.eval("res = num + 1;");
    Object _get = sandbox.get("res");
    Assert.assertEquals(Double.valueOf(11.0), _get);
    sandbox.inject("str", "20");
    sandbox.eval("res = num + str;");
    Object _get_1 = sandbox.get("res");
    Assert.assertEquals("1020", _get_1);
    final NashornSandbox sandboxInterruption = NashornSandboxes.create();
    sandboxInterruption.setMaxCPUTime(50);
    ExecutorService _newSingleThreadExecutor = Executors.newSingleThreadExecutor();
    sandboxInterruption.setExecutor(_newSingleThreadExecutor);
    sandboxInterruption.eval("res = 1;");
    sandboxInterruption.inject("num", Integer.valueOf(10));
    sandboxInterruption.eval("res = num + 1;");
    Object _get_2 = sandboxInterruption.get("res");
    Assert.assertEquals(Double.valueOf(11.0), _get_2);
    sandboxInterruption.inject("str", "20");
    sandboxInterruption.eval("res = num + str;");
    Object _get_3 = sandboxInterruption.get("res");
    Assert.assertEquals("1020", _get_3);
  }
}
