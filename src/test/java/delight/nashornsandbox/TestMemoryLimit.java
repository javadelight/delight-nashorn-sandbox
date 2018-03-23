package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit for testing memory limit.
 * <p>
 * <p>Created on 2017.11.24</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class TestMemoryLimit {
    private static final int MEMORY_LIMIT = 700 * 1024;

    @Test
    public void test() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        try {
            sandbox.setMaxMemory(MEMORY_LIMIT);
            sandbox.setExecutor(Executors.newSingleThreadExecutor());
            final String js = "var o={},i=0; while (true) {o[i++] = 'abc'}";
            sandbox.eval(js);
            fail("Exception should be thrown");
        } catch (final ScriptMemoryAbuseException e) {
            assertFalse(e.isScriptKilled());
        } finally {
            sandbox.getExecutor().shutdown();
        }
    }


    @Test()
    public void test_noexpectedbraces() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxMemory(MEMORY_LIMIT);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        final String js = "var o={},i=0; while (true) o[i++] = 'abc'";
        assertThrows(ScriptCPUAbuseException.class, () -> {
            sandbox.eval(js);
        });
        sandbox.getExecutor().shutdown();

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
        } catch (final ScriptMemoryAbuseException e) {
            assertTrue(e.isScriptKilled());
        } finally {
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
        } catch (final Exception e) {
            fail("No exception should be thrown");
        } finally {
            sandbox.getExecutor().shutdown();
        }
    }

}
