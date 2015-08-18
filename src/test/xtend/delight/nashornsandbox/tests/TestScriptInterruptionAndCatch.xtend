package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException
import java.util.concurrent.Executors
import org.junit.Test

class TestScriptInterruptionAndCatch {
	
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