package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.exceptions.ScriptMemoryAbuseException;
import delight.nashornsandbox.providers.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.script.ScriptException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Evaluation Tests")
public class EvaluationTests {

    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
    }

    @Nested
    @DisplayName("Script Validation")
    class Validation {
        @BeforeEach
        void beforeEach() {
            sandbox.allowNoBraces(false);
        }

        @Test
        @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
        @ExtendWith(CpuAbuseBracelessScriptProvider.class)
        @DisplayName("No braces are not allowed")
        void noBracesAllowed(String js) {

            assertThrows(BracesException.class, () -> sandbox.eval(js), js);
        }

        @Test
        @RepeatedTest(BracelessScriptProvider.testCount)
        @ExtendWith(BracelessScriptProvider.class)
        @DisplayName("Normal Braceless Scripts")
        void bracelessScripts(String js) {
            assertThrows(BracesException.class, () -> sandbox.eval(js), js);
        }
    }

    @Nested
    @DisplayName("CPU Limitations")
    class CPU {
        @BeforeEach
        void beforeEach() {
            sandbox.setMaxCPUTime(50);
        }

        @Nested
        @DisplayName("Braceless Javascript")
        class EvilScript {

            @Test
            @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
            @ExtendWith(CpuAbuseBracelessScriptProvider.class)
            @DisplayName("Braces Allowed")
            void bracesAllowed(String js) {
                sandbox.allowNoBraces(true);
                assertThrows(ScriptCPUAbuseException.class, () -> sandbox.eval(js), js);
            }
        }

        @Nested
        @DisplayName("Correct Javascript")
        class NiceScript {

            @Test
            @RepeatedTest(NiceCPUAbuseScriptProvider.testCount)
            @ExtendWith(NiceCPUAbuseScriptProvider.class)
            @DisplayName("Normal javascript")
            void cpuAbusingNiceScripts(String js) {
                assertThrows(ScriptCPUAbuseException.class, () -> sandbox.eval(js), js);
            }
        }
    }

    @Nested
    @DisplayName("Evaluating semantically wrong javascript")
    class SemanticError {
        @Test()
        @DisplayName("Throws ScriptException")
        public void functionNotExists() throws ScriptCPUAbuseException {
            assertThrows(ScriptException.class, () -> sandbox.eval("blah_blah_blah();"));
        }

        @Test
        @DisplayName("Message contains the line number")
        void lineNumber() {
            final StringBuilder _builder = new StringBuilder();
            _builder.append("var in_the_first_line_all_good;\n");
            _builder.append("\t\t\t");
            _builder.append("var so_is_the_second;\n");
            _builder.append("\t\t\t");
            _builder.append("var and_the_third;\n");
            _builder.append("\t\t\t");
            _builder.append("blah_blah_blah();");
            String multilineScript = _builder.toString();

            assertTrue(assertThrows(
                    ScriptException.class,
                    () -> {
                        sandbox.eval(multilineScript);
                    })
                    .getMessage()
                    .contains("4"));
        }

        @Nested
        @DisplayName("While running in a thread")
        class Thread {
            @BeforeEach
            void beforeEach() {
                sandbox.setExecutor(Executors.newSingleThreadExecutor());
                sandbox.setMaxCPUTime(5000);
            }

            @Test
            @DisplayName("Throws ScriptException ")
            public void test_with_thread() {
                assertThrows(ScriptException.class, () -> sandbox.eval("blah_blah_blah();"));
            }

            @AfterEach
            void afterEach() {
                sandbox.getExecutor().shutdown();
            }
        }


    }

    @Test
    @DisplayName("Simple Evaluation")
    public void test() throws ScriptCPUAbuseException, ScriptException {
        final Object res = sandbox.eval("var x = 1 + 1; x;");
        assertEquals(2, res);
    }

    @Test()
    @DisplayName("Scripts can be interrupted")
    public void test_catch() throws ScriptCPUAbuseException {
        //note:
        //throws nullpointer if fails
        assertThrows(ScriptCPUAbuseException.class, () -> {
            sandbox.setMaxCPUTime(50);
            final StringBuilder _builder = new StringBuilder();
            sandbox.inject("error", null);
            _builder.append("try {\n");
            _builder.append("\t");
            _builder.append("var x = 1;\n");
            _builder.append("\t");
            _builder.append("while (true) {\n");
            _builder.append("\t\t");
            _builder.append("x=x+1;\n");
            _builder.append("\t");
            _builder.append("}\n");
            _builder.append("} catch (e) {\n");
            _builder.append("\t");
            //"error" is null, will fail the test if accessed.
            _builder.append("//error.call()\n");
            _builder.append("}\n");
            sandbox.eval(_builder.toString());
        });
        sandbox.getExecutor().shutdown();
    }

    //prepared for the using of different test scripts
    @ExtendWith(MemoryAbuseScriptProvider.class)
    //@RepeatedTest(MemoryAbuseScriptProvider.testCount)
    @DisplayName("Memory Limitation")
    public void test(Pair<String, Integer> input) throws ScriptCPUAbuseException {
        sandbox.setMaxMemory(input.getSecond());
        assertThrows(ScriptMemoryAbuseException.class, () -> sandbox.eval(input.getFirst()));
    }

    @Test
    @DisplayName("Error on evaluating code starting with \"do\"")
    public void startsWithDo() throws ScriptCPUAbuseException, ScriptException {
        Boolean done = (Boolean) sandbox.eval("done = false;");
        assertFalse(done);
    }

    @Test
    @DisplayName("Keeps variables between evaluations")
    public void testKeepVariables() throws ScriptCPUAbuseException, ScriptException {
        sandbox.eval("var window={};");
        sandbox.eval("window.val1 = \"myval\";");
        final Object res = sandbox.eval("window.val1;");
        assertEquals("myval", res);
    }

    @AfterEach
    void afterEach() {
        sandbox.getExecutor().shutdown();
    }
}
