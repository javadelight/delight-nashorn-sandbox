package delight.nashornsandbox;

import javax.script.*;

import org.junit.Assert;
import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestEvalWithScriptContextWithNewBindings {
	@Test
	public void testWithNewBindings() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		// Create new binding to override the ECMAScript Global properties
		Bindings newBinding = sandbox.createBindings();
		newBinding.put("Date", "2112018");

		final Object res = sandbox.eval("function method() { return parseInt(Date);} method();", newBinding);
		Assert.assertEquals(2112018.0, res);
	}

	@Test
	public void testWithNewSimpleBindings() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		// Create new binding to override the ECMAScript Global properties
		Bindings newBinding = new SimpleBindings();
		newBinding.put("Date", "2112018");

		final Object res = sandbox.eval("function method() { return parseInt(Date);} method();", newBinding);
		Assert.assertTrue(Double.isNaN((Double)res));
	}

	@Test
	public void testWithNewBindingsScriptContext() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		ScriptContext newContext = new SimpleScriptContext();
		// Create new binding to override the ECMAScript Global properties 
		Bindings newBinding = sandbox.createBindings();
		newBinding.put("Date", "2112018");
		newContext.setBindings(newBinding, ScriptContext.ENGINE_SCOPE);

		final Object res = sandbox.eval("function method() { return parseInt(Date);} method();", newContext);
		Assert.assertEquals(2112018.0, res);
	}

	@Test
	public void testWithExistingBindings() throws ScriptCPUAbuseException, ScriptException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		ScriptContext newContext = new SimpleScriptContext();
		Bindings newBinding = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
		// This will not be updated by using existing bindings, since Date is a 
		// ECMAScript "global" properties and it is being in ENGINE_SCOPE
		newBinding.put("Date", "2112018");

		final Object res = sandbox.eval("function method() { return parseInt(Date);} method();", newContext);
		Assert.assertTrue(Double.isNaN((Double)res));
	}
}
