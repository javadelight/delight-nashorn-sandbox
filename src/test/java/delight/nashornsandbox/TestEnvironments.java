package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test evaluation Environments")
public class TestEnvironments {
    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
    }

    @Nested
    @DisplayName("Testing Contexts")
    class Contexts {
        ScriptContext newContext1;
        ScriptContext newContext2;

        @Nested
        @DisplayName("Altering the engine scope")
        class EngineScope {
            Bindings engineScope2;
            Bindings engineScope1;

            @BeforeEach
            void beforeEach() {
                newContext1 = new SimpleScriptContext();
                newContext2 = new SimpleScriptContext();
                engineScope1 = newContext1.getBindings(ScriptContext.ENGINE_SCOPE);
                engineScope2 = newContext2.getBindings(ScriptContext.ENGINE_SCOPE);
            }

            @Test
            @DisplayName("Code produces different results with different context variables")
            void evaluate() throws ScriptException {
                engineScope1.put("y", 2);
                engineScope2.put("y", 4);

                final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext1);
                assertEquals(3.0, res1);

                final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", newContext2);
                assertEquals(5.0, res2);
            }

            @Test
            @DisplayName("Executing on dfferent contexts does not interfere with each other")
            void evaluateCode() throws ScriptException {

                sandbox.eval("function cal() {var x = 1; return x;}", newContext1);
                sandbox.eval("function cal() {var x = 2; return x;}", newContext2);
                final Object res1 = sandbox.eval("cal();", newContext1);
                assertEquals(1, res1);

                final Object res2 = sandbox.eval("cal();", newContext2);
                assertEquals(2, res2);
            }

            @Test
            @DisplayName("CPU and memory limitations do not interfere")
            public void testWithCPUAndMemory() throws ScriptCPUAbuseException, ScriptException {
                sandbox.setMaxCPUTime(100);
                sandbox.setMaxMemory(1000 * 1024);
                sandbox.setExecutor(Executors.newSingleThreadExecutor());

                sandbox.eval("function cal() {var x = 1; return x;}", newContext1);
                sandbox.eval("function cal() {var x = 2; return x;}", newContext2);

                final Object res1 = sandbox.eval("cal();", newContext1);
                assertEquals(1, res1);

                final Object res2 = sandbox.eval("cal();", newContext2);
                assertEquals(2, res2);
                sandbox.getExecutor().shutdown();
            }
        }

    }

    @Nested
    @DisplayName("When we only include new bindings for the execution")
    class Binding {
        Bindings engineScope2;
        Bindings engineScope1;

        @BeforeEach
        void beforeEach() {
            engineScope1 = sandbox.createBindings();
            engineScope2 = sandbox.createBindings();
        }

        @Test
        @DisplayName("Code produces different results with different context variables")
        void evaluate() throws ScriptException {
            engineScope1.put("y", 2);
            engineScope2.put("y", 4);

            final Object res1 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", engineScope1);
            assertEquals(3.0, res1);

            final Object res2 = sandbox.eval("function cal() {var x = y + 1; return x;} cal();", engineScope2);
            assertEquals(5.0, res2);
        }

        @Test
        @DisplayName("Executing on dfferent contexts does not interfere with each other")
        void evaluateCode() throws ScriptException {

            sandbox.eval("function cal() {var x = 1; return x;}", engineScope1);
            sandbox.eval("function cal() {var x = 2; return x;}", engineScope2);
            final Object res1 = sandbox.eval("cal();", engineScope1);
            assertEquals(1, res1);

            final Object res2 = sandbox.eval("cal();", engineScope2);
            assertEquals(2, res2);
        }

        @Test
        @DisplayName("CPU and memory limitations do not interfere")
        public void testWithCPUAndMemory() throws ScriptCPUAbuseException, ScriptException {
            sandbox.setMaxCPUTime(100);
            sandbox.setMaxMemory(1000 * 1024);
            sandbox.setExecutor(Executors.newSingleThreadExecutor());

            sandbox.eval("function cal() {var x = 1; return x;}", engineScope1);
            sandbox.eval("function cal() {var x = 2; return x;}", engineScope2);

            final Object res1 = sandbox.eval("cal();", engineScope1);
            assertEquals(1, res1);

            final Object res2 = sandbox.eval("cal();", engineScope2);
            assertEquals(2, res2);
            sandbox.getExecutor().shutdown();
        }
    }
}

