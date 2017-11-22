package delight.nashornsandbox;

import java.io.Writer;
import java.util.concurrent.ExecutorService;

import javax.script.ScriptException;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

/**
 * The Nashorn sandbox interface.
 *
 * <p>Created on 2015-08-06</p>
 * 
 * @author <a href="mailto:mxro@nowhere.com">mxro</a> 
 * @author <a href="mailto:dev@youness.org">Youness SAHOUANE</a> 
 * @author <a href="mailto:eduveks@gmail.com">Eduardo Velasques</a> 
 * @author <a href="mailto:philip.borgstrom@gmail.com">philipborg</a> 
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public interface NashornSandbox {
    
  /**
   * Add a new class to the list of allowed classes.
   */
  void allow(final Class<?> clazz);
  
  /**
   * Remove a class from the list of allowed classes.
   */
  void disallow(final Class<?> clazz);
  
  /**
   * Check if a class is in the list of allowed classes.
   */
  boolean isAllowed(final Class<?> clazz);
  
  /**
   * Remove all classes from the list of allowed classes.
   */
  void disallowAllClasses();
  
  /**
   * Will add a global variable available to all scripts executed with this sandbox.
   * 
   * @param variableName the name of the variable
   * @param object the value, can be <code>null</code>
   */
  void inject(final String variableName, final Object object);
  
  /**
   * Sets the maximum CPU time in milliseconds allowed for script execution.
   * <p>
   *   Note, {@link ExecutorService} should be also set when time set is greater
   *   than 0.
   * </p>
   *
   * @param limit time limit in miliseconds
   * @see #setExecutor(ExecutorService)
   */
  void setMaxCPUTime(final long limit);
  
  void setWriter(final Writer writer);
  
  /**
   * Specifies the executor service which is used to run scripts when a CPU time 
   * limit is specified.
   * 
   * @param executor the executor service
   * @see #setMaxCPUTime(long)
   */
  void setExecutor(final ExecutorService executor);
  
  ExecutorService getExecutor();
  
  /**
   * Evaluates the JavaScript string.
   * 
   * @param js the JavaScript script to be evaluated
   * @throws ScriptCPUAbuseException when execution time exided (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occures
   * @see #setMaxCPUTime(long)
   */
  Object eval(final String js) throws ScriptCPUAbuseException, ScriptException;
  
  /**
   * Obtains the value of the specified JavaScript variable.
   */
  Object get(final String variableName);
  
  /**
   * Allow Nashorn print and echo functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowPrintFunctions(final boolean v);
  
  /**
   * Allow Nashorn readLine and readFully functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowReadFunctions(final boolean v);
  
  /**
   * Allow Nashorn load and loadWithNewGlobal functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowLoadFunctions(final boolean v);
  
  /**
   * Allow Nashorn quit and exit functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowExitFunctions(final boolean v);
  
  /**
   * Allow Nashorn globals object $ARG, $ENV, $EXEC, $OPTIONS, $OUT, $ERR and $EXIT.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowGlobalsObjects(final boolean v);

  /**
   * The size of prepared statments LRU cache. Default 0 (disabled).
   * <p>
   *   Each statments when {@link #setMaxCPUTime(long)} is set is prepared to
   *   quit itself when time exided. To execute only once this procedure per
   *   statment set this value.
   * </p>
   * <p>
   *   When {@link #setMaxCPUTime(long)} is set 0, this value is ignored.
   * </p>
   * 
   * @param max the maximum number of statments in the LRU cache
   */
  void setMaxPerparedStatements(int max);
  
}
