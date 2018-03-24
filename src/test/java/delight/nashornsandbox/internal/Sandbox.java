package delight.nashornsandbox.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

//Currently only tests, what is not covered by other unit tests
@DisplayName("SandBoxTests")
public class Sandbox {

    NashornSandboxImpl sandbox;
    SandboxClassFilter filter;

    @BeforeEach
    void beforeEach() {
        filter = spy(SandboxClassFilter.class);
        sandbox = new NashornSandboxImpl(filter);
    }

    @Nested
    @DisplayName("Before evaluation")
    class BeforeEval {
        @Nested
        @DisplayName("Setting properties to true")
        class SettingToTrue {
            @Test
            void allowReadFunctions() {
                sandbox.allowReadFunctions(true);
                assertTrue(sandbox.allowReadFunctions);
            }

            @Test
            void allowLoadFunctions() {
                sandbox.allowLoadFunctions(true);
                assertTrue(sandbox.allowLoadFunctions);
            }

            @Test
            void allowExitFunctions() {
                sandbox.allowExitFunctions(true);
                assertTrue(sandbox.allowExitFunctions);
            }

            @Test
            void allowGlobalsObjects() {
                sandbox.allowGlobalsObjects(true);
                assertTrue(sandbox.allowGlobalsObjects);
            }

            @Test
            void allowNoBraces() {
                sandbox.allowNoBraces(true);
                assertTrue(sandbox.allowNoBraces);
            }

            @Test
            void allowPrintFunctions() {
                sandbox.allowPrintFunctions(true);
                assertTrue(sandbox.allowPrintFunctions);
            }
        }

        @Nested
        @DisplayName("Setting properties to false")
        class SettingToFalse {
            @Test
            void allowReadFunctions() {
                sandbox.allowReadFunctions(false);
                assertFalse(sandbox.allowReadFunctions);
            }

            @Test
            void allowLoadFunctions() {
                sandbox.allowLoadFunctions(false);
                assertFalse(sandbox.allowReadFunctions);
            }

            @Test
            void allowExitFunctions() {
                sandbox.allowExitFunctions(false);
                assertFalse(sandbox.allowExitFunctions);
            }

            @Test
            void allowGlobalsObjects() {
                sandbox.allowGlobalsObjects(false);
                assertFalse(sandbox.allowExitFunctions);
            }

            @Test
            void allowNoBraces() {
                sandbox.allowNoBraces(false);
                assertFalse(sandbox.allowNoBraces);
            }

            @Test
            void allowPrintFunctions() {
                sandbox.allowPrintFunctions(false);
                assertFalse(sandbox.allowPrintFunctions);
            }

        }

    }

    @Nested
    @DisplayName("After evaluation, nothing can be changed")
    class AfterEval {
        @BeforeEach
        void beforeEach() throws ScriptException {
            sandbox.eval("");
        }

        @Nested
        @DisplayName("Setting properties to true")
        class SettingToTrue {
            @Test
            void allowReadFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowReadFunctions(true));
            }

            @Test
            void allowLoadFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowLoadFunctions(true));
            }

            @Test
            void allowExitFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowExitFunctions(true));
            }

            @Test
            void allowGlobalsObjects() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowGlobalsObjects(true));
            }

            @Test
            void allowNoBraces() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowNoBraces(true));
            }

            @Test
            void allowPrintFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowPrintFunctions(true));
            }
        }

        @Nested
        @DisplayName("Setting properties to false")
        class SettingToFalse {
            @Test
            void allowReadFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowReadFunctions(false));
            }

            @Test
            void allowLoadFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowLoadFunctions(false));
            }

            @Test
            void allowExitFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowExitFunctions(false));
            }

            @Test
            void allowGlobalsObjects() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowGlobalsObjects(false));
            }

            @Test
            void allowNoBraces() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowNoBraces(false));
            }

            @Test
            void allowPrintFunctions() {
                assertThrows(IllegalStateException.class, () -> sandbox.allowPrintFunctions(false));
            }

        }

    }

    @Nested
    @DisplayName("Class Filter Operations")
    class ClassFilter {

        @Test
        @DisplayName("Allowing classes")
        void allow() {
            sandbox.allow(String.class);
            verify(filter, times(1)).add(String.class);
        }

        @Test
        @DisplayName("Disallowing classes")
        void disallow() {
            sandbox.disallow(String.class);
            verify(filter, times(1)).remove(String.class);
        }

    }

}
