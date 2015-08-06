package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.io.File
import org.junit.Test

class TestAllowAccess {
	@Test
	def void test_file() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.allow(File)
		
		sandbox.eval('var File = Java.type(\"java.io.File\"); File;')
	}
}