package delight.nashornsandbox.exceptions;

/**
 * Exception is thrown when JS script abuse was detected.
 *
 * <p>Created on 2017.11.24</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class ScriptAbuseException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final boolean scriptKilled;
    
  public ScriptAbuseException(final String message, final boolean scriptKilled, 
      final Throwable throwable) {
    super(message, throwable);
    this.scriptKilled = scriptKilled;
  }

  /**
   * Check if script when asked exited nicely, or not.
   * <p>
   *   Note, killint java thread is very dangerous to VM health.
   * </p>  
   * 
   * @return <code>true</code> when evaluator thread was finished by 
   *      {@link Thread#stop()} method, <code>false</code> when only 
   *      {@link Thread#interrupt()} was used
   */
  public boolean isScriptKilled() {
    return scriptKilled;
  }
  
  @Override
  public String getMessage() {
    if(scriptKilled) {
      return super.getMessage() + " The operation could NOT be gracefully interrupted.";
    }
    else {
      return super.getMessage();
    }
  }

}
