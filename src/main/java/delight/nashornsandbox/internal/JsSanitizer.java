package delight.nashornsandbox.internal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.eclipse.xtext.xbase.lib.Exceptions;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * JavaScript sanitizer. Check for loops and inserts function call which breaks
 * script execution when JS engine thread is interrupted.
 *
 * <p>Created on 2017.11.22</p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("restriction")
class JsSanitizer
{
    private final static String BEAUTIFY_JS = "delight/nashornsandbox/internal/beautify.min.js";
    private static SoftReference<String> beautifysScript = new SoftReference<>(null);
    
    private final ScriptEngine scriptEngine;
    
    /**JS beutify() function reference.*/
    private final ScriptObjectMirror jsBeautify;

    private final Map<String,String> securedJsCache;
    
    /** 
     * The name of the JS error thrown when script is intrrupted. To prevent
     * collisions random sufix is added.
     */
    private final String jsInterruptedError;
    
    /**
     * The name of the JS function to be inserted into user script. To prevent
     * collisions random sufix is added.
     */
    private final String jsInterruptedFunction;
    
    /**
     * The name of the variable which holds reference to interruption checking
     * class. To prevent collisions random sufix is added.
     */
    private final String jsInterruptedTest;
    
    JsSanitizer(final ScriptEngine scriptEngine, final int maxPreparedStatements)
    {
        this.scriptEngine = scriptEngine;
        this.securedJsCache = createSecuredJsCache(maxPreparedStatements);
        final int random = Math.abs(new Random().nextInt());
        this.jsInterruptedError = "Interrupted_" + random;
        this.jsInterruptedFunction = "intCheckForInterruption_" + random;
        this.jsInterruptedTest = "InterruptTest_" + random;
        assertScriptEngine();
        this.jsBeautify = getBeautifHandler(scriptEngine);
    }

    private void assertScriptEngine() {
      try {
        scriptEngine.eval("var window = {};");
        scriptEngine.eval(getBeautifyJs());
      } 
      catch (final Exception e) {
        throw Exceptions.sneakyThrow(e);
      }
    }
          
    private static ScriptObjectMirror getBeautifHandler(final ScriptEngine scriptEngine) {
      try {
        return (ScriptObjectMirror) scriptEngine.eval("window.js_beautify;");
      }
      catch(final ScriptException e) {
        // should never happen
        throw new RuntimeException(e);
      }
    }
    
    private Map<String,String> createSecuredJsCache(final int maxPreparedStatements) {
      // Create cache
      if(maxPreparedStatements == 0)
      {
        return null;
      }
      else {
        return new LinkedHashMap<String,String>(maxPreparedStatements+1, .75F, true) {
          private static final long serialVersionUID = 1L;
          // This method is called just after a new entry has been added
          @Override
          @SuppressWarnings("unused")
          public boolean removeEldestEntry(final Map.Entry<String,String> eldest) {
            return size() > maxPreparedStatements;
          }
        };
      }
    }
    
    private static String replaceGroup(final String str, final String regex, final String replacement) {
      final Pattern pattern = Pattern.compile(regex);
      final Matcher matcher = pattern.matcher(str);
      final StringBuffer sb = new StringBuffer();
      while (matcher.find()) {
        matcher.appendReplacement(sb, ("$1" + replacement));
      }
      matcher.appendTail(sb);
      return sb.toString();
    }
      
    private String injectInterruptionCalls(final String str) {
      String res = str.replaceAll(";\\n(?![\\s]*else[\\s]+)", ";"+jsInterruptedFunction+"();");
      res = replaceGroup(res, "(while \\([^\\)]*)(\\) \\{)", "){"+jsInterruptedFunction+"();");
      res = replaceGroup(res, "(for \\([^\\)]*)(\\) \\{)", "){"+jsInterruptedFunction+"();");
      return res.replaceAll("\\} while \\(", "\n"+jsInterruptedFunction+"();\n\\} while \\(");
    }

    private String getPreamble() {
      final String clazzName = InterruptTest.class.getName();
      final StringBuilder sb = new StringBuilder();
      sb.append("var ").append(jsInterruptedTest).append("=Java.type('").append(clazzName).append("');");
      sb.append("var ").append(jsInterruptedFunction).append("=function(){");
      sb.append("if(").append(jsInterruptedTest).append(".isInterrupted()) {");
      sb.append("throw new Error('").append(jsInterruptedError).append("');");
      sb.append("}");
      sb.append("};\n");
      return sb.toString();
    }
    
    private void checkJs(final String js) {
      if (js.contains(jsInterruptedError) || js.contains(jsInterruptedFunction) || 
          js.contains(jsInterruptedTest)) {
        throw new IllegalArgumentException("Script contains the illegal string [" + 
          jsInterruptedError+","+jsInterruptedFunction+"]");
      }
    }

    String secureJs(final String js) throws ScriptException {
      String securedJs = null;
      if(securedJsCache != null) {
        securedJs = securedJsCache.get(js);
      }
      if(securedJs == null) {
        checkJs(js);
        final String beautifiedJs = (String) jsBeautify.call("beautify", js);
        final String injectedJs = injectInterruptionCalls(beautifiedJs);
        final String preamble = getPreamble();
        securedJs = preamble + injectedJs;
        if(securedJsCache != null) {
          securedJsCache.put(js, securedJs);
        }
      }
      return securedJs;
    }
    
    String getJsInterruptedError()
    {
        return jsInterruptedError;
    }
 
    private static String getBeautifyJs() {
      String script = beautifysScript.get();
      if(script == null) {
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(
            new BufferedInputStream(JsSanitizer.class.getClassLoader().getResourceAsStream(BEAUTIFY_JS))))) {
          final StringBuilder sb = new StringBuilder();
          String line;
          while ((line=reader.readLine()) != null) {
            sb.append(line).append('\n');
          }
          script = sb.toString();
        }
        catch (final IOException e) {
          throw new RuntimeException("Cannot find file: " + BEAUTIFY_JS, e);
        }
        beautifysScript = new SoftReference<>(script);
      }
      return script;
    }
    
}
