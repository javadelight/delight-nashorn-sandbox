package delight.nashornsandbox;

import javax.script.ScriptException;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.Test;

public class TestBuiltInObjectsAccess {
	
	
	@Test
	public void test_block_access() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		
		sandbox.eval("exit()");
		sandbox.eval("quit()");
	}
	
	
}
