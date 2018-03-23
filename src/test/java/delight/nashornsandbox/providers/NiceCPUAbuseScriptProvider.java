package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;

public class NiceCPUAbuseScriptProvider implements ParameterResolver {
    private final Iterator<String> iterator;
    //put any script here
    private String[] scripts = {
            "var i = 0; \nwhile (true); {i=1;}",
            "val x = 0\n" +
                    ";for (var i=0;i<=100000000000;i++) {\n" +
                    "\t" +
                    "x = x + i;\n" +
                    "}\n",
            "var x = 0;\ndo{x++;\n}while(true);\n"};
    //testCount is the count of scripts-1
    public static final int testCount = 2;

    NiceCPUAbuseScriptProvider() {
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
