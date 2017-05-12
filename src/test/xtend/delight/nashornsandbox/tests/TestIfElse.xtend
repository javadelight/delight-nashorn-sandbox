package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Assert
import org.junit.Test

class TestIfElse {
	
	@Test
	def void testIfElse() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.maxCPUTime = 500
		sandbox.executor = Executors.newSingleThreadExecutor
		
		sandbox.eval('''
			if (true)
			var x=1;
			else
			var y=2;		
		''')
		
		Assert.assertEquals(1, sandbox.eval('x'))
		
		sandbox.eval('''
		for (var i=0;i<10;i++) {
		    if (false)
		    	var x=3;
		    else
		    	var y=4;	
		}
		''')
		
		Assert.assertEquals(4, sandbox.eval('y'))
		
		sandbox.executor.shutdown()
		
	}
}