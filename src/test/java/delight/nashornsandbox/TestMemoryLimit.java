package delight.nashornsandbox;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.BracesException;
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
    private static final int MEMORY_LIMIT = 700*1024;

    @Test
    public void test() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(MEMORY_LIMIT);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while (true) {o[i++] = 'abc'}";
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
    
    @Test(expected=BracesException.class)
    public void test_noexpectedbraces() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(MEMORY_LIMIT);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while (true) o[i++] = 'abc'";
        sandbox.eval(js);
        fail("Exception should be thrown");
      }
      finally {
        sandbox.getExecutor().shutdown();
      }
    }

    @Test
    public void test_killed() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(MEMORY_LIMIT);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        sandbox.allowNoBraces(true);
        final String js = "var o={},i=0; while (true) o[i++] = 'abc'";
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

    @Test
    public void test_no_abuse() throws ScriptCPUAbuseException, ScriptException {
      final NashornSandbox sandbox = NashornSandboxes.create();
      try {
        sandbox.setMaxMemory(MEMORY_LIMIT);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while(i<10) {o[i++] = 'abc';}";
        sandbox.eval(js);
      }
      catch(final Exception e){
        throw new RuntimeException("No exception should be thrown", e);
      }
      finally {
        sandbox.getExecutor().shutdown();
      }
    }
    
}
