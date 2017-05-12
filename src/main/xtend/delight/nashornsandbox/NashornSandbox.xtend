package delight.nashornsandbox

import java.util.concurrent.ExecutorService

interface NashornSandbox {

	/**
	 * <p>Add a new class to the list of allowed classes.
	 * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
	 */
	def NashornSandbox allow(Class<?> clazz)

	/**
	 * <p>Remove a class from the list of allowed classes.
	 */
	def void disallow(Class<?> clazz)

	/**
	 * <p>Check if a class is in the list of allowed classes.
	 */
	def boolean isAllowed(Class<?> clazz)

	/**
	 * <p>Remove all classes from the list of allowed classes.
	 */
	def void disallowAllClasses()

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
	 * Enables debug output from the Sandbox.
	 */
	def void setDebug(boolean value)
	
	/**
	 * Obtains the value of the specified JavaScript variable.
	 */
	def Object get(String variableName)

	/**
	 * Allow Nashorn print and echo functions.
	 */
	def void allowPrintFunctions(boolean v)

	/**
	 * Allow Nashorn readLine and readFully functions.
	 */
	def void allowReadFunctions(boolean v)

	/**
	 * Allow Nashorn load and loadWithNewGlobal functions.
	 */
	def void allowLoadFunctions(boolean v)

	/**
	 * Allow Nashorn quit and exit functions.
	 */
	def void allowExitFunctions(boolean v)

	/**
	 * Allow Nashorn globals object $ARG, $ENV, $EXEC, $OPTIONS, $OUT, $ERR and $EXIT.
	 */
	def void allowGlobalsObjects(boolean v)
}