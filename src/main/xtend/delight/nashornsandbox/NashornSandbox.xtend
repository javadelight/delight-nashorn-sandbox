package delight.nashornsandbox

import javax.script.ScriptContext

interface NashornSandbox {
	
	def void allow(Class<?> clazz)
	 
	def Object eval(String js)
	
	def Object eval(String js, ScriptContext context)
}