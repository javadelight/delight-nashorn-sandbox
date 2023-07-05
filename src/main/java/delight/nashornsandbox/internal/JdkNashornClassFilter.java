package delight.nashornsandbox.internal;

import org.openjdk.nashorn.api.scripting.ClassFilter;

public class JdkNashornClassFilter extends SandboxClassFilter implements ClassFilter {

    @Override
    public boolean exposeToScripts(final String className) {
        return super.exposeToScripts(className);
    }

}
