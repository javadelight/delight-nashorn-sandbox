package delight.nashornsandbox.internal;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class EvaluateOperation implements ScriptEngineOperation {

	private final String js;
	private final ScriptContext scriptContext;

	public String getJs() {
		return js;
	}

	public ScriptContext getScriptContext() {
		return scriptContext;
	}

	public EvaluateOperation(String js, ScriptContext scriptContext) {
		this.js = js;
		this.scriptContext = scriptContext;
	}

	@Override
	public Object executeScriptEngineOperation(ScriptEngine scriptEngine) throws ScriptException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("--- Running JS ---");
			LOG.debug(js);
			LOG.debug("--- JS END ---");
		}

		if (scriptContext != null) {
			return scriptEngine.eval(js, scriptContext);
		} else {
			return scriptEngine.eval(js);
		}
	}

}
