package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.io.StringWriter
import org.junit.Assert
import org.junit.Test

class TestSetWriter {
	
	@Test
	def void test() {
		
		val sandbox = NashornSandboxes.create()
		sandbox.allowPrintFunctions(true)
		val writer = new StringWriter()

		sandbox.setWriter(writer)

		sandbox.eval('print("Hi there!");')
		
		Assert.assertEquals('Hi there!\n', writer.toString)

	}
	
}