package delight.nashornsandbox.internal;

import delight.nashornsandbox.NashornSandbox;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@SuppressWarnings("all")
public class NashornSandboxImpl implements NashornSandbox {
  public ScriptEngine createScriptEngine() {
    ScriptEngine _xblockexpression = null;
    {
      final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
      _xblockexpression = factory.getScriptEngine();
    }
    return _xblockexpression;
  }
}
