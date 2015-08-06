package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Assert
import org.junit.Test

class TestKeepVariables {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		
		sandbox.eval('var window={};')		
		sandbox.eval('window.val1 = "myval";')
		
		val res = sandbox.eval('window.val1;')
		
		Assert.assertEquals("myval", res)
		
	}
	
}