package delight.nashornsandbox;

import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;

import javax.script.Bindings;
import javax.script.Invocable;
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
   * @param limit time limit in milliseconds
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
   *   greater than 0. Nashorn takes some memory at start, be generous and give
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
   * Sets the writer, when want to have output from writer function called in
   * JS script
   * 
   * @param writer the writer, eg. {@link StringWriter}
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
   * @throws ScriptCPUAbuseException when execution time exceeded (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occurs
   * @see #setMaxCPUTime(long)
   */
  Object eval(String js) throws ScriptCPUAbuseException, ScriptException;

  /**
   * Evaluates the JavaScript string.
   *
   * @param js the JavaScript script to be evaluated
   * @param bindings the Bindings to use for evaluation
   * @throws ScriptCPUAbuseException when execution time exceeded (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occurs
   * @see #setMaxCPUTime(long)
   */
  Object eval(String js,Bindings bindings) throws ScriptCPUAbuseException, ScriptException;

  /**
   * Evaluates the JavaScript string for a given script context
   *
   * @param js the JavaScript script to be evaluated
   * @param scriptContext the ScriptContext exposing sets of attributes in different scopes.
   * @throws ScriptCPUAbuseException when execution time exceeded (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occurs
   * @see #setMaxCPUTime(long)
   */
  Object eval(String js, ScriptContext scriptContext) throws ScriptCPUAbuseException, ScriptException;


  /**
   * Evaluates the JavaScript string for a given script context
   * 
   * @param js the JavaScript script to be evaluated
   * @param bindings the Bindings to use for evaluation
   * @param scriptContext the ScriptContext exposing sets of attributes in different scopes. 
   * @throws ScriptCPUAbuseException when execution time exceeded (when greater
   *      than 0 is set
   * @throws ScriptException when script syntax error occurs
   * @see #setMaxCPUTime(long)
   */
  Object eval(String js, ScriptContext scriptContext,Bindings bindings) throws ScriptCPUAbuseException, ScriptException;
  
  /**
   * Obtains the value of the specified JavaScript variable.
   */
  Object get(String variableName);
  
  /**
   * Allow Nashorn print and echo functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause effect.
   * </p>
   */
  void allowPrintFunctions(boolean v);
  
  /**
   * Allow Nashorn readLine and readFully functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause effect.
   * </p>
   */
  void allowReadFunctions(boolean v);
  
  /**
   * Allow Nashorn load and loadWithNewGlobal functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause effect.
   * </p>
   */
  void allowLoadFunctions(boolean v);
  
  /**
   * Allow Nashorn quit and exit functions.
   * <p>
   *   Only before first {@link #eval(String)} call cause effect.
   * </p>
   */
  void allowExitFunctions(boolean v);
  
  /**
   * Allow Nashorn globals object $ARG, $ENV, $EXEC, $OPTIONS, $OUT, $ERR and $EXIT.
   * <p>
   *   Only before first {@link #eval(String)} call cause effect.
   * </p>
   */
  void allowGlobalsObjects(boolean v);

  /**
   * Force, to check if all blocks are enclosed with curly braces "{}".
   * <p>
   *   Explanation: all loops (for, do-while, while, and if-else, and functions
   *   should use braces, because poison_pill() function will be inserted after
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
   *   Properly written code (even in bad intention) like:
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
   * The size of prepared statements LRU cache. Default 0 (disabled).
   * <p>
   *   Each statements when {@link #setMaxCPUTime(long)} is set is prepared to
   *   quit itself when time exceeded. To execute only once this procedure per
   *   statement set this value.
   * </p>
   * <p>
   *   When {@link #setMaxCPUTime(long)} is set 0, this value is ignored.
   * </p>
   * 
   * @param max the maximum number of statements in the LRU cache
   */
  void setMaxPreparedStatements(int max);
  
  /**
   * Create new bindings used to replace the state of the current script engine
   * <p>
   * 	This can be typically used to override ECMAScript "global" properties
   * </p>
   * 
   * @return
   */
  Bindings createBindings();

  /**
   * Returns an {@link Invocable} instance, so that method invocations are also sandboxed.
   * @return
   */
  Invocable getSandboxedInvocable();

  /**
   * Overwrites the cache for pre-processed javascript. Must be called before the first invocation of {@link #eval(String)}
   * and its overloads.
   * @param cache the new cache to use
   */
  void setScriptCache(SecuredJsCache cache);
}
