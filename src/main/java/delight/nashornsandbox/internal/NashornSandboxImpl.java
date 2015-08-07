package delight.nashornsandbox.internal;

import com.google.common.base.Objects;
import delight.async.Value;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import delight.nashornsandbox.internal.BeautifyJs;
import delight.nashornsandbox.internal.InterruptTest;
import delight.nashornsandbox.internal.MonitorThread;
import delight.nashornsandbox.internal.SandboxClassFilter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class NashornSandboxImpl implements NashornSandbox {
  private final Set<String> allowedClasses;
  
  private ScriptEngine scriptEngine;
  
  private Integer maxCPUTimeInMs = Integer.valueOf(0);
  
  private ExecutorService exectuor;
  
  public void assertScriptEngine() {
    try {
      boolean _notEquals = (!Objects.equal(this.scriptEngine, null));
      if (_notEquals) {
        return;
      }
      final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
      SandboxClassFilter _sandboxClassFilter = new SandboxClassFilter(this.allowedClasses);
      ScriptEngine _scriptEngine = factory.getScriptEngine(_sandboxClassFilter);
      this.scriptEngine = _scriptEngine;
      this.scriptEngine.eval("var window = {};");
      this.scriptEngine.eval(BeautifyJs.CODE);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public Object eval(final String js) {
    try {
      Object _xblockexpression = null;
      {
        this.assertScriptEngine();
        if (((this.maxCPUTimeInMs).intValue() == 0)) {
          return this.scriptEngine.eval(js);
        }
        Object _xsynchronizedexpression = null;
        synchronized (this) {
          Object _xblockexpression_1 = null;
          {
            final Value<Object> resVal = new Value<Object>(null);
            final Value<Throwable> exceptionVal = new Value<Throwable>(null);
            final Thread outerThread = Thread.currentThread();
            final MonitorThread monitorThread = new MonitorThread(((this.maxCPUTimeInMs).intValue() * 1000));
            boolean _equals = Objects.equal(this.exectuor, null);
            if (_equals) {
              throw new IllegalStateException(
                "When a CPU time limit is set, an executor needs to be provided by calling .setExecutor(...)");
            }
            final Runnable _function = new Runnable() {
              @Override
              public void run() {
                try {
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
                  _builder.append("throw new Error(\'Interrupted\')");
                  _builder.newLine();
                  _builder.append("\t");
                  _builder.append("}");
                  _builder.newLine();
                  _builder.append("};");
                  _builder.newLine();
                  String _replaceAll = beautifiedJs.replaceAll(";\\n", ((";intCheckForInterruption" + Integer.valueOf(randomToken)) + "();\n"));
                  String _replace = _replaceAll.replace(") {", ((") {intCheckForInterruption" + Integer.valueOf(randomToken)) + "();\n"));
                  final String securedJs = (_builder.toString() + _replace);
                  monitorThread.start();
                  NashornSandboxImpl.this.scriptEngine.eval(securedJs);
                  final Object res = NashornSandboxImpl.this.scriptEngine.eval(js);
                  monitorThread.stopMonitor();
                  resVal.set(res);
                  outerThread.notify();
                } catch (final Throwable _t) {
                  if (_t instanceof Throwable) {
                    final Throwable t = (Throwable)_t;
                    exceptionVal.set(t);
                    /* NashornSandboxImpl.this; */
                    synchronized (NashornSandboxImpl.this) {
                      NashornSandboxImpl.this.notify();
                    }
                  } else {
                    throw Exceptions.sneakyThrow(_t);
                  }
                }
              }
            };
            this.exectuor.execute(_function);
            this.wait();
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
            boolean _notEquals = (!Objects.equal(_get_1, null));
            if (_notEquals) {
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
  public NashornSandbox setMaxCPUTime(final int limit) {
    NashornSandboxImpl _xblockexpression = null;
    {
      this.maxCPUTimeInMs = Integer.valueOf(limit);
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  @Override
  public NashornSandbox allow(final Class<?> clazz) {
    NashornSandboxImpl _xblockexpression = null;
    {
      String _name = clazz.getName();
      this.allowedClasses.add(_name);
      this.scriptEngine = null;
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
  
  public NashornSandboxImpl() {
    HashSet<String> _hashSet = new HashSet<String>();
    this.allowedClasses = _hashSet;
    this.allow(InterruptTest.class);
  }
}
