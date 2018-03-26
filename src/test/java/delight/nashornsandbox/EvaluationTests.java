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
class EvaluationTests {

    private NashornSandbox sandbox;

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

        @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
        @ExtendWith(CpuAbuseBracelessScriptProvider.class)
        @DisplayName("No braces are not allowed")
        void noBracesAllowed(String js) {

            assertThrows(BracesException.class, () -> sandbox.eval(js), js);
        }

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
        void functionNotExists() throws ScriptCPUAbuseException {
            assertThrows(ScriptException.class, () -> sandbox.eval("blah_blah_blah();"));
        }

        @Test
        @DisplayName("Message contains the line number")
        void lineNumber() {
            String multilineScript = "var in_the_first_line_all_good;\n" +
                    "\t\t\t" +
                    "var so_is_the_second;\n" +
                    "\t\t\t" +
                    "var and_the_third;\n" +
                    "\t\t\t" +
                    "blah_blah_blah();";

            assertTrue(assertThrows(
                    ScriptException.class,
                    () -> sandbox.eval(multilineScript))
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
            void test_with_thread() {
                assertThrows(ScriptException.class, () -> sandbox.eval("blah_blah_blah();"));
            }

            @AfterEach
            void afterEach() {
                sandbox.getExecutor().shutdown();
            }
        }


    }

    @DisplayName("Evaluating scripts, that should do no harm.")
    @RepeatedTest(NiceScriptProvider.testCount)
    @ExtendWith(NiceScriptProvider.class)
    void test(String js) throws ScriptCPUAbuseException {
        try {
            sandbox.eval(js);
        }catch (Exception e)
        {
            System.out.println(js);
            e.printStackTrace();
        }
    }

    @DisplayName("Evaluating semantically wrong scripts")
    @ExtendWith(InvalidScriptProvider.class)
    @RepeatedTest(InvalidScriptProvider.testCount)
    void badScriptTest(String js) throws ScriptCPUAbuseException {
        assertThrows(ScriptException.class,()->sandbox.eval(js));
    }

    @Test()
    @DisplayName("Scripts can be interrupted")
    void test_catch() throws ScriptCPUAbuseException {
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
            _builder.append("error.call()\n");
            _builder.append("}\n");
            sandbox.eval(_builder.toString());
        });
        sandbox.getExecutor().shutdown();
    }

    //Todo Look deeper into it, it might not work properly.
    @ExtendWith(MemoryAbuseScriptProvider.class)
    @RepeatedTest(MemoryAbuseScriptProvider.testCount)
    @DisplayName("Memory Limitation")
    void test(Pair<String, Integer> input) throws ScriptCPUAbuseException {
        sandbox.setMaxMemory(input.getSecond());
        assertThrows(ScriptMemoryAbuseException.class, () -> sandbox.eval(input.getFirst()));
    }


    @Test
    @DisplayName("Keeps variables between evaluations")
    void testKeepVariables() throws ScriptCPUAbuseException, ScriptException {
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
