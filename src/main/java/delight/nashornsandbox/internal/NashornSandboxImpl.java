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
  
  private final ExecutorService exectuor;
  
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
      + "\nInvalid number of arguments. The constructor MonitorThread(long) is not applicable for the arguments (int,Thread,(Object)=>void)");
  }
  
  @Override
  public void setMaxCPUTime(final int limit) {
    this.maxCPUTimeInMs = Integer.valueOf(limit);
  }
  
  @Override
  public void allow(final Class<?> clazz) {
    String _name = clazz.getName();
    this.allowedClasses.add(_name);
    this.scriptEngine = null;
  }
  
  public NashornSandboxImpl() {
    HashSet<String> _hashSet = new HashSet<String>();
    this.allowedClasses = _hashSet;
    this.allow(InterruptTest.class);
  }
}
