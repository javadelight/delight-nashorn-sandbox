package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.providers.BracelessScriptProvider;
import delight.nashornsandbox.providers.CpuAbuseBracelessScriptProvider;
import delight.nashornsandbox.providers.NiceCPUAbuseScriptProvider;
import delight.nashornsandbox.providers.NiceScriptProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
@DisplayName("Testing CPU abusing")
public class TestLimitCPU {

    @Nested
    @DisplayName("Braceless CPU abusing scripts")
    class EvilScript {
        NashornSandbox sandbox;

        @BeforeEach
        void beforeEach() {
            sandbox = NashornSandboxes.create();
            sandbox.setMaxCPUTime(50);
            sandbox.setExecutor(Executors.newSingleThreadExecutor());
        }

        //I should have the Extendswith on the class
        // but I could not get it working properly
        @Test
        @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
        @ExtendWith(CpuAbuseBracelessScriptProvider.class)
        @DisplayName("Throws BracesException")
        void noBraces(String js) {
            assertThrows(BracesException.class, () -> {
                sandbox.eval(js);
            });
        }

        @Test
        @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
        @ExtendWith(CpuAbuseBracelessScriptProvider.class)
        @DisplayName("Throws ScriptCPUAbuseException")
        void noBracesAllowed(String js) {
            sandbox.allowNoBraces(true);
            assertThrows(ScriptCPUAbuseException.class, () -> {
                sandbox.eval(js);
            });
        }

        @AfterEach
        void afterEach() {
            sandbox.getExecutor().shutdown();
        }
    }

    @Test
    @RepeatedTest(NiceScriptProvider.testCount)
    @ExtendWith(NiceScriptProvider.class)
    @DisplayName("Nice scripts throw nothing")
    void noBraces(String js) throws ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(500);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        sandbox.eval(js);
    }


    @Test
    @RepeatedTest(NiceCPUAbuseScriptProvider.testCount)
    @ExtendWith(NiceCPUAbuseScriptProvider.class)
    @DisplayName("Nice scripts throw CPU Abuse Exception ")
    void cpuAbusingNiceScripts(String js) throws ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(500);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        assertThrows(ScriptCPUAbuseException.class, () -> {
            sandbox.eval(js);
        });
        sandbox.getExecutor().shutdown();
    }

    @Test
    @RepeatedTest(BracelessScriptProvider.testCount)
    @ExtendWith(BracelessScriptProvider.class)
    @DisplayName("Testing Braceless scripts")
    void bracelessScripts(String js) throws ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(500);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        assertThrows(BracesException.class, () -> {
            sandbox.eval(js);
        });
        sandbox.getExecutor().shutdown();
    }

    @Test
    @RepeatedTest(BracelessScriptProvider.testCount)
    @ExtendWith(BracelessScriptProvider.class)
    @DisplayName("Testing Braceless scripts, braceless Script is allowed")
    void bracelessScriptsAllowe(String js) throws ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(500);
        sandbox.allowNoBraces(true);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        assertThrows(ScriptCPUAbuseException.class, () -> {
            sandbox.eval(js);
        });
        sandbox.getExecutor().shutdown();
    }

    //if all the CPU abuse tests pass than this one should not matter.
    /**
     * See issue <a href=
     * 'https://github.com/javadelight/delight-nashorn-sandbox/issues/30'>#30</a>
     */
    @Test
    public void testIsMatchCpuAbuseDirect() {
        String js = "while (true) {};";
        for (int i = 0; i < 20; i++) {

            NashornSandbox sandbox = NashornSandboxes.create();
            sandbox.setMaxCPUTime(100); // in millis
            sandbox.setMaxMemory(1000 * 1000); // 1 MB
            sandbox.allowNoBraces(false);
            sandbox.setMaxPreparedStatements(30);
            sandbox.setExecutor(Executors.newSingleThreadExecutor());
            Exception exp = null;
            try {

                Object result = sandbox.eval(js);

            } catch (Exception e) {

                exp = e;
            }
            sandbox.getExecutor().shutdown();
            assertNotNull(exp);
        }
    }

    @Test
    public void testCpuLmitInInvocable() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {
        final NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(50);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        try {
            final String badScript = "function x(){while (true){};}\n";
            try {
                sandbox.eval(badScript);
            } catch (ScriptCPUAbuseException e) {
                fail("we want to test invokeFunction(), but we failed too early");
            }
            Invocable invocable = sandbox.getSandboxedInvocable();
            try {
                invocable.invokeFunction("x");
                fail("expected an exception for the infinite loop");
            } catch (ScriptException e) {
                assertEquals(ScriptCPUAbuseException.class, e.getCause().getClass());
            }
        } finally {
            sandbox.getExecutor().shutdown();
        }
    }
}
