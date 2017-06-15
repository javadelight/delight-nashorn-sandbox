package delight.nashornsandbox.tests;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
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
    Assert.assertEquals(Double.valueOf(11.0), sandbox.get("res"));
    sandbox.inject("str", "20");
    sandbox.eval("res = num + str;");
    Assert.assertEquals("1020", sandbox.get("res"));
    final NashornSandbox sandboxInterruption = NashornSandboxes.create();
    sandboxInterruption.setMaxCPUTime(50);
    sandboxInterruption.setExecutor(Executors.newSingleThreadExecutor());
    sandboxInterruption.eval("res = 1;");
    sandboxInterruption.inject("num", Integer.valueOf(10));
    sandboxInterruption.eval("res = num + 1;");
    Assert.assertEquals(Double.valueOf(11.0), sandboxInterruption.get("res"));
    sandboxInterruption.inject("str", "20");
    sandboxInterruption.eval("res = num + str;");
    Assert.assertEquals("1020", sandboxInterruption.get("res"));
  }
}
