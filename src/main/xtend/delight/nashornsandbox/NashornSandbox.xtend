package delight.nashornsandbox

import javax.script.ScriptContext

interface NashornSandbox {

	/**
	 * <p>Add a new class to the list of allowed classes.
	 * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
	 */
	def void allow(Class<?> clazz)

	/**
	 * Sets the maximum CPU time in milliseconds allowed for script execution.
	 */
	def void setMaxCPUTime(int limit)

	def Object eval(String js)

	
}