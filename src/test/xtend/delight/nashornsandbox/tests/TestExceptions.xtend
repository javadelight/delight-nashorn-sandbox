package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Assert
import org.junit.Test

class TestExceptions {
	
	@Test(expected=Exception)
	def void test() {
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval("blah_blah_blah();");
	}
	
	@Test
	def void test_with_catch() {
		try {
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval("blah_blah_blah();");
		
		} catch (Throwable t) {
			
			return
		}
		
		Assert.fail("Exception not thrown!");
	}
	
}