package delight.nashornsandbox;

import delight.nashornsandbox.internal.NashornSandboxImpl;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;

/**
 * See https://github.com/javadelight/delight-nashorn-sandbox/issues/57
 * 
 * @author Max
 *
 */
@SuppressWarnings("restriction")
public class TestIssue57 {

	@Test(expected = IllegalArgumentException.class)
	public void testMapReduce() throws ScriptException {
		String script = "[1,2,3,4].map(function(n){return n+1}).reduce(function(prev,cur){return prev+cur}, 0)";
		String[] NASHORN_ARGS = { "--no-java", "--no-syntax-extensions" };

		ScriptEngine scriptEngine = new NashornSandboxImpl().createNashornScriptEngineFactory(NASHORN_ARGS);
		Double nashornResult = (Double) scriptEngine.eval(script);
		assertEquals(14, nashornResult.intValue());

		Double sandboxResults = (Double) NashornSandboxes.create(NASHORN_ARGS).eval(script);
		assertEquals(14, sandboxResults.intValue());
	}

}
