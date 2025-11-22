package delight.nashornsandbox.internal;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import static delight.nashornsandbox.internal.NashornSandboxImpl.LOG;

public class EvaluateOperation implements ScriptEngineOperation {

    private final String js;
    private final ScriptContext scriptContext;
    private final Bindings bindings;

    public String getJs() {
        return js;
    }

    public ScriptContext getScriptContext() {
        return scriptContext;
    }

    public Bindings getBindings() {
        return bindings;
    }

    public EvaluateOperation(String js, ScriptContext scriptContext, Bindings bindings) {
        this.js = js;
        this.scriptContext = scriptContext;
        this.bindings = bindings;
    }

    @Override
    public Object executeScriptEngineOperation(ScriptEngine scriptEngine) throws ScriptException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("--- Running JS ---");
            LOG.debug(js);
            LOG.debug("--- JS END ---");
        }
       
        if (bindings != null && scriptContext != null) {
            // Merge both: create a new context with scriptContext settings + merged engine bindings
            ScriptContext combinedContext = createMergedContext(scriptContext, bindings, scriptEngine);
            return scriptEngine.eval(js, combinedContext);
        } else if (bindings != null) {
            return scriptEngine.eval(js, bindings);
        } else if (scriptContext != null) {
            return scriptEngine.eval(js, scriptContext);
        } else {
            return scriptEngine.eval(js);
        }
    }

    private ScriptContext createMergedContext(ScriptContext scriptContext, Bindings additionalBindings, ScriptEngine scriptEngine) {
        SimpleScriptContext combined = new SimpleScriptContext();

        // Copy global scope bindings
        Bindings globalBindings = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
        if (globalBindings != null) {
            Bindings newGlobal = scriptEngine.createBindings();
            newGlobal.putAll(globalBindings);
            combined.setBindings(newGlobal, ScriptContext.GLOBAL_SCOPE);
        }

        // Merge engine scope: scriptContext engine bindings + additional bindings
        Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings mergedEngine = scriptEngine.createBindings();
        if (engineBindings != null) {
            mergedEngine.putAll(engineBindings);
        }
        mergedEngine.putAll(additionalBindings);
        combined.setBindings(mergedEngine, ScriptContext.ENGINE_SCOPE);

        // Copy other context attributes
        combined.setWriter(scriptContext.getWriter());
        combined.setErrorWriter(scriptContext.getErrorWriter());
        combined.setReader(scriptContext.getReader());

        return combined;
    }
}