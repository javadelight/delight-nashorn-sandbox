package delight.nashornsandbox.tests

import delight.async.jre.Async
import delight.nashornsandbox.NashornSandboxes
import org.junit.Test

class TestLimitCPU {

	@Test
	def void test() {

		val sandbox = NashornSandboxes.create()

		sandbox.maxCPUTime = 5

		sandbox.eval('''
			var x = 1;
			while (true) {
				x=x+1;
			}
		''')

	}

	@Test
	def void test_evil_script() {

		Async.waitFor [
			new Thread([
				val sandbox = NashornSandboxes.create()

				sandbox.maxCPUTime = 5

				sandbox.eval('''
					var x = 1;
					while (true) { }
				''')
			])
		]

	}

}