class RhinoSandboxImpl extends NashornSandboxImpl {
    public RhinoSandboxImpl(javax.script.ScriptEngine engine, String... params) {
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