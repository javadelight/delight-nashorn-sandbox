package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Test

class TestSwitch {

	@Test
	def void test() {
		val sandbox = NashornSandboxes.create()
		try {
			sandbox.allowPrintFunctions(true)
			sandbox.maxCPUTime = 50
			sandbox.executor = Executors.newSingleThreadExecutor
			sandbox.eval('''
				var expr = "one";
				
				switch (expr) {
				  case "one":
				    // ok
				    break;
				  case "two":
				    // ok
				    break;
				  default:
				    print("Unknown expression");
				}
				
			''')
		} finally {
			sandbox.executor.shutdown()
		}

	}

}
