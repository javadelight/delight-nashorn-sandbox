package delight.nashornsandbox;

import java.util.concurrent.ExecutorService;

@SuppressWarnings("all")
public interface NashornSandbox {
  /**
   * <p>Add a new class to the list of allowed classes.
   * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
   */
  public abstract NashornSandbox allow(final Class<?> clazz);
  
  /**
   * <p>Remove a class from the list of allowed classes.
   */
  public abstract void disallow(final Class<?> clazz);
  
  /**
   * <p>Check if a class is in the list of allowed classes.
   */
  public abstract boolean isAllowed(final Class<?> clazz);
  
  /**
   * <p>Remove all classes from the list of allowed classes.
   */
  public abstract void disallowAllClasses();
  
  /**
   * Will add a global variable available to all scripts executed with this sandbox.
   */
  public abstract NashornSandbox inject(final String variableName, final Object object);
  
  /**
   * Sets the maximum CPU time in milliseconds allowed for script execution.
   */
  public abstract NashornSandbox setMaxCPUTime(final long limit);
  
  /**
   * Specifies the executor service which is used to run scripts when a CPU time limit is specified.
   */
  public abstract NashornSandbox setExecutor(final ExecutorService executor);
  
  public abstract ExecutorService getExecutor();
  
  /**
   * Evaluates the string.
   */
  public abstract Object eval(final String js);
  
  /**
   * Obtains the value of the specified JavaScript variable.
   */
  public abstract Object get(final String variableName);
  
  /**
   * Allow Nashorn print and echo functions.
   */
  public abstract void allowPrintFunctions(final boolean v);
  
  /**
   * Allow Nashorn readLine and readFully functions.
   */
  public abstract void allowReadFunctions(final boolean v);
  
  /**
   * Allow Nashorn load and loadWithNewGlobal functions.
   */
  public abstract void allowLoadFunctions(final boolean v);
  
  /**
   * Allow Nashorn quit and exit functions.
   */
  public abstract void allowExitFunctions(final boolean v);
  
  /**
   * Allow Nashorn globals object $ARG, $ENV, $EXEC, $OPTIONS, $OUT, $ERR and $EXIT.
   */
  public abstract void allowGlobalsObjects(final boolean v);
}
