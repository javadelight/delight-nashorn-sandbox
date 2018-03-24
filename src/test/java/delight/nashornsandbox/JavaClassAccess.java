package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Java class access")
public class JavaClassAccess {
    NashornSandbox sandbox;
    private final String testClassScript = "var File = Java.type('java.io.File'); File;";
    final String testDisallowedClassScript = "var List = Java.type('java.util.ArrayList'); List;";

    @BeforeEach()
    void beforeEach() {
        sandbox = NashornSandboxes.create();
        sandbox.allow(File.class);
    }

    @Test
    @DisplayName("Injected java objects work properly")
    void injection() throws ScriptException {
        final Object _object = mock(Object.class);
        when(_object.toString()).thenReturn("java Object");
        sandbox.inject("fromJava", _object);
        sandbox.allow(String.class);
        sandbox.allow(Class.class);
        assertEquals("java Object", sandbox.eval("fromJava.toString();"));
    }

    @Test()
    @DisplayName("Can not call system.exit")
    public void test_system_exit() throws ScriptCPUAbuseException {
        Exception e = assertThrows(RuntimeException.class, () -> {
            sandbox.eval("java.lang.System.exit(0);");
        });
        assertTrue(e.getCause() instanceof ClassNotFoundException);
    }

    @Nested
    @DisplayName("Accessing classes")
    class AccessingClasses {

        @Test
        @DisplayName("Can access allowed class")
        public void canAccess() throws ScriptCPUAbuseException, ScriptException {
            sandbox.eval(testClassScript);
        }

        @Test
        @DisplayName("Can not access disallowed class")
        public void canNotAccess() throws ScriptCPUAbuseException, ScriptException {
            Exception e = assertThrows(RuntimeException.class, () -> sandbox.eval(testDisallowedClassScript));
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        }

    }

    @Nested
    @DisplayName("Can check if a class is allowed")
    class CheckAllowed {

        @Test
        @DisplayName("Returns true if class is allowed")
        void checkAllowedClass() {
            assertTrue(sandbox.isAllowed(File.class));
        }

        @Test
        @DisplayName("Returns false if class is not allowed")
        void checkDisAllowedClass() {
            assertFalse(sandbox.isAllowed(ArrayList.class));
        }
    }

}
