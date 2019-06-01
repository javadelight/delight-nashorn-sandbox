package delight.nashornsandbox;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

public class TestEngine {
	
	@Test(expected = ScriptException.class)
	public void test() throws ScriptCPUAbuseException, ScriptException {

		NashornSandbox sandbox = NashornSandboxes.create();
		
		Assert.assertEquals(null, sandbox.eval("this.engine.factory"));
		
	}
	

	@Test(expected = ScriptException.class)
	public void test_with_delete() throws ScriptCPUAbuseException, ScriptException {
		
		NashornSandbox sandbox = NashornSandboxes.create();

		sandbox.eval("Object.defineProperty(this, 'engine', {});\n" + "Object.defineProperty(this, 'context', {});");
        sandbox.eval("delete this.__noSuchProperty__;");
		sandbox.eval("delete this.engine; this.engine.factory;");
		sandbox.eval("delete this.engine; this.engine.factory.scriptEngine.compile('var File = Java.type(\"java.io.File\"); File;').eval()");

		Assert.assertEquals(null, sandbox.eval("delete this.engine; this.engine.factory;"));

	}

	
}
