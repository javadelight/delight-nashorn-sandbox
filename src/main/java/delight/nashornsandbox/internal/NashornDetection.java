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
        JDK_NASHORN_ScriptObjectMirror_CLASS = findClass("jdk.nashorn.api.scripting.ScriptObjectMirror", "JDK-provided Nashorn");
        if (JDK_NASHORN_ScriptObjectMirror_CLASS != null) {
            JDK_NASHORN_NashornScriptEngineFactory_CLASS = findClass("jdk.nashorn.api.scripting.NashornScriptEngineFactory", "JDK-provided Nashorn");
            JDK_NASHORN_ClassFilter_CLASS = findClass("jdk.nashorn.api.scripting.ClassFilter", "JDK-provided Nashorn");
        } else {
            // no need to search for those and add more logs
            JDK_NASHORN_NashornScriptEngineFactory_CLASS = null;
            JDK_NASHORN_ClassFilter_CLASS = null;
        }

        STANDALONE_NASHORN_ScriptObjectMirror_CLASS = findClass("org.openjdk.nashorn.api.scripting.ScriptObjectMirror", "Standalone Nashorn");
        if (STANDALONE_NASHORN_ScriptObjectMirror_CLASS != null) {
            STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS = findClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory", "Standalone Nashorn");
            STANDALONE_NASHORN_ClassFilter_CLASS = findClass("org.openjdk.nashorn.api.scripting.ClassFilter", "Standalone Nashorn");
        } else {
            STANDALONE_NASHORN_NashornScriptEngineFactory_CLASS = null;
            STANDALONE_NASHORN_ClassFilter_CLASS = null;
        }
        if (JDK_NASHORN_ScriptObjectMirror_CLASS == null && STANDALONE_NASHORN_ScriptObjectMirror_CLASS == null) {
            throw new RuntimeException("Neither JDK nor Standalone Nashorn was found. If running on JDK 15 or later, you must add standalone Nashorn to the classpath.");
        }
        logger.info("Delight-nashorn-sandbox detected that JDK Nashorn is {} and standalone Nashorn is {}, and will use {}",
                JDK_NASHORN_ScriptObjectMirror_CLASS != null ? "present" : "absent",
                STANDALONE_NASHORN_ScriptObjectMirror_CLASS != null ? "present" : "absent",
                JDK_NASHORN_ScriptObjectMirror_CLASS != null ? "JDK Nashorn" : "Standalone Nashorn"
        );
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
            return JdkNashornClassFilterCreator.createJdkNashornClassFilter();
        }
        if (STANDALONE_NASHORN_ClassFilter_CLASS != null) {
            return StandaloneNashornClassFilterCreator.createStandaloneNashornClassFilter();
        }
        throw new IllegalStateException("Neither jdk.nashorn.api.scripting.ClassFilter or org.openjdk.nashorn.api.scripting.ClassFilter is present");
    }

    // hide the references to the actual class in private inner classes, so the JVM won't ever load a bytecode that reference a class that is missing
    private static class JdkNashornClassFilterCreator {
        private static SandboxClassFilter createJdkNashornClassFilter() {
            return new JdkNashornClassFilter();
        }
    }

    private static class StandaloneNashornClassFilterCreator {
        private static SandboxClassFilter createStandaloneNashornClassFilter() {
            return new StandaloneNashornClassFilter();
        }

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
        } catch (UnsupportedClassVersionError e) {
            logger.debug("Class for {} is compiled for a more recent release of java: {}", message, e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.debug("Class for {} was not found", message);
        }
        return null;
    }
}
