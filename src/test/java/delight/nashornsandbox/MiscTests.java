package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.internal.RemoveComments;
import delight.nashornsandbox.providers.CommentedProvider;
import delight.nashornsandbox.providers.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.script.ScriptException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Miscellaneous tests")
public class MiscTests {

    @Test
    @RepeatedTest(CommentedProvider.testCount)
    @ExtendWith(CommentedProvider.class)
    @DisplayName("Comments are removed")
    public void test(Pair<String, String> values) {
        assertEquals(values.getFirst(), RemoveComments.perform(values.getSecond()));
    }
    @Test
    @DisplayName("Using many injections and evaluations")
    public void injectionsAndEvaluations() throws ScriptCPUAbuseException, ScriptException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.inject("num", Integer.valueOf(10));
        sandbox.eval("res = num + 1;");
        assertEquals(Double.valueOf(11.0), sandbox.get("res"));
        sandbox.inject("str", "20");
        sandbox.eval("res = num + str;");
        assertEquals("1020", sandbox.get("res"));
        final NashornSandbox sandboxInterruption = NashornSandboxes.create();
        sandboxInterruption.setMaxCPUTime(50);
        sandboxInterruption.setExecutor(Executors.newSingleThreadExecutor());
        sandboxInterruption.eval("res = 1;");
        sandboxInterruption.inject("num", Integer.valueOf(10));
        sandboxInterruption.eval("res = num + 1;");
        assertEquals(Double.valueOf(11.0), sandboxInterruption.get("res"));
        sandboxInterruption.inject("str", "20");
        sandboxInterruption.eval("res = num + str;");
        assertEquals("1020", sandboxInterruption.get("res"));
    }
}
