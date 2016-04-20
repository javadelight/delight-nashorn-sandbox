package delight.nashornsandbox

import java.util.concurrent.ExecutorService

interface NashornSandbox {

	/**
	 * <p>Add a new class to the list of allowed classes.
	 * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
	 */
	def NashornSandbox allow(Class<?> clazz)

	/**
	 * Sets the maximum CPU time in milliseconds allowed for script execution.
	 */
	def NashornSandbox setMaxCPUTime(long limit)

	/**
	 * Specifies the executor service which is used to run scripts when a CPU time limit is specified.
	 */
	def NashornSandbox setExecutor(ExecutorService executor)
	
	def ExecutorService getExecutor()
	
	def Object eval(String js)

	
}