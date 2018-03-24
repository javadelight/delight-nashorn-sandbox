package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;

public class CpuAbuseBracelessScriptProvider implements ParameterResolver {
    private final Iterator<String> iterator;
    //put any script here
    private String[] scripts = {
            "var x; \n while(true);",
            "var x=0;\nwhile (true) x++;\n",
            "while (true) {};"};
    //testCount is the count of scripts-1
    public static final int testCount = 2;

    CpuAbuseBracelessScriptProvider() {
        iterator = Arrays.stream(scripts).iterator();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isInstance("asd");
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return iterator.next();
    }
}
