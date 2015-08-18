package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Test
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException

class TestLimitCPU {

	@Test(expected=ScriptCPUAbuseException)
	def void test() {

		val sandbox = NashornSandboxes.create()
		try {
			sandbox.maxCPUTime = 50
			sandbox.executor = Executors.newSingleThreadExecutor
			sandbox.eval('''
				var x = 1;
				while (true) {
					x=x+1;
				}
			''')
		} finally {
			sandbox.executor.shutdown()
		}

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

	@Test
	def void test_nice_script() {
		val sandbox = NashornSandboxes.create()

		sandbox.maxCPUTime = 50
		sandbox.executor = Executors.newSingleThreadExecutor

		sandbox.eval('''
			var x = 1;
			for (var i=0;i<=1000;i++) {
				x = x + i
			}
		''')
		
		sandbox.executor.shutdown()
	}
	
	@Test(expected=ScriptCPUAbuseException)
	def void test_catch() {

		val sandbox = NashornSandboxes.create()
		try {
			sandbox.maxCPUTime = 50
			sandbox.executor = Executors.newSingleThreadExecutor
			sandbox.eval('''
				try {
					var x = 1;
					while (true) {
						x=x+1;
					}
				} catch (e) {
					// never call me!
				}
			''')
		} finally {
			sandbox.executor.shutdown()
		}

	}

}