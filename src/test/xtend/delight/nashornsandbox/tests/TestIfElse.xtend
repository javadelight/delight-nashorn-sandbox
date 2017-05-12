package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Assert
import org.junit.Test

class TestIfElse {
	
	@Test
	def void testIfElse() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('''
			if (true)
			var x=1;
			else
			var y=2;		
		''')
		
		Assert.assertEquals(1, sandbox.eval('x'));
		
	}
}