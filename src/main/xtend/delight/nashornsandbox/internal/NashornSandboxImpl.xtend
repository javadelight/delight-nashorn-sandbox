package delight.nashornsandbox.internal

import delight.nashornsandbox.NashornSandbox
import java.util.HashSet
import java.util.Set
import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory

class NashornSandboxImpl implements NashornSandbox {
	
	val Set<String> allowedClasses
	
	def ScriptEngine createScriptEngine() {
		/*
		 * If eclipse shows an error here, see http://stackoverflow.com/a/10642163/270662
		 */
		val NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		
		factory.getScriptEngine(new SandboxClassFilter(allowedClasses));
	}
	
	new () {
		this.allowedClasses = new HashSet() 
	}
	
}