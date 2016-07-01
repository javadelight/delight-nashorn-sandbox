package delight.nashornsandbox

import java.util.concurrent.ExecutorService

interface NashornSandbox {

	/**
	 * <p>Add a new class to the list of allowed classes.
	 * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
	 */
	def NashornSandbox allow(Class<?> clazz)
	
	/**
	 * Will add a global variable available to all scripts executed with this sandbox.
	 */
	def NashornSandbox inject(String variableName, Object object)

	/**
	 * Sets the maximum CPU time in milliseconds allowed for script execution.
	 */
	def NashornSandbox setMaxCPUTime(long limit)

	/**
	 * Specifies the executor service which is used to run scripts when a CPU time limit is specified.
	 */
	def NashornSandbox setExecutor(ExecutorService executor)
	
	def ExecutorService getExecutor()
	
	/**
	 * Evaluates the string.
	 */
	def Object eval(String js)
	
	/**
	 * Obtains the value of the specified JavaScript variable.
	 */
	def Object get(String variableName)

	
}