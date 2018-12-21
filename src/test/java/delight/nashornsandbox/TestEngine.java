package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

public class TestEngine {
	
	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		
		NashornSandbox sandbox = NashornSandboxes.create();
		
		Assert.assertEquals(null, sandbox.eval("this.engine"));
		
	}
	
}
