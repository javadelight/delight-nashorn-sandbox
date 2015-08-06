package delight.nashornsandbox;

@SuppressWarnings("all")
public interface NashornSandbox {
  /**
   * <p>Add a new class to the list of allowed classes.
   * <p>WARNING: Adding a new class, AFTER a script has been evaluated, will destroy the engine and recreate it. The script context will thus be lost.
   */
  public abstract void allow(final Class<?> clazz);
  
  public abstract Object eval(final String js);
}
