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
@SuppressWarnings("restriction")
class SandboxClassFilter implements ClassFilter {
  private final Set<Class<?>> allowed;
  private final Set<String> stringCache;

  public SandboxClassFilter() {
    allowed = new HashSet<>();
    stringCache = new HashSet<>();
  }

  //Constructor for testing.
  SandboxClassFilter(Set<Class<?>> allowed,Set<String> stringCache) {
    this.allowed = allowed;
    this.stringCache = stringCache;
  }

  @Override
  public boolean exposeToScripts(final String className) {
    return stringCache.contains(className);
  }
  
  public void add(final Class<?> clazz) {
    allowed.add(clazz);
    stringCache.add(clazz.getName());
  }
  
  public void remove(final Class<?> clazz) {
    allowed.remove(clazz);
    stringCache.remove(clazz.getName());
  }

  public void clear() {
    allowed.clear();
    stringCache.clear();
  }
  
  public boolean contains(final Class<?> clazz) {
    return allowed.contains(clazz);
  }

}
