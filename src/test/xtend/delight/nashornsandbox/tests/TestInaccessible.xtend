package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestInaccessible {
	
	@Test(expected=Exception)
	def void test_system_exit() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('java.lang.System.exit(0);')
	}
	
	@Test(expected=Exception)
	def void test_file() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('var File = Java.type(\"java.io.File\"); File;')
	}
	
}