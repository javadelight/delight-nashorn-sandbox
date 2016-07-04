package delight.nashornsandbox.tests

import delight.nashornsandbox.NashornSandboxes
import java.util.concurrent.Executors
import org.junit.Assert
import org.junit.Test

class TestManyEvalsAndInjections {

    @Test
    def void test() {

        val sandbox = NashornSandboxes.create()
        sandbox.inject("num", 10)
        sandbox.eval('res = num + 1;')
        Assert.assertEquals(11.0, sandbox.get("res"))
        sandbox.inject("str", "20")
        sandbox.eval('res = num + str;')
        Assert.assertEquals("1020", sandbox.get("res"))

        val sandboxInterruption = NashornSandboxes.create()
        sandboxInterruption.maxCPUTime = 50
        sandboxInterruption.executor = Executors.newSingleThreadExecutor
        sandboxInterruption.eval('res = 1;')
        sandboxInterruption.inject("num", 10)
        sandboxInterruption.eval('res = num + 1;')
        Assert.assertEquals(11.0, sandboxInterruption.get("res"))
        sandboxInterruption.inject("str", "20")
        sandboxInterruption.eval('res = num + str;')
        Assert.assertEquals("1020", sandboxInterruption.get("res"))
    }

}