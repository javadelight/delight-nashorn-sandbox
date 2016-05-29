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
  
  public abstract Object eval(final String js);
}
