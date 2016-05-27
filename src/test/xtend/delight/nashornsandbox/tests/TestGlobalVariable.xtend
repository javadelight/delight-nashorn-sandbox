package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestGlobalVariable {
	@Test
	def void test_java_variable() {
		
		val sandbox = NashornSandboxes.create()
				
		sandbox.inject("fromJava", new Object());
		
		sandbox.allow(String)
		sandbox.allow(Class)
		
		sandbox.eval("fromJava.toString();");
	}
}