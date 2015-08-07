package delight.nashornsandbox;

import java.util.concurrent.Executor;

@SuppressWarnings("all")
public interface NashornSandbox {
  /**
   * <p>Add a new class to the list of allowed classes.
   * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
   */
  public abstract void allow(final Class<?> clazz);
  
  /**
   * Sets the maximum CPU time in milliseconds allowed for script execution.
   */
  public abstract void setMaxCPUTime(final int limit);
  
  /**
   * Specifies the executor which is used to run scripts when a CPU time limit is specified.
   */
  public abstract void setExecutor(final Executor executor);
  
  public abstract Object eval(final String js);
}
