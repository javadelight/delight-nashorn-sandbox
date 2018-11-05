package delight.nashornsandbox;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptException;

@Ignore
public class TestPerformance {
    @Test
    public void noBindings() throws ScriptCPUAbuseException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();

        String js = "1==1";


        long start = System.nanoTime();
        for (int i = 0; i< 1000; ++i) {
            sandbox.eval(js);
        }
        System.out.println("No binding " + (((double)(System.nanoTime() - start))/1_000_000_000));
    }

    @Test
    public void emptyBindings() throws ScriptCPUAbuseException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();

        String js = "1==1";

        long start = System.nanoTime();
        for (int i = 0; i< 1000; ++i) {
            sandbox.eval(js, sandbox.createBindings());
        }
        System.out.println("Empty binding " + (((double)(System.nanoTime() - start))/1_000_000_000));
    }

    @Test
    public void iterateBindings() throws ScriptCPUAbuseException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();

        String js = "1==1";

        long start = System.nanoTime();
        for (int i = 0; i< 1000; ++i) {
            final Bindings bindings = sandbox.createBindings();
            bindings.put("i", i);
            sandbox.eval(js, bindings);
        }
        System.out.println("Iterate binding " + (((double)(System.nanoTime() - start))/1_000_000_000));
    }

    @Test
    public void alternateBindings() throws ScriptCPUAbuseException, ScriptException {
        NashornSandbox sandbox = NashornSandboxes.create();

        String js = "1==1";

        long start = System.nanoTime();
        for (int i = 0; i< 1000; ++i) {
            if (i%2 == 0)
                sandbox.eval(js, sandbox.createBindings());
            else {
                final Bindings bindings = sandbox.createBindings();
                bindings.put("a","b");
                sandbox.eval(js, bindings);
            }
        }
        System.out.println("Alternating binding " + (((double)(System.nanoTime() - start))/1_000_000_000));
    }
}
