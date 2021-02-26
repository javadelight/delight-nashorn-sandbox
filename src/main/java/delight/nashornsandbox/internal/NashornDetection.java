package delight.nashornsandbox.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class NashornDetection {

    private static final Logger logger = LoggerFactory.getLogger(NashornDetection.class);

    private static final Class<?> JDK_NASHORN_ScriptObjectMirror_CLASS;
    private static final Class<?> JDK_NASHORN_NashornScriptEngineFactory_CLASS;
    private static final Class<?> JDK_NASHORN_ClassFilter_CLASS;

    private static final Class<?> STANDALONE_NASHORN_ScriptObjectMirror_CLASS;
    private static final Class<?> STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS;
    private static final Class<?> STANDALONE_NASHORN_ClassFilter_CLASS;

    static {
        JDK_NASHORN_ScriptObjectMirror_CLASS = findClass("jdk.nashorn.api.scripting.ScriptObjectMirror", "JDK-provided Nashorn not found");
        if (JDK_NASHORN_ScriptObjectMirror_CLASS != null) {
            JDK_NASHORN_NashornScriptEngineFactory_CLASS = findClass("jdk.nashorn.api.scripting.NashornScriptEngineFactory", "");
            JDK_NASHORN_ClassFilter_CLASS = findClass("jdk.nashorn.api.scripting.ClassFilter", "");
        } else {
            // no need to search for those and add more logs
            JDK_NASHORN_NashornScriptEngineFactory_CLASS = null;
            JDK_NASHORN_ClassFilter_CLASS = null;
        }

        STANDALONE_NASHORN_ScriptObjectMirror_CLASS = findClass("org.openjdk.nashorn.api.scripting.ScriptObjectMirror", "Standalone Nashorn not found");
        if (STANDALONE_NASHORN_ScriptObjectMirror_CLASS == null) {
            STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS = findClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory", "");
            STANDALONE_NASHORN_ClassFilter_CLASS = findClass("org.openjdk.nashorn.api.scripting.ClassFilter", "");
        } else {
            STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS = null;
            STANDALONE_NASHORN_ClassFilter_CLASS = null;
        }
        // TODO add a report of what was detected and what will be used
    }

    public static boolean isJDKNashornScriptObjectMirror(Object script) {
        return JDK_NASHORN_ScriptObjectMirror_CLASS != null && JDK_NASHORN_ScriptObjectMirror_CLASS.isInstance(script);
    }

    public static boolean isStandaloneNashornScriptObjectMirror(Object script) {
        return STANDALONE_NASHORN_ScriptObjectMirror_CLASS != null && STANDALONE_NASHORN_ScriptObjectMirror_CLASS.isInstance(script);
    }

    public static SandboxClassFilter createSandboxClassFilter() {
        // TODO allow to force one impl?
        if (JDK_NASHORN_ClassFilter_CLASS != null) {
            return new JdkNashornClassFilter();
        }
        if (STANDALONE_NASHORN_ClassFilter_CLASS != null) {
            return new StandaloneNashornClassFilter();
        }
        throw new IllegalStateException("Neither jdk.nashorn.api.scripting.ClassFilter or org.openjdk.nashorn.api.scripting.ClassFilter is present");
    }

    // actually returns an instance of either jdk.nashorn.api.scripting.ClassFilter or org.openjdk.nashorn.api.scripting.ClassFilter
    public static Class<?> getClassFilterClass() {
        if (JDK_NASHORN_ClassFilter_CLASS != null) {
            return JDK_NASHORN_ClassFilter_CLASS;
        } else {
            return STANDALONE_NASHORN_ClassFilter_CLASS;
        }
    }

    // actually returns an instance of either jdk.nashorn.api.scripting.NashornScriptEngineFactory or org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
    public static Object getNashornScriptEngineFactory() {
        try {
            if (JDK_NASHORN_NashornScriptEngineFactory_CLASS != null) {
                return JDK_NASHORN_NashornScriptEngineFactory_CLASS.getConstructor().newInstance();
            } else if (STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS != null) {
                return STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS.getConstructor().newInstance();
            }
            throw new IllegalStateException("Neither jdk.nashorn.api.scripting.NashornScriptEngineFactory or org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory is present");
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> findClass(String className, String message) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.debug(message);
            return null;
        }
    }
}
