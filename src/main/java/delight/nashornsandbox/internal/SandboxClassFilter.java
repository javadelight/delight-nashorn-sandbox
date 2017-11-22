package delight.nashornsandbox.internal;

import java.util.HashSet;
import java.util.Set;

import jdk.nashorn.api.scripting.ClassFilter;

/**
 * The class Filter.
 *
 * <p>Created on 2015-08-07</p>

 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @author <a href="mailto:eduveks@gmail.com">Eduardo Velasques</a>
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
@SuppressWarnings("all")
class SandboxClassFilter implements ClassFilter {
  private final Set<Class<?>> allowed;
  private final Set<String> stringCache;
  
  @Override
  public boolean exposeToScripts(final String className) {
      return this.stringCache.contains(className);
  }
  
  public void add(final Class<?> clazz) {
    this.allowed.add(clazz);
    this.stringCache.add(clazz.getName());
  }
  
  public void remove(final Class<?> clazz) {
    this.allowed.remove(clazz);
    this.stringCache.remove(clazz.getName());
  }

  public void clear() {
    this.allowed.clear();
    this.stringCache.clear();
  }
  
  public boolean contains(final Class<?> clazz) {
    return this.allowed.contains(clazz);
  }
  
  public SandboxClassFilter() {
    this.allowed = new HashSet<>();
    this.stringCache = new HashSet<>();
  }
  
}
