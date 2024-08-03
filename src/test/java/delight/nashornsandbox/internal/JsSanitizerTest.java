package delight.nashornsandbox.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;

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
		scriptEngine = new NashornSandboxImpl().createNashornScriptEngineFactory();
	}

	@Before
	public void setUp() {
		jsSanitizer = new JsSanitizer(scriptEngine, 0);
	}

	@Test
	public void testSecureJs() throws Exception {
		final String js1 = jsSanitizer.secureJs("while(a > 0)\n\n\n {\n\n\nprint(a);}");
		assertTrue(js1.contains("while (a > 0) {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js2 = jsSanitizer.secureJs("while(a > 0) {print(a);}");
		assertTrue(js2.contains("while (a > 0) {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js3 = jsSanitizer.secureJs("do { c=1;} while(a > 0)");
		assertTrue(js3.contains("do {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js4 = jsSanitizer.secureJs("function a() { b++;}");
		assertTrue(js4.contains("function a() {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js5 = jsSanitizer.secureJs("var a = function() { b++;}");
		assertTrue(js5.contains("var a = function () {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js6 = jsSanitizer.secureJs("(function() { b++;})();");
		assertTrue(js6.contains("function () {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		final String js7 = jsSanitizer.secureJs("switch(expression) { case 'ABC': i++;break;}");
		System.out.println(js7);
		assertTrue(js7.contains("switch (expression) {\ncase 'ABC':"));
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
		assertTrue(bjs2.contains("for (var i = 0; i < 10; i++) {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
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
		final String js1 = "function FindProxyForURL(url, host) {\nvar i = 1;\nvar i = 2;\nvar i = 3;\nvar i = 4;\nvar i = 5;\nvar i = 6;\nvar i = 7;" +
				"\nif (dnsDomainIs(host, 'proxy8.com.net')) {\n    var i = 9;\n    var i = 10;\n    return 'PROXY http://proxy8.acme.com:8080';\n}" +
				"\nelse if (dnsDomainIs(host, 'acme1.com.net'))\n    return 'PROXY http://proxy9.acme.com:8080';" +
				"\nelse if (dnsDomainIs(host, 'initech.acme.com'))\n    return 'PROXY http://acme10.com:8080';" +
				"\nelse if (dnsDomainIs(host, 'whymper.net'))\n    return 'PROXY http://acme2.com:8080';" +
				"\nelse if (dnsDomainIs(host, 'enough.acme.com'))\n    return 'PROXY http://one.proxy.acme.com:8080';" +
				"\nelse\n    return 'DIRECT';\nvar i = 9;\nvar i = 10;\nvar i = 11;}";
		final String bjs = jsSanitizer.secureJs(js1);
		assertTrue(bjs.contains("function FindProxyForURL(url, host) {\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
		assertTrue(bjs.contains("var i = 10;\n    " + JsSanitizer.JS_INTERRUPTED_FUNCTION));
	}

	@Test
	public void testSecureJs_simple() throws Exception {
		final String js1 = "sum(a) + avg(b);";
		assertEquals(js1, jsSanitizer.secureJs(js1));
		final String js2 = "while(true) {sum(a) + avg(b);}";
        assertNotEquals(js2, jsSanitizer.secureJs(js2));
	}

	@Test
	public void test_issue_66_case1() {
		String script = "var t1 = \"(function)\";\n" + "var person={\n" + "    \"name\": \"test\"\n" + "};\n"
				+ "print(\"t1\" + person.name);";

		NashornSandbox sandbox = NashornSandboxes.create();
		try {
			sandbox.setMaxCPUTime(6000);
			sandbox.setMaxMemory(50 * 1024 * 1024L);
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

}