package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestExit {
	
	@Test
	def void test() {
		val sandbox = NashornSandboxes.create()

		sandbox.eval("exit();");
		
	    println("hello")
	}
	
}