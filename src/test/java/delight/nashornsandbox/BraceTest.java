package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.providers.BracelessScriptProvider;
import delight.nashornsandbox.providers.CpuAbuseBracelessScriptProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Braceless Script Tests")
public class BraceTest {
    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(50);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
    }

    @Test
    @RepeatedTest(CpuAbuseBracelessScriptProvider.testCount)
    @ExtendWith(CpuAbuseBracelessScriptProvider.class)
    @DisplayName("CPU abusing scripts")
    void noBracesAllowed(String js) {
        assertThrows(BracesException.class, () -> sandbox.eval(js));
    }

    @Test
    @RepeatedTest(BracelessScriptProvider.testCount)
    @ExtendWith(BracelessScriptProvider.class)
    @DisplayName("Normal Braceless Scripts")
    void bracelessScripts(String js) {
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(500);
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        assertThrows(BracesException.class, () -> sandbox.eval(js));
        sandbox.getExecutor().shutdown();
    }


    @AfterEach
    void afterEach() {
        sandbox.getExecutor().shutdown();
    }

}
