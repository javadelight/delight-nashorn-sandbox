package delight.nashornsandbox;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Asserts that there are enough resources available to prevent flaky tests.
 * 
 * For more information, see <a href="https://github.com/javadelight/delight-nashorn-sandbox/issues/137">#137</a>
 */
public class TestSystemRequirements {

  @Test
  public void test_cpu() {  
    int processors = Runtime.getRuntime().availableProcessors();
    assertTrue("Unit tests likely to fail since they are run on a low-spec machine. Try to run on a machine with at least 2 CPU cores", processors > 1);
  }

  @Test
  public void test_memory() {  
    long maxMemory = Runtime.getRuntime().maxMemory();
    assertTrue("Unit test likely to fail. Run in an environment where the JVM has access to at least 2 GB max memory.\n"+
      "Max heap memory detected "+
      maxMemory, maxMemory > 1_320_327_936);
  }
  
}
