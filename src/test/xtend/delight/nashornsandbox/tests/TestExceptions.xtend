package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Assert
import org.junit.Test

class TestExceptions {

	@Test(expected=Exception)
	def void test() {
		val sandbox = NashornSandboxes.create()

		sandbox.eval("blah_blah_blah();");
	}

	@Test
	def void test_with_catch() {
		try {
			val sandbox = NashornSandboxes.create()

			sandbox.eval("blah_blah_blah();");

		} catch (Throwable t) {

			return
		}

		Assert.fail("Exception not thrown!");
	}

	@Test
	def void test_with_thread() {
		try {
			val sandbox = NashornSandboxes.create()
			sandbox.setMaxCPUTime(100);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			sandbox.eval("blah_blah_blah();");

		} catch (Throwable t) {

			return
		}

		Assert.fail("Exception not thrown!");
	}
	@Test
	def void test_with_line_number() {
		var NashornSandbox sandbox
		try {
			sandbox = NashornSandboxes.create()
			
			sandbox.maxCPUTime = 5000
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			sandbox.eval('''var in_the_first_line_all_good;
			var so_is_the_second;
			var and_the_third;
			blah_blah_blah();''');

		} catch (Throwable t) {
			
			sandbox.executor.shutdown()
			
			
			Assert.assertTrue(t.message.contains("4"))
			return

		}

		Assert.fail("Exception not thrown!");
	}

}
