package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestSimpleEval {
	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		final Object res = sandbox.eval("var x = 1 + 1; x;");
		Assert.assertEquals(Integer.valueOf(2), res);
	}

}
