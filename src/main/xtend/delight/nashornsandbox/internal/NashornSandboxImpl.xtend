package delight.nashornsandbox.internal

import delight.nashornsandbox.NashornSandbox
import java.util.HashSet
import java.util.Set
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptObjectMirror

class NashornSandboxImpl implements NashornSandbox {

	val Set<String> allowedClasses

	var ScriptEngine scriptEngine
	var Integer maxCPUTimeInMs = 0

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
		
		val monitorThread = new MonitorThread(maxCPUTimeInMs*1000, Thread.currentThread, [
			Thread.currentThread.interrupt
		])
		
		if (js.contains("intCheckForInterruption")) {
			throw new IllegalArgumentException('Script contains the illegal string [intCheckForInterruption]')
		}
		
		val  jsBeautify = scriptEngine.eval('window.js_beautify;') as ScriptObjectMirror

		val String beautifiedJs = jsBeautify.call("beautify", js) as String 
		
		val securedJs = '''
			var InterruptTest = Java.type('«InterruptTest.name»');
			var isInterrupted = InterruptTest.isInterrupted;
			var intCheckForInterruption = function() {
				if (isInterrupted()) {
				    throw new Error('Interrupted')
				}
			};
		'''+beautifiedJs.replaceAll(';\\n', ';intCheckForInterruption();\\n')
		
		println(securedJs)
		
		scriptEngine.eval(securedJs)

		val res = scriptEngine.eval(js)
		
		monitorThread.stopMonitor
		
		res
		
		
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