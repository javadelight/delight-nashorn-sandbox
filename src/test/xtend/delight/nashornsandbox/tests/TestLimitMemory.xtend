package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Ignore
import org.junit.Test

@Ignore
class TestLimitMemory {
	
	@Test
	def void test_array_concat() {
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('new Array(100000000).concat(new Array(100000000));')
		
		
	}
	
	@Test
	def void test_array_create() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('new string[10^10]')
		
	}
	
}