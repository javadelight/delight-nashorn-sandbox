package delight.nashornsandbox.exceptions;

/**
 * Exception thrown when script memory usage exides given limit.
 *
 * <p>Created on 2017.11.25</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class ScriptMemoryAbuseException extends ScriptAbuseException {
  private static final long serialVersionUID = 1L;

  public ScriptMemoryAbuseException(final String message, final boolean scriptKilled,
      final Throwable throwable) {
    super(message, scriptKilled, throwable);
  }
  
}
