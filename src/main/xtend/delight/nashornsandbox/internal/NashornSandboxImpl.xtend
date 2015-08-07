package delight.nashornsandbox.internal

import delight.async.Value
import delight.nashornsandbox.NashornSandbox
import java.util.HashSet
import java.util.Random
import java.util.Set
import java.util.concurrent.ExecutorService
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException

class NashornSandboxImpl implements NashornSandbox {

	val Set<String> allowedClasses

	var ScriptEngine scriptEngine
	var Integer maxCPUTimeInMs = 0
	var ExecutorService exectuor

	def void assertScriptEngine() {
		if (scriptEngine != null) {
			return
		}

		/*
		 * If eclipse shows an error here, see http://stackoverflow.com/a/10642163/270662
		 */
		val NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

		scriptEngine = factory.getScriptEngine(new SandboxClassFilter(allowedClasses));

		scriptEngine.eval('var window = {};')
		scriptEngine.eval(BeautifyJs.CODE)
	}

	override Object eval(String js) {
		assertScriptEngine

		if (maxCPUTimeInMs == 0) {
			return scriptEngine.eval(js)
		}

		val resVal = new Value<Object>(null)
		val exceptionVal = new Value<Throwable>(null)

		val outerThread = Thread.currentThread

		val monitorThread = new MonitorThread(maxCPUTimeInMs * 1000)

		exectuor.execute([
			try {
				val mainThread = Thread.currentThread

				monitorThread.threadToMonitor = Thread.currentThread

				monitorThread.onInvalidHandler = [
					mainThread.interrupt
				]

				if (js.contains("intCheckForInterruption")) {
					throw new IllegalArgumentException('Script contains the illegal string [intCheckForInterruption]')
				}

				val jsBeautify = scriptEngine.eval('window.js_beautify;') as ScriptObjectMirror

				val String beautifiedJs = jsBeautify.call("beautify", js) as String

				val randomToken = Math.abs(new Random().nextInt)

				val securedJs = '''
					var InterruptTest = Java.type('«InterruptTest.name»');
					var isInterrupted = InterruptTest.isInterrupted;
					var intCheckForInterruption«randomToken» = function() {
						if (isInterrupted()) {
						    throw new Error('Interrupted')
						}
					};
				''' +
					beautifiedJs.replaceAll(';\\n', ';intCheckForInterruption' + randomToken + '();\n').replace(') {',
						') {intCheckForInterruption' + randomToken + '();\n')

				monitorThread.start
				scriptEngine.eval(securedJs)

				val res = scriptEngine.eval(js)

				monitorThread.stopMonitor

				resVal.set(res)

				outerThread.notify

			} catch (Throwable t) {
				exceptionVal.set(t)
				outerThread.notify
			}
		])

		Thread.wait

		if (exceptionVal.get != null) {
			throw exceptionVal.get
		}

		if (monitorThread.CPULimitExceeded) {
			throw new ScriptCPUAbuseException("", exceptionVal.get())
		}

		resVal.get()

	}

	override void setMaxCPUTime(int limit) {
		this.maxCPUTimeInMs = limit
	}

	override void allow(Class<?> clazz) {
		allowedClasses.add(clazz.name)
		scriptEngine = null
	}

	new() {
		this.allowedClasses = new HashSet()
		allow(InterruptTest)
	}

}