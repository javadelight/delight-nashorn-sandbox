package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.internal.NashornSandboxImpl;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing possibly problematic default functions")
public class DefaultOperationTests {
    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
    }

    @Nested
    @DisplayName("Exit functions")
    class Exit {
        @Nested
        @DisplayName("Not alowed")
        class NotAllowed {
            @Test
            public void exit() throws ScriptCPUAbuseException, ScriptException {
                sandbox.eval("exit();");
            }

            @Test
            public void quit() throws ScriptCPUAbuseException, ScriptException {
                sandbox.eval("quit();");
            }
        }


        /*

        @Nested
        @DisplayName("Allowed")
        class Allowed{
            @Rule
            public final ExpectedSystemExit exit = ExpectedSystemExit.none();

            @BeforeEach
            void beforeEach(){
                exit.checkAssertionAfterwards(()->{System.out.println("not exited");});
                sandbox.allowExitFunctions(true);
            }
            @Test
            public void exit() throws ScriptCPUAbuseException, ScriptException {
                exit.expectSystemExit();
                sandbox.eval("exit();");
            }

            @Test
            public void quit() throws ScriptCPUAbuseException, ScriptException {
                exit.expectSystemExit();
                sandbox.eval("quit();");
            }
        }*/

    }

    @DisplayName("Writer")
    @Nested
    class Writer {
        final StringWriter writer = new StringWriter();

        @BeforeEach
        void beforeEach() {
            sandbox.allowPrintFunctions(true);
            sandbox.setWriter(writer);
        }

        @Test
        public void print() throws ScriptCPUAbuseException, ScriptException {

            sandbox.eval("print(\"Hi there!\");");
            // \n at the end of the string is not enough.
            // javascript adds an extra carrige return.
            assertEquals("Hi there!\r\n", writer.toString());
        }

    }
}
