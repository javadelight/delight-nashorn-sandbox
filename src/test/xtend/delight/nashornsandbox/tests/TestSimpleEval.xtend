package delight.nashornsandbox.tests

import org.junit.Test
import delight.nashornsandbox.NashornSandboxes

class TestSimpleEval {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		
		val res = sandbox.eval('var x = 1 + 1; x;')
		
		println(res)
		
	}
	
}