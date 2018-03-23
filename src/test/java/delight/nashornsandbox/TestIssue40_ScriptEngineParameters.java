package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for <a href='https://github.com/javadelight/delight-nashorn-sandbox/issues/40'>issue 40</a>.
 *
 * @author Max Rohde
 */
public class TestIssue40_ScriptEngineParameters {

    @Test()
    public void test() throws ScriptCPUAbuseException, ScriptException {

        final NashornSandbox sandbox = NashornSandboxes.create("--no-java");
        sandbox.allow(File.class);

        // should throw an exception since 'Java' is not allowed.
        assertThrows(Exception.class, () -> {
            sandbox.eval("var File = Java.type('java.io.File'); File;");
        });

    }

}
