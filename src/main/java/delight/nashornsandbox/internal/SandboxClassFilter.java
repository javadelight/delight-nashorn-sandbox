package delight.nashornsandbox.internal;

import java.util.HashSet;
import java.util.Set;
import jdk.nashorn.api.scripting.ClassFilter;

@SuppressWarnings("all")
public class SandboxClassFilter implements ClassFilter {
  private final Set<String> allowed;
  
  @Override
  public boolean exposeToScripts(final String className) {
    return this.allowed.contains(className);
  }
  
  public void add(final String className) {
    this.allowed.add(className);
  }
  
  public void remove(final String className) {
    this.allowed.remove(className);
  }
  
  public void clear() {
    this.allowed.clear();
  }
  
  public boolean contains(final String className) {
    return this.allowed.contains(className);
  }
  
  public SandboxClassFilter() {
    HashSet<String> _hashSet = new HashSet<String>();
    this.allowed = _hashSet;
  }
}
