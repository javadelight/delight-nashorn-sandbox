package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
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

		val sandbox = NashornSandboxes.create()

		sandbox.maxCPUTime = 5
		sandbox.executor = Executors.newSingleThreadExecutor
		
		sandbox.eval('''
			var x = 1;
			while (true) { }
		''')
		
		sandbox.executor.shutdown()

	}

}