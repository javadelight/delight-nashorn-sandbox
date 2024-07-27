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
	public void test_example_1() throws ScriptCPUAbuseException, ScriptException {
		NashornSandbox sandbox = NashornSandboxes.create();

		String script = "function(data) {\n" +
				"  if (data.get(\"propertyA\") == \"a special value 1\" || data.get(\"propertyA\") == \"a special value 2\") {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyC\") == \"a special value 1\" || data.get(\"propertyJ\") == \"a special value 1\" || data.get(\"propertyV\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"4\" && (data.get(\"propertyD\") == \"a special value 1\" || data.get(\"propertyV\") == \"a special value 1\" || data.get(\"propertyW\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 2\" && (data.get(\"propertyE\") == \"a special value 1\" || data.get(\"propertyF\") == \"a special value 1\" || data.get(\"propertyL\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyE\") == \"a special value 1\" || data.get(\"propertyF\") == \"a special value 1\" || data.get(\"propertyL\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  }  else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else if (data.get(\"propertyB\") == \"a special value 3\" && (data.get(\"propertyM\") == \"a special value 1\" || data.get(\"propertyY\") == \"a special value 1\" || data.get(\"propertyH\") == \"a special value 1\")) {\n"
				+
				"    return \"a special value 1\";\n" +
				"  } else {\n" +
				"     return \"0\"\n" +
				"  };\n" +
				"}\n";

		sandbox.eval(script);
	}

	@Test
	public void test_example_2() throws ScriptCPUAbuseException, ScriptException {
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
