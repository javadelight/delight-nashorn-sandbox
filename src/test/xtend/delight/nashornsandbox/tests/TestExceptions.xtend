package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestExceptions {
	
	@Test(expected=Exception)
	def void test() {
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval("blah_blah_blah();");
	}
	
}