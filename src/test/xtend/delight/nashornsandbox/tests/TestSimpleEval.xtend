package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Assert
import org.junit.Test

class TestSimpleEval {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		
		val res = sandbox.eval('var x = 1 + 1; x;')
		
		Assert.assertEquals(2, res)

	}
	
}