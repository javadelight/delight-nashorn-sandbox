package delight.nashornsandbox.exceptions;

@SuppressWarnings("all")
public class ScriptCPUAbuseException extends Exception {
  public ScriptCPUAbuseException(final String string, final Throwable throwable) {
    super(string, throwable);
  }
}
