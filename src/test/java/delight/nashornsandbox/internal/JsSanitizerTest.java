package delight.nashornsandbox.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.BracesException;

/**
 * JUnit test for {@link JsSanitizer}.
 *
 * <p>
 * Created on 2017.11.24
 * </p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class JsSanitizerTest {
	private JsSanitizer jsSanitizer;
	private final ScriptEngine scriptEngine;

	public JsSanitizerTest() {
		scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	@Before
	public void setUp() {
		jsSanitizer = new JsSanitizer(scriptEngine, 0, false);
	}

	@Test
	public void testSecureJs() throws Exception {
		final String js1 = jsSanitizer.secureJs("while(a > 0)\n\n\n {\n\n\nprint(a);}");
		assertTrue(js1.contains("while (a > 0) {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js2 = jsSanitizer.secureJs("while(a > 0) {print(a);}");
		assertTrue(js2.contains("while (a > 0) {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js3 = jsSanitizer.secureJs("do { c=1;} while(a > 0)");
		assertTrue(js3.contains("do {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js4 = jsSanitizer.secureJs("function a() { b++;}");
		assertTrue(js4.contains("function a() {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js5 = jsSanitizer.secureJs("var a = function() { b++;}");
		assertTrue(js5.contains("var a = function() {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js6 = jsSanitizer.secureJs("(function() { b++;})();");
		assertTrue(js6.contains("function() {" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js7 = jsSanitizer.secureJs("switch(expression) { case 'ABC': i++;break;}");
		assertTrue(js7.contains("switch (expression) {\n case 'ABC':"));
	}

	@Test
	public void testSecureJs_10statment() throws Exception {
		final String js1 = "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n"
				+ "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=10;\nvar i=11;";
		final String bjs1 = jsSanitizer.secureJs(js1);
		assertTrue(bjs1.contains("var i = 10;\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js2 = "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n"
				+ "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n" + "for(var i=0; i<10; i++) {i--}" + "var i=10;\nvar i=11;";
		final String bjs2 = jsSanitizer.secureJs(js2);
		assertTrue(bjs2.contains("var i = 10;\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
	}

	@Test
	public void testSecureJs_10statment_break() throws Exception {
		final String js1 = "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n"
				+ "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=10;\nvar i=11;";
		final String bjs1 = jsSanitizer.secureJs(js1);
		assertTrue(bjs1.contains("var i = 10;\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js2 = "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n"
				+ "var i=0;\nvar i=0;\nvar i=0;\nvar i=0;\n" + "for(var i=0; i<10; i++) {break; continue; i--}\n"
				+ "var i=10;\nvar i=11;";
		final String bjs2 = jsSanitizer.secureJs(js2);
		assertFalse(bjs2.contains("break;" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		assertFalse(bjs2.contains("continue;" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
	}

	@Test
	public void testSecureJs_10statment_elseif_comments() throws Exception {
		final String js1 = "function FindProxyForURL(url, host)\n{\nvar i=1;\nvar i=2;\nvar i=3;\n"
				+ "var i=4;\nvar i=5;\nvar i=6;\nvar i=7;\nif (dnsDomainIs(host, \"proxy8.com.net\"))\n"
				+ "return \"PROXY http://proxy8.acme.com:8080\";\nelse if  (dnsDomainIs(host, \"acme1.com.net\"))\n"
				+ "return \"PROXY http://proxy9.acme.com:8080\";\nelse if (dnsDomainIs(host, \"initech.acme.com\"))\n"
				+ "// would break if inserted interrupt function\nreturn \"PROXY http://acme10.com:8080\";\n"
				+ "else if (dnsDomainIs(host, \"whymper.net\"))\nreturn \"PROXY http://acme2.com:8080\";\n"
				+ "else if (dnsDomainIs(host, \"enough.acme.com\"))\nreturn \"PROXY http://one.proxy.acme.com:8080\";\n"
				+ "else\nreturn \"DIRECT\";\n}"
		;
		final String bjs = jsSanitizer.secureJs(js1);
		assertFalse(bjs.contains("\"PROXY http://acme10.com:8080\";\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		assertTrue(bjs.contains("\"DIRECT\";\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js12 = "function FindProxyForURL(url, host)\n{\nvar i=1;\nvar i=2;\nvar i=3;\n"
				+ "var i=4;\nvar i=5;\nvar i=6;\nvar i=7;\nif (dnsDomainIs(host, \"proxy8.com.net\"))\n"
				+ "return \"PROXY http://proxy8.acme.com:8080\";\nelse if  (dnsDomainIs(host, \"acme1.com.net\"))\n"
				+ "return \"PROXY http://proxy9.acme.com:8080\";\nelse if (dnsDomainIs(host, \"skynet.acme.com\"))\n"
				+ "     // would break if inserted interrupt function\n     return \"PROXY http://acme10.com:8080\";\n"
				+ "else if (dnsDomainIs(host, \"whymper.net\"))\nreturn \"PROXY http://acme2.com:8080\";\n"
				+ "else if (dnsDomainIs(host, \"enough.acme.com\"))\nreturn \"PROXY http://one.proxy.acme.com:8080\";\n"
				+ "else\nreturn \"DIRECT\";\n}"
		;
		final String bjs2 = jsSanitizer.secureJs(js12);
		assertFalse(bjs2.contains("\"PROXY http://acme10.com:8080\";\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		assertTrue(bjs2.contains("\"DIRECT\";\n" + JsSanitizer.JS_INTERRUPTED_FUNCTION));
	}

	@Test
	public void testSecureJs_simple() throws Exception {
		final String js1 = "sum(a) + avg(b)";
		assertEquals(js1, jsSanitizer.secureJs(js1));
		final String js2 = "while(true) {sum(a) + avg(b);}";
		assertFalse(js2.equals(jsSanitizer.secureJs(js2)));
	}

	@Test
	public void testCheckBracess() throws Exception {
		badBracesTest("while(true) a = 1;");
		badBracesTest("while(true) ;");
		badBracesTest("while(true)");
		badBracesTest("while(true) \n a=1;");
		badBracesTest(";while(true)");
		wellBracesTest("{a = 1;}while(true)");
		wellBracesTest("while(true) {");
		wellBracesTest("} while(true);");
		wellBracesTest("while(true) {}");

		badBracesTest("for(int i=1; i<10; i++) a = 1;");
		badBracesTest("for(int i=1; i<10; i++) ;");
		badBracesTest("for(int i=1; i<10; i++)");
		wellBracesTest("for(int i=1; i<10; i++) {");
		wellBracesTest("for(;;){}");

		badBracesTest("do a=1; while(true");
		wellBracesTest("a = 'you must do it yourself';");
		wellBracesTest("do {a=1;} while(true)");
		wellBracesTest("do {} while(true)");

		// badBracesTest("function f() a++;");
		// badBracesTest("var f = function(p1, p2) a++");
		wellBracesTest("function a() {");
		wellBracesTest("var f = function(p1) {");
	}

	@Test
	public void test_issue_66_case1() {
		String script = "var t1 = \"(function)\";\n" + "var person={\n" + "    \"name\": \"test\"\n" + "};\n"
				+ "print(\"t1\" + person.name);";

		NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(6000);
			sandbox.setMaxMemory(50 * 1024 * 1024L);
			sandbox.allowNoBraces(true);
			sandbox.allowExitFunctions(true);
			sandbox.allowGlobalsObjects(true);
			sandbox.allowLoadFunctions(true);
			sandbox.allowPrintFunctions(true);
			sandbox.allowReadFunctions(true);
			sandbox.setMaxPreparedStatements(10000);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			sandbox.eval(script);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}
	
	@Test
	public void test_issue_66_case2() {
		String script = "var name = 'n'; "+"function a() {\n" + 
				"}\n" + 
				"switch (name) {\n" + 
				"    case \"s\":\n" + 
				"    case \"n\":\n" + 
				"}";

		NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(6000);
			sandbox.setMaxMemory(50 * 1024 * 1024L);
			sandbox.allowNoBraces(true);
			sandbox.allowExitFunctions(true);
			sandbox.allowGlobalsObjects(true);
			sandbox.allowLoadFunctions(true);
			sandbox.allowPrintFunctions(true);
			sandbox.allowReadFunctions(true);
			sandbox.setMaxPreparedStatements(10000);
			sandbox.setExecutor(Executors.newSingleThreadExecutor());
			sandbox.eval(script);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		} finally {
			sandbox.getExecutor().shutdown();
		}
	}

	private void badBracesTest(final String js) {
		try {
			jsSanitizer.checkBraces(jsSanitizer.beautifyJs(js));
			fail("Should be exception");
		} catch (final BracesException e) {
			// nothing to do
		}
	}

	private void wellBracesTest(final String js) {
		try {
			jsSanitizer.checkBraces(js);
		} catch (final BracesException e) {
			fail("Should NOT be exception");
		}
	}

}
