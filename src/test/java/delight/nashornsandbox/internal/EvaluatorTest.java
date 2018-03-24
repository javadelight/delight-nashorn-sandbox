package delight.nashornsandbox.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

//wierd class name
@DisplayName("Testing EvaluateOperation")
public class EvaluatorTest {

    EvaluateOperation evaluateOperation;
    String js = "var Tested = /** @class */ (function () {\n" +
            "    function Tested(name) {\n" +
            "        this._name = name;\n" +
            "    }\n" +
            "    Tested.prototype.getName = function () {\n" +
            "        return this._name;\n" +
            "    };\n" +
            "    return Tested;\n" +
            "}());\n" +
            "var testedObject = new Tested(\"tested object\");\n";
    ScriptContext scriptContext;
    Bindings bindings;

    @BeforeEach
    void beforeEach() {
        scriptContext = mock(ScriptContext.class);
        bindings = mock(Bindings.class);
        evaluateOperation = new EvaluateOperation(js, scriptContext, bindings);
    }

    @Test
    @DisplayName("can return the javascript given to it")
    void returnsjs() {
        assertEquals(js, evaluateOperation.getJs());
    }

    @Test
    @DisplayName("can return the script context given to it")
    void returnsContext() {
        assertEquals(scriptContext, evaluateOperation.getScriptContext());
    }

    @Test
    @DisplayName("can return the binding given to it")
    void returnsBindings() {
        assertEquals(bindings, evaluateOperation.getBindings());
    }

}
