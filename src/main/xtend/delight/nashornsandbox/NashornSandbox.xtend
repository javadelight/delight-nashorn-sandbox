package delight.nashornsandbox

import javax.script.ScriptContext

interface NashornSandbox {

	/**
	 * <p>Add a new class to the list of allowed classes.
	 * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
	 */
	def void allow(Class<?> clazz)

	def Object eval(String js)

	
}