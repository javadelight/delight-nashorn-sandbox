package delight.nashornsandbox.internal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import delight.nashornsandbox.SecuredJsCache;
import delight.nashornsandbox.exceptions.BracesException;

/**
 * JavaScript sanitizer. Check for loops and inserts function call which breaks
 * script execution when JS engine thread is interrupted.
 *
 * <p>
 * Created on 2017.11.22
 * </p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class JsSanitizer {

	private static class PoisonPil {
		Pattern pattern;
		String replacement;

		PoisonPil(final Pattern pattern, final String replacement) {
			this.pattern = pattern;
			this.replacement = replacement;
		}
	}

	/** The resource name of beautify.min.js script. */
	private final static String BEAUTIFY_JS = "/META-INF/resources/webjars/js-beautify/1.14.7/js/lib/beautifier.js";

	/** The beautify function search list. */
	private static final List<String> BEAUTIFY_FUNCTIONS = Arrays.asList("exports.beautifier.js;", "window.js_beautify;", "exports.js_beautify;",
			"global.js_beautify;");



	/**
	 * The name of the JS function to be inserted into user script. To prevent
	 * collisions random suffix is added.
	 */
	final static String JS_INTERRUPTED_FUNCTION = "__if";

	/**
	 * The name of the variable which holds reference to interruption checking
	 * class. To prevent collisions random suffix is added.
	 */
	final static String JS_INTERRUPTED_TEST = "__it";

	private final static List<PoisonPil> POISON_PILLS = Arrays.asList(
			// every 10th statements ended with semicolon put interrupt checking function
			new PoisonPil(Pattern.compile("(([^;]+;){9}[^;]+(?<!break|continue);\\n(?![\\W]*(\\/\\/.+[\\W]+)*else))"),
					JS_INTERRUPTED_FUNCTION + "();\n"),
			// every (except switch) block start brace put interrupt checking function
			new PoisonPil(Pattern.compile("(\\s*for\\s*\\([^\\{]+\\)\\s*\\{)"), JS_INTERRUPTED_FUNCTION + "();"), // for
																													// with
																													// block
			new PoisonPil(Pattern.compile("(\\s*for\\s*\\([^\\{]+\\)\\s*[^\\{]+;)"), JS_INTERRUPTED_FUNCTION + "();"), // for
																														// without
																						// block
			//
			new PoisonPil(Pattern.compile("(\\s*([^\"]?function)\\s*[^\"}]*\\([\\s]*\\)\\s*\\{)"),
					JS_INTERRUPTED_FUNCTION + "();"), // function except when enclosed in quotes
			new PoisonPil(Pattern.compile("(\\s*while\\s*\\([^\\{]+\\{)"), JS_INTERRUPTED_FUNCTION + "();"),
			new PoisonPil(Pattern.compile("(\\s*do\\s*\\{)"), JS_INTERRUPTED_FUNCTION + "();"));

	/**
	 * The beautifier options. Don't change if you are not know what you are doing,
	 * because regexps are depended on it.
	 */
	private final static Map<String, Object> BEAUTIFY_OPTIONS = new HashMap<>();

	static {
		BEAUTIFY_OPTIONS.put("brace_style", "collapse");
		BEAUTIFY_OPTIONS.put("preserve_newlines", false);
		BEAUTIFY_OPTIONS.put("indent_size", 1);
		BEAUTIFY_OPTIONS.put("max_preserve_newlines", 0);
	}

	/** Soft reference to the text of the js script. */
	private static SoftReference<String> beautifysScript = new SoftReference<>(null);

	private final ScriptEngine scriptEngine;

	/** JS beautify() function reference. */
	private final Function<String, String> jsBeautify;

	private final SecuredJsCache securedJsCache;

	/** <code>true</code> when lack of braces is allowed. */
	private final boolean allowNoBraces;

	JsSanitizer(final ScriptEngine scriptEngine, final int maxPreparedStatements, final boolean allowBraces) {
		this.scriptEngine = scriptEngine;
		this.allowNoBraces = allowBraces;
		this.securedJsCache = createSecuredJsCache(maxPreparedStatements);
		assertScriptEngine();
        Object beautifHandler = getBeautifyHandler(scriptEngine);
        this.jsBeautify = beautifierAsFunction(beautifHandler);
	}

	JsSanitizer(final ScriptEngine scriptEngine, final boolean allowBraces, SecuredJsCache cache) {
		this.scriptEngine = scriptEngine;
		this.allowNoBraces = allowBraces;
		this.securedJsCache = cache;
		assertScriptEngine();
        Object beautifHandler = getBeautifyHandler(scriptEngine);
        this.jsBeautify = beautifierAsFunction(beautifHandler);
	}

	private void assertScriptEngine() {
		try {
			scriptEngine.eval("var window = {};");
			scriptEngine.eval("var exports = {};");
			scriptEngine.eval("var global = {};");
			// Object.assign polyfill
			scriptEngine.eval("\"function\"!=typeof Object.assign&&Object.defineProperty(Object,\"assign\",{value:function(e,t){\"use strict\";if(null==e)throw new TypeError(\"Cannot convert undefined or null to object\");for(var n=Object(e),r=1;r<arguments.length;r++){var o=arguments[r];if(null!=o)for(var c in o)Object.prototype.hasOwnProperty.call(o,c)&&(n[c]=o[c])}return n},writable:!0,configurable:!0});");
			scriptEngine.eval(getBeautifyJs());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object getBeautifyHandler(final ScriptEngine scriptEngine) {
		try {
			for (final String name : BEAUTIFY_FUNCTIONS) {
				final Object somWindow = scriptEngine.eval(name);
				if (somWindow != null) {
					return somWindow;
				}
			}
			throw new RuntimeException("Cannot find function 'js_beautify' in: window, exports, global");
		} catch (final ScriptException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}

	private SecuredJsCache createSecuredJsCache(final int maxPreparedStatements) {
		// Create cache
		if (maxPreparedStatements == 0) {
			return null;
		} else {
			return newSecuredJsCache(maxPreparedStatements);
		}
	}

	private SecuredJsCache newSecuredJsCache(final int maxPreparedStatements) {
		final LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>(maxPreparedStatements + 1, .75F, true) {
			private static final long serialVersionUID = 1L;

			// This method is called just after a new entry has been added
			@Override
			public boolean removeEldestEntry(final Map.Entry<String, String> eldest) {
				return size() > maxPreparedStatements;
			}
		};
		return new LinkedHashMapSecuredJsCache(linkedHashMap, allowNoBraces);
	}

	/** Pattern for back braces. */
	private final static List<Pattern> LACK_EXPECTED_BRACES = Arrays.asList(
			Pattern.compile("for [^\\{]+$"),
			Pattern.compile("^\\s*do [^\\{]*$", Pattern.MULTILINE),
			Pattern.compile("^[^\\}]*while [^\\{]+$", Pattern.MULTILINE));

			/**
	 * After beautifier every braces should be in place, if not, or too many we need
	 * to prevent script execution.
	 *
	 * @param beautifiedJs
	 *            evaluated script
	 * @throws BracesException
	 *             when braces are incorrect
	 */
	void checkBraces(final String beautifiedJs) throws BracesException {
		if (allowNoBraces) {
			return;
		}

		String withoutComments = RemoveComments.perform(beautifiedJs);
		for (final Pattern pattern : LACK_EXPECTED_BRACES) {
			final Matcher matcher = pattern.matcher(withoutComments);
			if (matcher.find()) {

				String line = "";
				int index = matcher.start();

				while (index >= 0 && withoutComments.charAt(index) != '\n' ) {
					line = withoutComments.charAt(index)+line;
					index--;
				}

				int singleParaCount = line.length() - line.replace("'", "").length();
				int doubleParaCount = line.length() - line.replace("\"", "").length();
				int commentParaCount = line.length() - line.replace("//", "").length();
				if (singleParaCount % 2 != 0 || doubleParaCount % 2 != 0 || commentParaCount > 0) {
					// for in string

				} else {
					throw new BracesException("Potentially no block braces after function|for|while|do. Found ["+matcher.group()+"]. "+
					"To disable this exception, please set the option `allowNoBraces(true)`");
				}
			}
		}
	}

	String injectInterruptionCalls(final String str) {
		String current = str;
		for (final PoisonPil pp : POISON_PILLS) {
			final StringBuffer sb = new StringBuffer();
			final ThreadLocal<Integer> matchCount = new ThreadLocal<>();
			matchCount.set(0);
			final Matcher matcher = pp.pattern.matcher(new SecureInterruptibleCharSequence(current,
			 matchCount));
			while (matcher.find()) {
				matcher.appendReplacement(sb, ("$1" + pp.replacement));
			}
			matcher.appendTail(sb);
			current = sb.toString();
		}
		return current;
	}

	private String getPreamble() {
		final String clazzName = InterruptTest.class.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("var ").append(JS_INTERRUPTED_TEST).append("=Java.type('").append(clazzName).append("');");
		sb.append("var ").append(JS_INTERRUPTED_FUNCTION).append("=function(){");
		sb.append(JS_INTERRUPTED_TEST).append(".test();};\n");
		return sb.toString();
	}

	private void checkJs(final String js) {
		if (js.contains(JS_INTERRUPTED_FUNCTION) || js.contains(JS_INTERRUPTED_TEST)) {
			throw new IllegalArgumentException(
					"Script contains the illegal string [" + JS_INTERRUPTED_TEST + "," + JS_INTERRUPTED_FUNCTION + "]");
		}
	}

	public String secureJs(final String js) throws ScriptException {
		if (securedJsCache == null) {
			return secureJsImpl(js);
		}
		ScriptException[] ex = new ScriptException[1];
		String securedJs = securedJsCache.getOrCreate(js, allowNoBraces, ()->{
			try {
				return secureJsImpl(js);
			} catch (BracesException e) {
				ex[0] = e;
				return null;
			}
		});
		if (ex[0] != null) {
			throw ex[0];
		}
		return securedJs;
	}

	private String secureJsImpl(final String js) throws BracesException {
		checkJs(js);
		final String beautifiedJs = beautifyJs(js);
		checkBraces(beautifiedJs);
		final String injectedJs = injectInterruptionCalls(beautifiedJs);
		// if no injection, no need to add preamble
		if (beautifiedJs.equals(injectedJs)) {
			return beautifiedJs;
		} else {
			final String preamble = getPreamble();
			return preamble + injectedJs;
		}
	}

	String beautifyJs(final String js) {
        return jsBeautify.apply(js);
	}

	private static String getBeautifyJs() {
		String script = beautifysScript.get();
		if (script == null) {
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(JsSanitizer.class.getResourceAsStream(BEAUTIFY_JS)), StandardCharsets.UTF_8))) {
				final StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
				script = sb.toString();
			} catch (final IOException e) {
				throw new RuntimeException("Cannot find file: " + BEAUTIFY_JS, e);
			}
			beautifysScript = new SoftReference<>(script);
		}
		return script;
	}


    @SuppressWarnings("unchecked")
	private static Function<String, String> beautifierAsFunction(Object beautifyScript) {
        if (NashornDetection.isJDKNashornScriptObjectMirror(beautifyScript)) {
			return script -> {
				jdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (jdk.nashorn.api.scripting.ScriptObjectMirror) beautifyScript;
				return (String) scriptObjectMirror.call("beautify", script, BEAUTIFY_OPTIONS);
			};
        }

        if (NashornDetection.isStandaloneNashornScriptObjectMirror(beautifyScript)) {
			return script -> {
				org.openjdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (org.openjdk.nashorn.api.scripting.ScriptObjectMirror) beautifyScript;
				return (String) scriptObjectMirror.call("beautify", script, BEAUTIFY_OPTIONS);
			};
        }

        if (beautifyScript instanceof Function<?, ?>) {
            return script -> (String) ((Function<Object[], Object>) beautifyScript).apply(new Object[]{script, BEAUTIFY_OPTIONS});
        }

        throw new RuntimeException("Unsupported handler type for jsBeautify: " + beautifyScript.getClass().getName());
    }
}
