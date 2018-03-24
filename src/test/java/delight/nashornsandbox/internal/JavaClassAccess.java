package delight.nashornsandbox.internal;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Java class access")
public class JavaClassAccess {
    NashornSandbox sandbox;
    private final String testClassScript = "var File = Java.type('java.io.File'); File;";
    final String testDisallowedClassScript = "var List = Java.type('java.util.ArrayList'); List;";
    SandboxClassFilter filter;

    @BeforeEach()
    void beforeEach() {
        filter = spy(SandboxClassFilter.class);
        sandbox = new NashornSandboxImpl(filter);
        sandbox.allow(File.class);
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

    @Nested
    @DisplayName("Injecting variables")
    class Injecting {
        MockObject _object;

        private class MockObject {
            @Override
            public String toString() {
                return "java Object";
            }
        }

        @BeforeEach
        void beforeEach() {
            _object = mock(MockObject.class);
            when(_object.toString()).thenReturn("java Object");
            sandbox.inject("fromJava", _object);
        }

        @Test
        @DisplayName("Injected java objects work properly")
        void injection() throws ScriptException {
            assertEquals("java Object", sandbox.eval("fromJava.toString();"));
        }

        @Test
        @DisplayName("Type of injected variable is added to the filter")
        void typeadded() {
            verify(filter,times(1)).add(_object.getClass());
        }

    }

}
