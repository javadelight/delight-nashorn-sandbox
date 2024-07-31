package delight.nashornsandbox.internal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

	/** The resource name of inject.js script. */
	private final static String INJECT_JS = "/inject.js";


	private static final List<String> inject_FUNCTIONS = Arrays.asList("exports.injectJs;", "window.injectJs;", "exports.injectJs;", "global.injectJs;");

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

	/** Soft reference to the text of the js script. */
	private static SoftReference<String> injectScript = new SoftReference<>(null);

	private final ScriptEngine scriptEngine;

	/** JS beautify() function reference. */
	private final Function<String, String> jsInject;

	private final SecuredJsCache securedJsCache;

	/** <code>true</code> when lack of braces is allowed. */
	private final boolean allowNoBraces;

	JsSanitizer(final ScriptEngine scriptEngine, final int maxPreparedStatements, final boolean allowBraces) {
		this.scriptEngine = scriptEngine;
		this.allowNoBraces = allowBraces;
		this.securedJsCache = createSecuredJsCache(maxPreparedStatements);
		assertScriptEngine();
		Object beautifHandler = getInjectHandler(scriptEngine);
		this.jsInject = injectAsFunction(beautifHandler);
	}

	JsSanitizer(final ScriptEngine scriptEngine, final boolean allowBraces, SecuredJsCache cache) {
		this.scriptEngine = scriptEngine;
		this.allowNoBraces = allowBraces;
		this.securedJsCache = cache;
		assertScriptEngine();
		Object injectHandler = getInjectHandler(scriptEngine);
		this.jsInject = injectAsFunction(injectHandler);
	}

	private void assertScriptEngine() {
		try {
			scriptEngine.eval("var window = {};");
			scriptEngine.eval("var exports = {};");
			scriptEngine.eval("var global = {};");
			// Object.assign polyfill
			scriptEngine.eval("\"function\"!=typeof Object.assign&&Object.defineProperty(Object,\"assign\",{value:function(e,t){\"use strict\";if(null==e)throw new TypeError(\"Cannot convert undefined or null to object\");for(var n=Object(e),r=1;r<arguments.length;r++){var o=arguments[r];if(null!=o)for(var c in o)Object.prototype.hasOwnProperty.call(o,c)&&(n[c]=o[c])}return n},writable:!0,configurable:!0});");
			scriptEngine.eval(getInjectJs());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object getInjectHandler(final ScriptEngine scriptEngine) {
		try {
			for (final String name : inject_FUNCTIONS) {
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
		final String injectedJs = injectInterruptionCalls(js);
		// if no injection, no need to add preamble
		if (injectedJs.equals(js)) {
			return injectedJs;
		} else {
			final String preamble = getPreamble();
			return preamble + injectedJs;
		}
	}

	String injectInterruptionCalls(final String js) {
		return jsInject.apply(js);
	}

	private static String getInjectJs() {
		String script = injectScript.get();
		if (script == null) {
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(JsSanitizer.class.getResourceAsStream(INJECT_JS)), StandardCharsets.UTF_8))) {
				final StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
				script = sb.toString();
			} catch (final IOException e) {
				throw new RuntimeException("Cannot find file: " + INJECT_JS, e);
			}
			injectScript = new SoftReference<>(script);
		}
		return script;
	}


	@SuppressWarnings("unchecked")
	private static Function<String, String> injectAsFunction(Object injectScript) {

		if (NashornDetection.isStandaloneNashornScriptObjectMirror(injectScript)) {
			return script -> {
				org.openjdk.nashorn.api.scripting.ScriptObjectMirror scriptObjectMirror = (org.openjdk.nashorn.api.scripting.ScriptObjectMirror) injectScript;
				return (String) scriptObjectMirror.call("injectJs", script, JS_INTERRUPTED_FUNCTION);
			};
		}

		if (injectScript instanceof Function<?, ?>) {
			return script -> (String) ((Function<Object[], Object>) injectScript).apply(new Object[]{script, JS_INTERRUPTED_FUNCTION});
		}

		throw new RuntimeException("Unsupported handler type for sanitizerJs: " + injectScript.getClass().getName());
	}
}
