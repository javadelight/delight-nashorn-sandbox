package delight.nashornsandbox;

@SuppressWarnings("all")
public interface NashornSandbox {
  public abstract void allow(final Class<?> clazz);
  
  public abstract Object eval(final String js);
}
