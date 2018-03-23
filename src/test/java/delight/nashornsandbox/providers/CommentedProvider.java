package delight.nashornsandbox.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.Iterator;

public class CommentedProvider implements ParameterResolver {
    private Iterator<Pair<String,String>> iterator;
    //put any script here
    @SuppressWarnings("unchecked")
    private Pair<String,String>[] scripts = new Pair[]{
            new Pair<>("var url = 'http://hello.com'", "var url = 'http://hello.com'"),
            new Pair<>("var url = \"http://hello.com\"", "var url = \"http://hello.com\""),
            new Pair<>("var url = 'http://hello.com';", "var url = 'http://hello.com';// mycomment"),
            new Pair<>("var url = 'http://hello.com'", "/* whatisthis */var url = 'http://hello.com'"),
    };
    //testCount is the count of scripts-1
    public static final int testCount = 3;

    CommentedProvider() {
        iterator = Arrays.stream(scripts).iterator();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isInstance(scripts[0]);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return iterator.next();
    }
}
