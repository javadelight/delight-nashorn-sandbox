package delight.nashornsandbox

import javax.script.ScriptContext
import java.lang.reflect.Executable
import java.util.concurrent.Executor

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

	/**
	 * Specifies the executor which is used to run scripts when a CPU time limit is specified.
	 */
	def void setExecutor(Executor executor)
	
	def Object eval(String js)

	
}