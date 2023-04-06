package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue134 {

	@Test(expected = javax.script.ScriptException.class)
	public void test() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();
		SandboxScriptContext context = sandbox.createScriptContext();
		sandbox.eval("load('classpath:TestIssue134.js')", context);
		sandbox.eval("load('somethingwrong')", context);
	}

}
