package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
@DisplayName("Error on evaluating code starting with \"do\"")
public class TestIssue36 {

    @Test
    public void test() throws ScriptCPUAbuseException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(100);
        sandbox.setMaxMemory(1000 * 1000);
        sandbox.allowNoBraces(false);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        sandbox.setExecutor(executor);
        Boolean done = (Boolean) sandbox.eval("done = false;");
        assertFalse(done);

        sandbox.getExecutor().shutdown();

    }

}
