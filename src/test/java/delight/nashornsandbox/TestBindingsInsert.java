package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptException;

import static org.junit.Assert.assertNull;

public class TestBindingsInsert {

    @Test
    @Ignore("This shows that you can override the secure settings")
    public void testOverride() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        assertNull(sandbox.eval("var $ARG=\"a\"; $ARG;"));
    }

    @Test
    //This shows that with the override we will restore to the secure settings so the next script that runs through should be fine
    public void testInsertOptions() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.eval("var $ARG=\"a\";");
        assertNull(sandbox.eval("$ARG;"));
    }

    @Test
    public void testInsertOptionsWithBinding() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        final Bindings bindings = sandbox.createBindings();
        bindings.put("$ARG","a");
        sandbox.eval("$ARG", bindings);
        assertNull(sandbox.eval("$ARG;"));
    }

    @Test
    public void testInsertOptionsWithBinding2() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        final Bindings bindings = sandbox.createBindings();
        bindings.put("$ARG","a");
        sandbox.eval("var $ARG=\"a\";", bindings);
        assertNull(sandbox.eval("$ARG;"));
    }
}
