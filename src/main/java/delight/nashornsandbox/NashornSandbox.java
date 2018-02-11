package delight.nashornsandbox;

import java.io.Writer;
import java.util.concurrent.ExecutorService;

import javax.script.ScriptContext;
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
  void allow(Class<?> clazz);
  
  /**
   * Remove a class from the list of allowed classes.
   */
  void disallow(Class<?> clazz);
  
  /**
   * Check if a class is in the list of allowed classes.
   */
  boolean isAllowed(Class<?> clazz);
  
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
  void inject(String variableName, Object object);
  
  /**
   * Sets the maximum CPU time in milliseconds allowed for script execution.
   * <p>
   *   Note, {@link ExecutorService} should be also set when time is set greater
   *   than 0.
   * </p>
   *
   * @param limit time limit in miliseconds
   * @see #setExecutor(ExecutorService)
   */
  void setMaxCPUTime(long limit);
  
  /**
   * Sets the maximum memory in Bytes which JS executor thread can allocate.
   * <p>
   *   Note, thread memory usage is only approximation.
   * </p>
   * <p>
   *   Note, {@link ExecutorService} should be also set when memory limit is set
   *   greater than 0. Nashorn takes some memory at start, be denerous and give
   *   at least 1MB.
   * </p>
   * <p>
   *   Current implementation of this limit works only on Sun/Oracle JVM.
   * </p>
   * 
   * @param limit limit in bytes
   * @see com.sun.management.ThreadMXBean#getThreadAllocatedBytes(long)
   */
  void setMaxMemory(long limit);
  
  /**
   * Sets the writer, whem want to have output from writer funcion called in
   * JS script
   * 
   * @param writer the writer, eg. {@ling StringWriter}
   */
  void setWriter(Writer writer);
  
  /**
   * Specifies the executor service which is used to run scripts when a CPU time 
   * limit is specified.
   * 
   * @param executor the executor service
   * @see #setMaxCPUTime(long)
   */
  void setExecutor(ExecutorService executor);
  
  /**
   * Gets the current executor service.
   *  
   * @return current executor service
   */
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
  Object eval(String js) throws ScriptCPUAbuseException, ScriptException;
  
  /**
   * Evaluates the JavaScript string for a given script context
   * 
   * @param js the JavaScript script to be evaluated
   * @param js the JavaScript script to be evaluated
   * @param scriptContext the ScriptContext exposing sets of attributes in different scopes. 
   * @throws ScriptCPUAbuseException when execution time exided (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occures
   * @see #setMaxCPUTime(long)
   */
  Object eval(String js, ScriptContext scriptContext) throws ScriptCPUAbuseException, ScriptException;
  
  /**
   * Obtains the value of the specified JavaScript variable.
   */
  Object get(String variableName);
  
  /**
   * Allow Nashorn print and echo functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowPrintFunctions(boolean v);
  
  /**
   * Allow Nashorn readLine and readFully functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowReadFunctions(boolean v);
  
  /**
   * Allow Nashorn load and loadWithNewGlobal functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowLoadFunctions(boolean v);
  
  /**
   * Allow Nashorn quit and exit functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowExitFunctions(boolean v);
  
  /**
   * Allow Nashorn globals object $ARG, $ENV, $EXEC, $OPTIONS, $OUT, $ERR and $EXIT.
   * <p>
   *   Only before first {@link #eval(String)} call cause efect.
   * </p>
   */
  void allowGlobalsObjects(boolean v);

  /**
   * Force, to check if all blocks are enclosed with curly braces "{}".
   * <p>
   *   Explanation: all loops (for, do-while, while, and if-else, and functions
   *   should use braces, because poison_pill() function will be inserted afer
   *   each open brace "{", to ensure interruption checking. Otherwise simple
   *   code like:
   *   <pre>
   *     while(true) while(true) {
   *       // do nothing
   *     }
   *   </pre>
   *   or even:
   *   <pre>
   *     while(true)
   *   </pre>
   *   cause unbreakable loop, which force this sandbox to use {@link Thread#stop()}
   *   which make JVM unstable.
   * </p>
   * <p>
   *   Properly writen code (even in bad intention) like:
   *   <pre>
   *     while(true) { while(true) {
   *       // do nothing
   *     }}
   *   </pre>
   *   will be changed into:
   *   <pre>
   *     while(true) {poison_pill(); 
   *       while(true) {poison_pill();
   *         // do nothing
   *       }
   *     }
   *   </pre>
   *   which finish nicely when interrupted.
   * <p>
   *   For legacy code, this check can be turned off, but with no guarantee, the
   *   JS thread will gracefully finish when interrupted.
   * </p>
   * 
   * @param v <code>true</code> when sandbox should check if all required braces 
   *      are placed into JS code, <code>false</code> when no check should be 
   *      performed
   */
  void allowNoBraces(boolean v);
  
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
