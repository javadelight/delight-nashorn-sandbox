package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;

public class NiceScriptProvider implements ParameterResolver {
    private final Iterator<String> iterator;
    //put any script here
    private String[] scripts = {"var x = 1;\n",
            "var x = 1 + 1;" +
                    "for (var i=0;i<=1000;i++) {\n" +
                    "\t" +
                    "x = x + i\n" +
                    "}\n",
            " x;",
            "function preProcessor()\n" +
                    "{\n" +
                    "var map =  { \"inputparam\": \" for \" };\n"
                    + "}\n"
                    + "preProcessor();",
            "done = false;"};
    public static final int testCount = 3;

    NiceScriptProvider() {
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
