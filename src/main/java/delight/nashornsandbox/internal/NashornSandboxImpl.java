package delight.nashornsandbox.internal;

import com.google.common.base.Objects;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.internal.BeautifyJs;
import delight.nashornsandbox.internal.InterruptTest;
import delight.nashornsandbox.internal.SandboxClassFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
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
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field res is undefined for the type NashornSandboxImpl");
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
