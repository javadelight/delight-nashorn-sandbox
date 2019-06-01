package delight.nashornsandbox;

import java.io.File;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

/**
 * Test for <a href='https://github.com/javadelight/delight-nashorn-sandbox/issues/40'>issue 40</a>.
 * 
 * @author Max Rohde
 *
 */
public class TestIssue40_ScriptEngineParameters {

	@Test(expected=ScriptException.class)
	public void test() throws ScriptCPUAbuseException, ScriptException {
		
		final NashornSandbox sandbox = NashornSandboxes.create("-strict");
	    sandbox.allow(File.class);
	    
	    // should throw an exception since 'Java' is not allowed. 
	    sandbox.eval("idontexist = 1;");
		
	}
	
	

}
