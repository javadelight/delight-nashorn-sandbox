package delight.nashornsandbox.internal

import delight.async.Value
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException
import java.util.HashSet
import java.util.Random
import java.util.Set
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror
import javax.script.ScriptException

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

		synchronized (this) {
			val resVal = new Value<Object>(null)
			val exceptionVal = new Value<Throwable>(null)

			val outerThread = Thread.currentThread

			val monitorThread = new MonitorThread(maxCPUTimeInMs * 1000000)

			if (exectuor == null) {
				throw new IllegalStateException(
					"When a CPU time limit is set, an executor needs to be provided by calling .setExecutor(...)")
			}

			exectuor.execute([
				try {
					val mainThread = Thread.currentThread

					monitorThread.threadToMonitor = Thread.currentThread
					println(Thread.currentThread)
					monitorThread.onInvalidHandler = [
						// println('do interrupt')
						mainThread.interrupt
						println(mainThread.interrupted)
					]

					if (js.contains("intCheckForInterruption")) {
						throw new IllegalArgumentException(
							'Script contains the illegal string [intCheckForInterruption]')
					}

					val jsBeautify = scriptEngine.eval('window.js_beautify;') as ScriptObjectMirror

					val String beautifiedJs = jsBeautify.call("beautify", js) as String

					val randomToken = Math.abs(new Random().nextInt)

					val securedJs = '''
						var InterruptTest = Java.type('«InterruptTest.name»');
						var isInterrupted = InterruptTest.isInterrupted;
						var intCheckForInterruption«randomToken» = function() {
							if (isInterrupted()) {
							    throw new Error('Interrupted«randomToken»')
							}
						};
					''' +
						beautifiedJs.replaceAll(';\\n', ';intCheckForInterruption' + randomToken + '();\n').
							replace(') {', ') {intCheckForInterruption' + randomToken + '();\n')

					monitorThread.start
					scriptEngine.eval(securedJs)

					try {
						val res = scriptEngine.eval(js)
						resVal.set(res)
					} catch (ScriptException e) {
						println("received exception "+e)
						if (e.message.contains("Interrupted" + randomToken)) {
							monitorThread.notifyOperationInterrupted

						}
					} finally {
						monitorThread.stopMonitor

						synchronized (NashornSandboxImpl.this) {
							NashornSandboxImpl.this.notify

						}
					}

				} catch (Throwable t) {
					
					exceptionVal.set(t)
					monitorThread.stopMonitor
					synchronized (NashornSandboxImpl.this) {
						NashornSandboxImpl.this.notify

					}
				}
			])

			this.wait

			if (monitorThread.CPULimitExceeded) {
				var notGraceful = ""
				if (!monitorThread.gracefullyInterrputed) {
					notGraceful = " The operation could not be gracefully interrupted."
				}
				throw new ScriptCPUAbuseException(
					"Script used more than the allowed [" + maxCPUTimeInMs + " ms] of CPU time. " + notGraceful,
					exceptionVal.get())
			}

			if (exceptionVal.get != null) {
				throw exceptionVal.get
			}

			resVal.get()

		}

	}

	override NashornSandbox setMaxCPUTime(int limit) {
		this.maxCPUTimeInMs = limit
		this
	}

	override NashornSandbox allow(Class<?> clazz) {
		allowedClasses.add(clazz.name)
		scriptEngine = null
		this
	}

	override NashornSandbox setExecutor(ExecutorService executor) {
		this.exectuor = executor
		this
	}

	override ExecutorService getExecutor() {
		this.exectuor
	}

	new() {
		this.allowedClasses = new HashSet()
		allow(InterruptTest)
	}

}