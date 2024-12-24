package delight.nashornsandbox;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestArrayFrom {

	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create("--language=es6");

		String arrayFromPolyfill = "(function() {" +
				"  if (!Array.from) {" +
				"    Array.from = function(source) {" +
				"      if (source == null) {" +
				"        throw new TypeError('Array.from requires an array-like or iterable object.');" +
				"      }" +
				"      var result = [];" +
				"      var length = source.length >>> 0;" +
				"      for (var i = 0; i < length; i++) {" +
				"        if (i in source) {" +
				"          result[i] = source[i];" +
				"        }" +
				"      }" +
				"      return result;" +
				"    };" +
				"  }" +
				"})();";

		sandbox.eval(arrayFromPolyfill);
		final String script = "let array = Array.from(1,2,3);array";
		Object res = sandbox.eval(script);
		assertTrue(res != null);

	}

}
