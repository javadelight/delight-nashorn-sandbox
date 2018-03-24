package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;

public class MemoryAbuseScriptProvider implements ParameterResolver {
    private final Iterator<Pair<String, Integer>> iterator;
    //put any script here
    private Pair<String,Integer>[] scripts = new Pair[]{new Pair<>("\"var o={},i=0; while (true) {o[i++] = 'abc'}\"", 700 * 1024)};
    //testCount is the count of scripts-1
    public static final int testCount = 0;

    MemoryAbuseScriptProvider() {
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
