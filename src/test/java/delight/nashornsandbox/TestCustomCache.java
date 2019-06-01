package delight.nashornsandbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.function.Supplier;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestCustomCache {
	
	public static class CustomCache implements SecuredJsCache {

		private boolean consulted = false;
		private String produced;
		private String source;

		@Override
		public String getOrCreate(String js, boolean allowNoBraces, Supplier<String> producer) {
			consulted = true;
			source = js;
			produced = producer.get();
			return "void(0);";
		}
	}

	@Test
	public void testCustomCacheUsage() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sb = NashornSandboxes.create();
		final CustomCache cache = new CustomCache();
		sb.setScriptCache(cache);
		Object result = sb.eval("5 ;");

		assertTrue(cache.consulted);
		assertNull(result);
		assertEquals("5 ;", cache.source);

		// In case this fails because of changes to the beautify-routine:
		// The actual value is not that important, this is just
		// a plausibility check to determine whether "producer"
		// has been passed correctly to the cache's getOrCreate()
		// method.
		assertEquals("5;", cache.produced);
	}

	
}
