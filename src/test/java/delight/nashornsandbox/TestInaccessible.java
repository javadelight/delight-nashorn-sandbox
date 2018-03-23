package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("all")
public class TestInaccessible {
    @Test()
    public void test_system_exit() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        assertThrows(Exception.class, () -> {
            sandbox.eval("java.lang.System.exit(0);");
        });

    }

    @Test()
    public void test_file() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        assertThrows(Exception.class, () -> {

            sandbox.eval("var File = Java.type(\"java.io.File\"); File;");
        });
    }
}
