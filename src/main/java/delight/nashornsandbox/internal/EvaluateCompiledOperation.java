package delight.nashornsandbox.internal;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

public class EvaluateCompiledOperation implements ScriptEngineOperation {

	private final CompiledScript compiledScript;
	private final ScriptContext scriptContext;
	private final Bindings bindings;

	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

	public ScriptContext getScriptContext() {
		return scriptContext;
	}

	public Bindings getBindings() {
		return bindings;
	}

	public EvaluateCompiledOperation(CompiledScript compiledScript, ScriptContext scriptContext, Bindings bindings) {
		this.compiledScript = compiledScript;
		this.scriptContext = scriptContext;
		this.bindings = bindings;
	}

	@Override
	public Object executeScriptEngineOperation(ScriptEngine scriptEngine) throws ScriptException
	{
		if (LOG.isDebugEnabled()) {
			LOG.debug("--- Running Compiled JS ---");
			LOG.debug(compiledScript.toString());
			LOG.debug("--- Compiled JS END ---");
		}

		if (bindings != null) {
			return compiledScript.eval(bindings);
		} else if (scriptContext != null) {
			return compiledScript.eval(scriptContext);
		} else {
			return compiledScript.eval();
		}
	}
}
