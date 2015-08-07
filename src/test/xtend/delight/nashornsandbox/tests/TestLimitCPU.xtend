package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Test
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException

class TestLimitCPU {

	@Test(expected=ScriptCPUAbuseException)
	def void test() {
		
		val sandbox = NashornSandboxes.create()

		sandbox.maxCPUTime = 50

		sandbox.eval('''
			var x = 1;
			while (true) {
				x=x+1;
			}
		''')

	}

	@Test(expected=ScriptCPUAbuseException)
	def void test_evil_script() {
		val sandbox = NashornSandboxes.create()
		try {

			sandbox.maxCPUTime = 50
			sandbox.executor = Executors.newSingleThreadExecutor

			sandbox.eval('''
				var x = 1;
				while (true) { }
			''')
		} finally {
			sandbox.executor.shutdown()
		}
	}

}