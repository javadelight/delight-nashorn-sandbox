package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestLimitCPU {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		
		
		
		sandbox.eval('java.lang.System.exit(0);')
		
	}
	
}