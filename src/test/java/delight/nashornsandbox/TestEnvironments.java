package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test evaluation Environments")
public class TestEnvironments {
    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
    }

    class Contexts {
    }

    class Binding {



    }

    @Test
    public void test() throws ScriptCPUAbuseException, ScriptException {
        ScriptContext newContext1 = new SimpleScriptContext();
        Bindings engineScope1 = newContext1.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope1.put("y", 2);

        ScriptContext newContext2 = new SimpleScriptContext();
        Bindings engineScope2 = newContext2.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope2.put("y", 4);

        final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext1);
        assertEquals(3.0, res1);

        final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext2);
        assertEquals(5.0, res2);
    }
}
