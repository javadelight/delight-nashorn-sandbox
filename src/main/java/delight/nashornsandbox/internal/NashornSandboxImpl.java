package delight.nashornsandbox.internal;

import delight.async.Value;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.internal.BeautifyJs;
import delight.nashornsandbox.internal.InterruptTest;
import delight.nashornsandbox.internal.MonitorThread;
import delight.nashornsandbox.internal.SandboxClassFilter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class NashornSandboxImpl implements NashornSandbox {
  protected SandboxClassFilter sandboxClassFilter;
  
  protected final Map<String, Object> globalVariables;
  
  protected ScriptEngine scriptEngine;
  
  protected Long maxCPUTimeInMs = Long.valueOf(0L);
  
  protected ExecutorService exectuor;
  
  protected boolean allowPrintFunctions = false;
  
  protected boolean allowReadFunctions = false;
  
  protected boolean allowLoadFunctions = false;
  
  protected boolean allowExitFunctions = false;
  
  protected boolean allowGlobalsObjects = false;
  
  protected volatile boolean debug = false;
  
  public void assertScriptEngine() {
    try {
      if ((this.scriptEngine != null)) {
        return;
      }
      final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
      ScriptEngine _scriptEngine = factory.getScriptEngine(this.sandboxClassFilter);
      this.scriptEngine = _scriptEngine;
      this.scriptEngine.eval("var window = {};");
      this.scriptEngine.eval(BeautifyJs.CODE);
      Set<Map.Entry<String, Object>> _entrySet = this.globalVariables.entrySet();
      for (final Map.Entry<String, Object> entry : _entrySet) {
        String _key = entry.getKey();
        Object _value = entry.getValue();
        this.scriptEngine.put(_key, _value);
      }
      String _xifexpression = null;
      if ((!this.allowExitFunctions)) {
        _xifexpression = (("" + "quit = function() {};\n") + "exit = function() {};\n");
      } else {
        _xifexpression = "";
      }
      String _plus = ("\n" + _xifexpression);
      String _plus_1 = (_plus + "\n");
      String _xifexpression_1 = null;
      if ((!this.allowPrintFunctions)) {
        _xifexpression_1 = (("" + "print = function() {};\n") + "echo = function() {};\n");
      } else {
        _xifexpression_1 = "";
      }
      String _plus_2 = (_plus_1 + _xifexpression_1);
      String _plus_3 = (_plus_2 + "\n");
      String _xifexpression_2 = null;
      if ((!this.allowReadFunctions)) {
        _xifexpression_2 = (("" + "readFully = function() {};\n") + "readLine = function() {};\n");
      } else {
        _xifexpression_2 = "";
      }
      String _plus_4 = (_plus_3 + _xifexpression_2);
      String _plus_5 = (_plus_4 + "\n");
      String _xifexpression_3 = null;
      if ((!this.allowLoadFunctions)) {
        _xifexpression_3 = (("" + "load = function() {};\n") + "loadWithNewGlobal = function() {};\n");
      } else {
        _xifexpression_3 = "";
      }
      String _plus_6 = (_plus_5 + _xifexpression_3);
      String _plus_7 = (_plus_6 + "\n");
      String _xifexpression_4 = null;
      if ((!this.allowGlobalsObjects)) {
        _xifexpression_4 = ((((((("" + "$ARG = null;\n") + "$ENV = null;\n") + "$EXEC = null;\n") + "$OPTIONS = null;\n") + 
          "$OUT = null;\n") + "$ERR = null;\n") + "$EXIT = null;\n");
      } else {
        _xifexpression_4 = "";
      }
      String _plus_8 = (_plus_7 + _xifexpression_4);
      String _plus_9 = (_plus_8 + "\n");
      this.scriptEngine.eval(_plus_9);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected static String replaceGroup(final String str, final String regex, final String replacementForGroup2) {
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(str);
    final StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, ("$1" + replacementForGroup2));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
  
  protected static String injectInterruptionCalls(final String str, final int randomToken) {
    String _xblockexpression = null;
    {
      String res = str.replaceAll(";\\n(?![\\s]*else[\\s]+)", ((";intCheckForInterruption" + Integer.valueOf(randomToken)) + "();\n"));
      String _replaceGroup = NashornSandboxImpl.replaceGroup(res, "(while \\([^\\)]*)(\\) \\{)", ((") {intCheckForInterruption" + Integer.valueOf(randomToken)) + "();"));
      res = _replaceGroup;
      String _replaceGroup_1 = NashornSandboxImpl.replaceGroup(res, "(for \\([^\\)]*)(\\) \\{)", ((") {intCheckForInterruption" + Integer.valueOf(randomToken)) + "();"));
      res = _replaceGroup_1;
      String _replaceAll = res.replaceAll("\\} while \\(", (("\nintCheckForInterruption" + Integer.valueOf(randomToken)) + "();\n\\} while \\("));
      _xblockexpression = res = _replaceAll;
    }
    return _xblockexpression;
  }
  
  @Override
  public Object eval(final String js) {
    try {
      Object _xblockexpression = null;
      {
        this.assertScriptEngine();
        if (((this.maxCPUTimeInMs).longValue() == 0)) {
          if (this.debug) {
            InputOutput.<String>println("--- Running JS ---");
            InputOutput.<String>println(js);
            InputOutput.<String>println("--- JS END ---");
          }
          return this.scriptEngine.eval(js);
        }
        Object _xsynchronizedexpression = null;
        synchronized (this) {
          Object _xblockexpression_1 = null;
          {
            final Value<Object> resVal = new Value<Object>(null);
            final Value<Throwable> exceptionVal = new Value<Throwable>(null);
            final MonitorThread monitorThread = new MonitorThread(((this.maxCPUTimeInMs).longValue() * 1000000));
            if ((this.exectuor == null)) {
              throw new IllegalStateException(
                "When a CPU time limit is set, an executor needs to be provided by calling .setExecutor(...)");
            }
            final Object monitor = new Object();
            final Runnable _function = new Runnable() {
              @Override
              public void run() {
                try {
                  boolean _contains = js.contains("intCheckForInterruption");
                  if (_contains) {
                    throw new IllegalArgumentException(
                      "Script contains the illegal string [intCheckForInterruption]");
                  }
                  Object _eval = NashornSandboxImpl.this.scriptEngine.eval("window.js_beautify;");
                  final ScriptObjectMirror jsBeautify = ((ScriptObjectMirror) _eval);
                  Object _call = jsBeautify.call("beautify", js);
                  final String beautifiedJs = ((String) _call);
                  Random _random = new Random();
                  int _nextInt = _random.nextInt();
                  final int randomToken = Math.abs(_nextInt);
                  StringConcatenation _builder = new StringConcatenation();
                  _builder.append("var InterruptTest = Java.type(\'");
                  String _name = InterruptTest.class.getName();
                  _builder.append(_name, "");
                  _builder.append("\');");
                  _builder.newLineIfNotEmpty();
                  _builder.append("var isInterrupted = InterruptTest.isInterrupted;");
                  _builder.newLine();
                  _builder.append("var intCheckForInterruption");
                  _builder.append(randomToken, "");
                  _builder.append(" = function() {");
                  _builder.newLineIfNotEmpty();
                  _builder.append("\t");
                  _builder.append("if (isInterrupted()) {");
                  _builder.newLine();
                  _builder.append("\t    ");
                  _builder.append("throw new Error(\'Interrupted");
                  _builder.append(randomToken, "\t    ");
                  _builder.append("\')");
                  _builder.newLineIfNotEmpty();
                  _builder.append("\t");
                  _builder.append("}");
                  _builder.newLine();
                  _builder.append("};");
                  _builder.newLine();
                  String preamble = _builder.toString();
                  String _replace = preamble.replace("\n", "");
                  preamble = _replace;
                  String _injectInterruptionCalls = NashornSandboxImpl.injectInterruptionCalls(beautifiedJs, randomToken);
                  final String securedJs = (preamble + _injectInterruptionCalls);
                  final Thread mainThread = Thread.currentThread();
                  Thread _currentThread = Thread.currentThread();
                  monitorThread.setThreadToMonitor(_currentThread);
                  final Runnable _function = new Runnable() {
                    @Override
                    public void run() {
                      mainThread.interrupt();
                    }
                  };
                  monitorThread.setOnInvalidHandler(_function);
                  monitorThread.start();
                  try {
                    if (NashornSandboxImpl.this.debug) {
                      InputOutput.<String>println("--- Running JS ---");
                      InputOutput.<String>println(securedJs);
                      InputOutput.<String>println("--- JS END ---");
                    }
                    final Object res = NashornSandboxImpl.this.scriptEngine.eval(securedJs);
                    resVal.set(res);
                  } catch (final Throwable _t) {
                    if (_t instanceof ScriptException) {
                      final ScriptException e = (ScriptException)_t;
                      String _message = e.getMessage();
                      boolean _contains_1 = _message.contains(("Interrupted" + Integer.valueOf(randomToken)));
                      if (_contains_1) {
                        monitorThread.notifyOperationInterrupted();
                      } else {
                        exceptionVal.set(e);
                        monitorThread.stopMonitor();
                        synchronized (monitor) {
                          monitor.notify();
                        }
                        return;
                      }
                    } else {
                      throw Exceptions.sneakyThrow(_t);
                    }
                  } finally {
                    monitorThread.stopMonitor();
                    synchronized (monitor) {
                      monitor.notify();
                    }
                  }
                } catch (final Throwable _t_1) {
                  if (_t_1 instanceof Throwable) {
                    final Throwable t = (Throwable)_t_1;
                    exceptionVal.set(t);
                    monitorThread.stopMonitor();
                    synchronized (monitor) {
                      monitor.notify();
                    }
                  } else {
                    throw Exceptions.sneakyThrow(_t_1);
                  }
                }
              }
            };
            this.exectuor.execute(_function);
            synchronized (monitor) {
              monitor.wait();
            }
            boolean _isCPULimitExceeded = monitorThread.isCPULimitExceeded();
            if (_isCPULimitExceeded) {
              String notGraceful = "";
              boolean _gracefullyInterrputed = monitorThread.gracefullyInterrputed();
              boolean _not = (!_gracefullyInterrputed);
              if (_not) {
                notGraceful = " The operation could not be gracefully interrupted.";
              }
              Throwable _get = exceptionVal.get();
              throw new ScriptCPUAbuseException(
                ((("Script used more than the allowed [" + this.maxCPUTimeInMs) + " ms] of CPU time. ") + notGraceful), _get);
            }
            Throwable _get_1 = exceptionVal.get();
            boolean _tripleNotEquals = (_get_1 != null);
            if (_tripleNotEquals) {
              throw exceptionVal.get();
            }
            _xblockexpression_1 = resVal.get();
          }
          _xsynchronizedexpression = _xblockexpression_1;
        }
        _xblockexpression = _xsynchronizedexpression;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public NashornSandbox setMaxCPUTime(final long limit) {
    NashornSandboxImpl _xblockexpression = null;
    {
      this.maxCPUTimeInMs = Long.valueOf(limit);
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  @Override
  public NashornSandbox allow(final Class<?> clazz) {
    NashornSandboxImpl _xblockexpression = null;
    {
      String _name = clazz.getName();
      this.sandboxClassFilter.add(_name);
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  @Override
  public void disallow(final Class<?> clazz) {
    String _name = clazz.getName();
    this.sandboxClassFilter.remove(_name);
  }
  
  @Override
  public boolean isAllowed(final Class<?> clazz) {
    String _name = clazz.getName();
    return this.sandboxClassFilter.contains(_name);
  }
  
  @Override
  public void disallowAllClasses() {
    this.sandboxClassFilter.clear();
  }
  
  @Override
  public NashornSandbox inject(final String variableName, final Object object) {
    NashornSandboxImpl _xblockexpression = null;
    {
      this.globalVariables.put(variableName, object);
      Class<?> _class = object.getClass();
      String _name = _class.getName();
      boolean _contains = this.sandboxClassFilter.contains(_name);
      boolean _not = (!_contains);
      if (_not) {
        Class<?> _class_1 = object.getClass();
        this.allow(_class_1);
      }
      if ((this.scriptEngine != null)) {
        this.scriptEngine.put(variableName, object);
      }
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  @Override
  public NashornSandbox setExecutor(final ExecutorService executor) {
    NashornSandboxImpl _xblockexpression = null;
    {
      this.exectuor = executor;
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  @Override
  public ExecutorService getExecutor() {
    return this.exectuor;
  }
  
  @Override
  public Object get(final String variableName) {
    Object _xblockexpression = null;
    {
      this.assertScriptEngine();
      _xblockexpression = this.scriptEngine.get(variableName);
    }
    return _xblockexpression;
  }
  
  @Override
  public void allowPrintFunctions(final boolean v) {
    this.allowPrintFunctions = v;
  }
  
  @Override
  public void allowReadFunctions(final boolean v) {
    this.allowReadFunctions = v;
  }
  
  @Override
  public void allowLoadFunctions(final boolean v) {
    this.allowLoadFunctions = v;
  }
  
  @Override
  public void allowExitFunctions(final boolean v) {
    this.allowExitFunctions = v;
  }
  
  @Override
  public void allowGlobalsObjects(final boolean v) {
    this.allowGlobalsObjects = v;
  }
  
  @Override
  public void setDebug(final boolean value) {
    this.debug = value;
  }
  
  public NashornSandboxImpl() {
    SandboxClassFilter _sandboxClassFilter = new SandboxClassFilter();
    this.sandboxClassFilter = _sandboxClassFilter;
    HashMap<String, Object> _hashMap = new HashMap<String, Object>();
    this.globalVariables = _hashMap;
    this.allow(InterruptTest.class);
  }
  
  @Override
  public void setWriter(final Writer writer) {
    this.assertScriptEngine();
    ScriptContext _context = this.scriptEngine.getContext();
    _context.setWriter(writer);
  }
}
