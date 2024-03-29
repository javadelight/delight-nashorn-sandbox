package delight.nashornsandbox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSystemRequirements {

  @Test
  public void test_cpu() {  
    int processors = Runtime.getRuntime().availableProcessors();
    assertTrue("Unit tests likely to fail since they are run on a low-spec machine. Try to run on a machine with at least 2 CPU cores.\n"+
    "Processors detected: "+processors, processors > 1);
  }

  @Test
  public void test_memory() {  
    long maxMemory = Runtime.getRuntime().maxMemory();
    assertTrue("Unit test likely to fail. Run in an environment where the JVM has access to at least 2 GB max memory.\n"+
      "Max heap memory detected "+
      maxMemory + "\n"+
      "Also ensure correct JVM parameters set when running tests, e.g. -Drun.jvmArguments=\"-Xmx4048m -Xms1024m\"",
      maxMemory > 1_320_327_936);
  }
  
}
