package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

public class TestES6 {

	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create("--language=es6");
		sandbox.setMaxCPUTime(100);
		sandbox.setMaxMemory(1000 * 1000);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		sandbox.setExecutor(executor);
		sandbox.eval("let greetUsers = (users) => {"+
  "  users.forEach((args) => {"+
    "  console.log(`Hello, ${args.name}! You are ${args.age} years old.`);"+
  "});"+
"};");

		sandbox.getExecutor().shutdown();

	}

}
