package delight.nashornsandbox.internal;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@SuppressWarnings("all")
public class SaveThread {
  public void test() {
    final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
  }
}
