package delight.nashornsandbox.internal;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.internal.SandboxClassFilter;
import java.util.HashSet;
import java.util.Set;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@SuppressWarnings("all")
public class NashornSandboxImpl implements NashornSandbox {
  private final Set<String> allowedClasses;
  
  public ScriptEngine createScriptEngine() {
    ScriptEngine _xblockexpression = null;
    {
      final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
      SandboxClassFilter _sandboxClassFilter = new SandboxClassFilter(this.allowedClasses);
      _xblockexpression = factory.getScriptEngine(_sandboxClassFilter);
    }
    return _xblockexpression;
  }
  
  public void allow(final Class<?> clazz) {
    String _name = clazz.getName();
    this.allowedClasses.add(_name);
  }
  
  public NashornSandboxImpl() {
    HashSet<String> _hashSet = new HashSet<String>();
    this.allowedClasses = _hashSet;
  }
}
