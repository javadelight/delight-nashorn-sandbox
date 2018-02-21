package delight.nashornsandbox.internal;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public interface ScriptEngineOperation {

	Object executeScriptEngineOperation(ScriptEngine scriptEngine) throws ScriptException, Exception;

}
