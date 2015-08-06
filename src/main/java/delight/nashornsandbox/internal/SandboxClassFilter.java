package delight.nashornsandbox.internal;

import java.util.Set;
import jdk.nashorn.api.scripting.ClassFilter;

@SuppressWarnings("all")
public class SandboxClassFilter implements ClassFilter {
  private final Set<String> allowed;
  
  @Override
  public boolean exposeToScripts(final String className) {
    return this.allowed.contains(className);
  }
  
  public SandboxClassFilter(final Set<String> allowed) {
    this.allowed = allowed;
  }
}
