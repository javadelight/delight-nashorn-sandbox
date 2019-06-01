package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

@SuppressWarnings("all")
public class TestKeepVariables {
	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.eval("var window={};");
		sandbox.eval("window.val1 = \"myval\";");
		final Object res = sandbox.eval("window.val1;");
		Assert.assertEquals("myval", res);
	}

}
