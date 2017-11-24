package delight.nashornsandbox;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;

/**
 * JUnit for testing memory limit.
 *
 * <p>Created on 2017.11.24</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class TestMemoryLimit {
    @Test
    public void test() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(50*1024);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while (true) {o[i] = 'abc'}";
        sandbox.eval(js);
        fail("Exception should be thrown");
      }
      catch(final ScriptMemoryAbuseException e){
        assertFalse(e.isScriptKilled());
      }
      finally {
        sandbox.getExecutor().shutdown();
      }
    }
    
    @Test
    public void test_killed() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(50*1024);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while (true) o[i] = 'abc'";
        sandbox.eval(js);
        fail("Exception should be thrown");
      }
      catch(final ScriptMemoryAbuseException e){
        assertTrue(e.isScriptKilled());
      }
      finally {
        sandbox.getExecutor().shutdown();
      }
    }
    
}
