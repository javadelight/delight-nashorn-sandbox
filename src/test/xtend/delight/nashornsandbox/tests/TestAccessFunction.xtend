package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.junit.Assert
import org.junit.Test

class TestAccessFunction {
	
	@Test
	def void test_access_variable() {
		val sandbox = NashornSandboxes.create()
		
	    sandbox.eval('function callMe() { return 42; };')
		
		Assert.assertEquals(42, (sandbox.get("callMe") as ScriptObjectMirror).call(this))
		
		Assert.assertEquals(42, (sandbox.eval("callMe") as ScriptObjectMirror).call(this))
		
	}
}