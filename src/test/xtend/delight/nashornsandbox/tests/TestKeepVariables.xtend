package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestKeepVariables {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		
		
		
		sandbox.eval('window.val1 = "myval";')
		
		val res = sandbox.eval('window.val1;')
		
		println(res)
		
	}
	
}