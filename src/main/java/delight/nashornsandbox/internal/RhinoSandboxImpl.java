package delight.nashornsandbox.internal;

import javax.script.ScriptEngine;

import delight.nashornsandbox.internal.JsSanitizer;
import delight.nashornsandbox.internal.NashornSandboxImpl;
import delight.nashornsandbox.internal.RhinoJsSanitizer;

public class RhinoSandboxImpl extends NashornSandboxImpl {
    public RhinoSandboxImpl(ScriptEngine engine, String... params) {
        super(engine, params);
    }

    @Override
    protected JsSanitizer getSanitizer() {
        if (sanitizer == null) {
            if (suppliedCache == null) {
                sanitizer = new RhinoJsSanitizer(scriptEngine, maxPreparedStatements);
            } else {
                sanitizer = new RhinoJsSanitizer(scriptEngine, suppliedCache);
            }
        }
        return sanitizer;
    }
}
