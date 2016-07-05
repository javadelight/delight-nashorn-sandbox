package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.io.File
import org.junit.Assert
import org.junit.Test

class TestAllowAndDisallowClasses {
    @Test
    def void test_file() {

        val sandbox = NashornSandboxes.create()

        val testClassScript = 'var File = Java.type(\"java.io.File\"); File;'

        sandbox.allow(File)
        sandbox.eval(testClassScript)

        if (!sandbox.isAllowed(File)) {
            Assert.fail("Expected class File is allowed.")
        }

        sandbox.disallow(File)
        try {
            sandbox.eval(testClassScript)
            Assert.fail("When disallow the File class expected a ClassNotFoundException!")
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof java.lang.ClassNotFoundException)
                || e.getCause().getMessage() != "java.io.File") {
                throw e;
            }
        }

        sandbox.allow(File)
        sandbox.eval(testClassScript)

        sandbox.disallowAllClasses()
        try {
            sandbox.eval(testClassScript)
            Assert.fail("When disallow all classes expected a ClassNotFoundException!")
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof java.lang.ClassNotFoundException)
                    || e.getCause().getMessage() != "java.io.File") {
                throw e;
            }
        }
    }
}