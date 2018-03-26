package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;


public class InvalidScriptProvider implements ParameterResolver {
    private final Iterator<String> iterator;
    //put any script here
    private String[] scripts = {
            "function preProcessor()\n"+
                    "{\n"+
                    "var map =  { \"inputparam\": \"l\" }; for (;;); \n"+
                    "}\n"+
                    "preProcessor();","val x = 0"
           };
    public static final int testCount = 2;

    InvalidScriptProvider() {
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
