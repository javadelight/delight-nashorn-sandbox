package delight.nashornsandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import delight.nashornsandbox.exceptions.BracesException;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import junit.framework.Assert;

public class TestIssue34 {

	Logger logger;
	NashornSandbox sandbox;

	public static class Logger {

		List<String> msgs = new ArrayList<String>();

		public void debug(String msg) {
			msgs.add(msg);
		}

		public String getOutput() {
			return Arrays.toString(msgs.toArray());
		}
	}

	@Before
	public void setUp() {
		sandbox = NashornSandboxes.create();
		sandbox.setMaxCPUTime(100); // in millis
		sandbox.setMaxMemory(1000 * 1000); // 1 MB
		sandbox.allowNoBraces(false);
		sandbox.allowPrintFunctions(true);
		sandbox.setMaxPreparedStatements(30);
		sandbox.setExecutor(Executors.newSingleThreadExecutor());
		logger = new Logger();
		sandbox.inject("logger", logger);
	}

	@Test
	public void testIssue34_Scenario1() throws ScriptCPUAbuseException, ScriptException {
		String js = "";
		js += "function main(){\n";
		js += "	for(var i=0;i<2;i++)\n";
		js += "	logger.debug('loop cnt-'+i);\n";
		js += "}\n";
		js += "function main2(){\n";
		js += "}\n";
		js += "main();\n";

		sandbox.eval(js);

		Assert.assertTrue(logger.getOutput().contains("loop cnt-0"));

	}

	@Test
	public void testIssue34_Scenario2() throws ScriptCPUAbuseException, ScriptException {
		String js = "";
		js += "function main(){\n" + "logger.debug(\"... In fun1()....\");\n" + "for(var i=0;i<2;i++)//{\n"
				+ "logger.debug(\"loop cnt-\"+i);\n" + "}\n" + "main();";
		
		
		
		Throwable ex = null;
		try {
			sandbox.eval(js);
		} catch (Throwable t) {
			ex = t;
		}

		Assert.assertTrue(ex instanceof BracesException);

	}
	
	

	@Test
	public void testIssue34_Scenario3() throws ScriptCPUAbuseException, ScriptException {
		String js = "";
		js += "function loopTest(){\n" + "var i=0;\n" + "do{\n" + "logger.debug(\"loop cnt=\"+(++i));\n"
				+ "}while(i<11)\n" + "}\n" + "loopTest();";

		sandbox.eval(js);

		Assert.assertTrue(logger.getOutput().contains("loop cnt=6"));

	}
	
	
	
	@Test
	public void testIssue34_Scenario3_2() throws ScriptCPUAbuseException, ScriptException {
    String js = "//simple do-while loop for demo\n";
		js += "function loopTest(){\n" +
    "var i=0;\n" +
				"do{\n" +
    "logger.debug(\"loop cnt=\"+(++i));\n"
				+ "}while(i<11);" + "}\n" +
    "loopTest();";

		
		sandbox.eval(js);

		Assert.assertTrue(logger.getOutput().contains("loop cnt=6"));

	}
	
	
	
	@Test
	public void testIssue34_Scenario4()  {
		String js = "";
		js += "if(srctable.length) srctable.length = 0;__if();\n" + "else {\n" + "for(var key in srctable) {__if();\n"
				+ "delete srctable[key];\n" + "}\n" + "}";

		Throwable ex = null;
		try {
			sandbox.eval(js);
		} catch (Throwable t) {
			ex = t;
		}

		Assert.assertTrue(ex instanceof IllegalArgumentException);

	}
	
	

	@Test
	public void testIssue34_Scenario5() {
		String js = "";
		js += "function loopTest(){\n" + 
				"var i=0;\n" + 
				"do{\n" + 
				"i++;\n" + 
				"}while(true)\n" + 
				"}\n" + 
				"loopTest();";

		Throwable ex = null;
		try {
			sandbox.eval(js);
		} catch (Throwable t) {
			ex = t;
		}
		
		Assert.assertTrue(ex instanceof ScriptCPUAbuseException);

	}
	
	

	@After
	public void tearDown() {
		sandbox.getExecutor().shutdown();
	}

}
