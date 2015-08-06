package delight.nashornsandbox.internal;

import com.google.common.base.Objects;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.internal.SandboxClassFilter;
import java.util.HashSet;
import java.util.Set;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@SuppressWarnings("all")
public class NashornSandboxImpl implements NashornSandbox {
  private final Set<String> allowedClasses;
  
  private ScriptEngine scriptEngine;
  
  public void assertScriptEngine() {
    boolean _notEquals = (!Objects.equal(this.scriptEngine, null));
    if (_notEquals) {
      return;
    }
    final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    SandboxClassFilter _sandboxClassFilter = new SandboxClassFilter(this.allowedClasses);
    ScriptEngine _scriptEngine = factory.getScriptEngine(_sandboxClassFilter);
    this.scriptEngine = _scriptEngine;
  }
  
  public void run(final String js) {
    this.assertScriptEngine();
  }
  
  public void allow(final Class<?> clazz) {
    String _name = clazz.getName();
    this.allowedClasses.add(_name);
    this.scriptEngine = null;
  }
  
  public NashornSandboxImpl() {
    HashSet<String> _hashSet = new HashSet<String>();
    this.allowedClasses = _hashSet;
  }
}
