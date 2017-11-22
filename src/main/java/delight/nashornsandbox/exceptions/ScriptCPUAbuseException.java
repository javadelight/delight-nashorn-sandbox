package delight.nashornsandbox.exceptions;

/**
 * Exception thrown when script runtime exides given limit.
 * 
 * <p>Created on 2015-08-07</p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @version $Id$
 */
public class ScriptCPUAbuseException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public ScriptCPUAbuseException(final String string, final Throwable throwable) {
    super(string, throwable);
  }
}
