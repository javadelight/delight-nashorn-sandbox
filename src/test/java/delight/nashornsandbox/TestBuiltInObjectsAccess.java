package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestBuiltInObjectsAccess {

	@Test
	public void test_block_access() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		
		sandbox.eval("exit()");
		sandbox.eval("quit()");
	}

}
