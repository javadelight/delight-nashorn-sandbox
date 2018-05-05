package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javax.script.Invocable;
import javax.script.ScriptException;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
@DisplayName("Testing invokables")
public class TestInvocable {

	@Test
	@DisplayName("Invoke function")
	public void testInvokeFunction() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		final String script = "function x(){return 1;}\n";
		sandbox.eval(script);
		Invocable invocable = sandbox.getSandboxedInvocable();
		assertEquals(1, invocable.invokeFunction("x"));
	}

	@Test
	@DisplayName("Invoke method")
	public void testInvokeMethod() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		final String script = "var obj = {n: 1, x:function(arg){return this.n + arg;}};";
		sandbox.eval(script);
		Object thisobj = sandbox.get("obj");
		Invocable invocable = sandbox.getSandboxedInvocable();

		assertEquals(3.0, invocable.invokeMethod(thisobj, "x", 2));
	}
	@Test
	@DisplayName("CPU can not be abused")
	public void testCpuLmitInInvocable() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {
		final NashornSandbox sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(50);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		try {
			final String badScript = "function x(){while (true){};}\n";
			try {
				sandbox.eval(badScript);
			} catch (ScriptCPUAbuseException e) {
				fail("we want to test invokeFunction(), but we failed too early");
			}
			Invocable invocable = sandbox.getSandboxedInvocable();
			try {
				invocable.invokeFunction("x");
				fail("expected an exception for the infinite loop");
			} catch (ScriptException e) {
				assertEquals(ScriptCPUAbuseException.class, e.getCause().getClass());
			}
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

}
