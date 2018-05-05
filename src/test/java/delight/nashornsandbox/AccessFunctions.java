package delight.nashornsandbox;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("Access functions")
public class AccessFunctions {
    NashornSandbox sandbox;

    @BeforeEach
    void beforeEach() {
        sandbox = NashornSandboxes.create();
    }

    @Nested
    @DisplayName("Get members")
    class simpleGet {

        @Test
        @DisplayName("Variables")
        void variable() throws ScriptException {
            sandbox.eval("var x = 12;");
            final Object _get = sandbox.get("x");
            assertEquals(12, _get);
        }

        @Test
        @DisplayName("Functions")
        void function() throws ScriptException {
            sandbox.eval("function callMe() { return 42; };");
            final Object _get = sandbox.get("callMe");
            assertEquals(42, ((ScriptObjectMirror) _get).call(this));
        }

        @Nested
        @DisplayName("Objects")
        class Objects {
            Object _get;
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
            @BeforeEach
            void beforeEach() throws ScriptException {
                sandbox.eval(js);
                _get = sandbox.get("testedObject");
            }
            @Test
            @DisplayName("Call function")
            void object() {
                assertEquals("tested object", ((ScriptObjectMirror) _get).callMember("getName"));
            }
            @Test
            @DisplayName("Get property")
            void variable() {
                assertEquals("tested object", ((ScriptObjectMirror) _get).getMember("_name"));
            }
        }
    }
}
