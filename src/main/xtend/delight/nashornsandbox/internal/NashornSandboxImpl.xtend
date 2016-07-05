package delight.nashornsandbox.internal

import delight.async.Value
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.Random
import java.util.Set
import java.util.concurrent.ExecutorService
import javax.script.ScriptEngine
import javax.script.ScriptException
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror

class NashornSandboxImpl implements NashornSandbox {

	var SandboxClassFilter sandboxClassFilter
	val Map<String, Object> globalVariables

	var ScriptEngine scriptEngine
	var Long maxCPUTimeInMs = 0L
	var ExecutorService exectuor

	var allowPrintFunctions = false
	var allowReadFunctions = false
	var allowLoadFunctions = false
	var allowExitFunctions = false
	var allowGlobalsObjects = false

	def void assertScriptEngine() {
		if (scriptEngine != null) {
			return
		}

		/*
		 * If eclipse shows an error here, see http://stackoverflow.com/a/10642163/270662
		 */
		val NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

		scriptEngine = factory.getScriptEngine(sandboxClassFilter);

		scriptEngine.eval('var window = {};')
		scriptEngine.eval(BeautifyJs.CODE)
		for (entry : globalVariables.entrySet) {
			scriptEngine.put(entry.key, entry.value)
		}
		
		scriptEngine.eval("\n" +
				(if (!this.allowPrintFunctions) "" +
						"quit = function() {};\n" +
						"exit = function() {};\n"
				else "") +
				"\n" +
				(if (!this.allowPrintFunctions) "" +
						"print = function() {};\n" +
						"echo = function() {};\n"
				else "") +
				"\n" +
				(if (!this.allowReadFunctions) "" +
						"readFully = function() {};\n" +
						"readLine = function() {};\n"
				else "") +
				"\n" +
				(if (!this.allowLoadFunctions) "" +
						"load = function() {};\n" +
						"loadWithNewGlobal = function() {};\n"
				else "") +
				"\n" +
				(if (!this.allowGlobalsObjects) "" +
						"$ARG = null;\n" +
						"$ENV = null;\n" +
						"$EXEC = null;\n" +
						"$OPTIONS = null;\n" +
						"$OUT = null;\n" +
						"$ERR = null;\n" +
						"$EXIT = null;\n"
				else "") +
                "\n")
		
	}

	override Object eval(String js) {
		assertScriptEngine

		if (maxCPUTimeInMs == 0) {
			return scriptEngine.eval(js)
		}

		synchronized (this) {
			val resVal = new Value<Object>(null)
			val exceptionVal = new Value<Throwable>(null)

			val monitorThread = new MonitorThread(maxCPUTimeInMs * 1000000)

			if (exectuor == null) {
				throw new IllegalStateException(
					"When a CPU time limit is set, an executor needs to be provided by calling .setExecutor(...)")
			}

			val monitor = new Object()

			exectuor.execute([
				try {

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

					val mainThread = Thread.currentThread

					monitorThread.threadToMonitor = Thread.currentThread

					monitorThread.onInvalidHandler = [

						mainThread.interrupt

					]

					monitorThread.start

					try {
						val res = scriptEngine.eval(securedJs)
						resVal.set(res)
					} catch (ScriptException e) {
						if (e.message.contains("Interrupted" + randomToken)) {
							monitorThread.notifyOperationInterrupted

						} else {
							exceptionVal.set(e)
							monitorThread.stopMonitor
							synchronized (monitor) {
								monitor.notify

							}
							return;
						}
					} finally {
						monitorThread.stopMonitor

						synchronized (monitor) {
							monitor.notify

						}
					}

				} catch (Throwable t) {

					exceptionVal.set(t)
					monitorThread.stopMonitor
					synchronized (monitor) {
						monitor.notify

					}
				}
			])

			synchronized (monitor) {
				monitor.wait
			}

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

	override NashornSandbox setMaxCPUTime(long limit) {
		this.maxCPUTimeInMs = limit
		this
	}

	override NashornSandbox allow(Class<?> clazz) {
		sandboxClassFilter.add(clazz.name)
		this
	}

	override disallow(Class<?> clazz) {
		sandboxClassFilter.remove(clazz.name)
	}

	override isAllowed(Class<?> clazz) {
		sandboxClassFilter.contains(clazz.name)
	}

	override disallowAllClasses() {
		sandboxClassFilter.clear()
	}

	override NashornSandbox inject(String variableName, Object object) {
		this.globalVariables.put(variableName, object)
		if (!sandboxClassFilter.contains(object.class.name)) {
			allow(object.class)
		}
		if (scriptEngine != null) {
			scriptEngine.put(variableName, object)
		}
		this
	}

	override NashornSandbox setExecutor(ExecutorService executor) {
		this.exectuor = executor
		this
	}

	override ExecutorService getExecutor() {
		this.exectuor
	}
	
	override get(String variableName) {
		assertScriptEngine
		scriptEngine.get(variableName)
	}

	override allowPrintFunctions(boolean v) {
		this.allowPrintFunctions = v
	}

	override allowReadFunctions(boolean v) {
		this.allowReadFunctions = v
	}

	override allowLoadFunctions(boolean v) {
		this.allowLoadFunctions = v
	}

	override allowExitFunctions(boolean v) {
		this.allowExitFunctions = v
	}

	override allowGlobalsObjects(boolean v) {
		this.allowGlobalsObjects = v
	}
	
	new() {
		this.sandboxClassFilter = new SandboxClassFilter()
		this.globalVariables = new HashMap<String, Object>
		allow(InterruptTest)
	}
	
	

}
