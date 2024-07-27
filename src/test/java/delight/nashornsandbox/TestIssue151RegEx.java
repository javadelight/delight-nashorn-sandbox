package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class TestIssue151RegEx {

	@Test
	public void test() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();
		SandboxScriptContext context = sandbox.createScriptContext();

		String jsonString = "var variable = {"
				+ "0:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345'  },"
				+ "1:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "2:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "3:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "4:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "5:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "6:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "7:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "8:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "9:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "10:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "11:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "12:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "13:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "14:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "15:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "16:{ status: 'ABCDEF12345', statusName: 'ABCDEF12345' },"
				+ "17:{ status: 'ABCDEF', statusName: 'ABCDEF' },"
				+ "18:{ status: 'ABCDEF', statusName: 'ABCDEF' },"
				+ "19:{ status: 'ABCDEF', statusName: 'ABCDE' }"
				+ "};"
				+ "//This is a Comment"
				+ "return msg;";

		sandbox.eval(jsonString, context);

	}

}
