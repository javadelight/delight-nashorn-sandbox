package delight.nashornsandbox;

import javax.script.ScriptContext;

@SuppressWarnings("all")
public interface NashornSandbox {
  public abstract void allow(final Class<?> clazz);
  
  public abstract Object eval(final String js);
  
  public abstract Object eval(final String js, final ScriptContext context);
}
