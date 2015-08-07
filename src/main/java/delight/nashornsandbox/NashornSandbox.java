package delight.nashornsandbox;

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
  
  public abstract Object eval(final String js);
}
