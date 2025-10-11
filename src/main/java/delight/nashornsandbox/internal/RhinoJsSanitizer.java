class RhinoJsSanitizer extends JsSanitizer {

    static String JS_INTERRUPTED_FUNCTION = "__if";

    protected RhinoJsSanitizer(javax.script.ScriptEngine scriptEngine, int maxPreparedStatements) {
        super(scriptEngine, maxPreparedStatements);
    }

    public RhinoJsSanitizer(javax.script.ScriptEngine scriptEngine, SecuredJsCache cache) {
        super(scriptEngine, cache);
    }

    @Override
    protected Function<String, String> injectAsFunction(Object injectScript) {
        // Check if the object is a callable Rhino Function
        if (injectScript instanceof org.mozilla.javascript.Function) {
            return script -> {
                Context cx = Context.enter();
                try {
                    org.mozilla.javascript.Function rhinoFunction = (org.mozilla.javascript.Function) injectScript;

                    // ⭐ Get the scope directly from the function object itself!
                    Scriptable scope = rhinoFunction.getParentScope();

                    // Fallback: if for some reason the function has no scope, create a default one.
                    // This is rare for user-defined functions.
                    if (scope == null) {
                        scope = cx.initStandardObjects();
                    }

                    Object[] args = new Object[]{script, JS_INTERRUPTED_FUNCTION + "();"};

                    // Call the JS function: call(context, scope, thisObject, args)
                    Object result = rhinoFunction.call(cx, scope, scope, args);

                    return Context.toString(result);
                } finally {
                    Context.exit();
                }
            };
        }

        // This part for handling a standard Java Function remains the same
        if (injectScript instanceof java.util.function.Function<?, ?>) {
            @SuppressWarnings("unchecked")
            java.util.function.Function<Object[], Object> javaFunction =
                    (java.util.function.Function<Object[], Object>) injectScript;

            return script -> (String) javaFunction.apply(new Object[]{script, JS_INTERRUPTED_FUNCTION + "();"});
        }

        throw new RuntimeException("Unsupported handler type for sanitizerJs: " + injectScript.getClass().getName());
    }
}