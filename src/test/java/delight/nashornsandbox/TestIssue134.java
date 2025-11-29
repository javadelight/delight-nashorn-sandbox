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


	@Test(expected = javax.script.ScriptException.class)
	public void test_allowLoadFunctions_false_with_scriptcontext_wrapper() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.allowLoadFunctions(false);
		sandbox.allowExitFunctions(true); 
		sandbox.allowPrintFunctions(true);

		// Simulate user's wrapper pattern: accepting external ScriptContext and wrapping it
		// ScriptContext externalContext = new SimpleScriptContext();
		// SandboxScriptContext sandboxScriptContext = () -> externalContext;
		SandboxScriptContext sandboxScriptContext = sandbox.createScriptContext();

		// This demonstrates the bug: allowLoadFunctions is not respected with wrapped ScriptContext
		sandbox.eval("load({ name: 'test', script: 'print(\"something bad is going to happen\"); exit(1)' });", sandboxScriptContext);
	}

	// @Test(expected = javax.script.ScriptException.class)
	@Test
	public void test_prevent_unsafe_script_context() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.allowLoadFunctions(false);
		sandbox.allowExitFunctions(true); 
		sandbox.allowPrintFunctions(true);

		// Simulate user's wrapper pattern: accepting external ScriptContext and wrapping it
		ScriptContext externalContext = new SimpleScriptContext();
		SandboxScriptContext sandboxScriptContext = () -> externalContext;

		// This demonstrates the bug: allowLoadFunctions is not respected with wrapped ScriptContext
		sandbox.eval("load({ name: 'test', script: 'print(\"should get warning about Script context\")' });", sandboxScriptContext);
	}
}
