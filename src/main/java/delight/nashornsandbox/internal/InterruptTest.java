package delight.nashornsandbox.internal;

@SuppressWarnings("all")
public class InterruptTest {
  public static boolean isInterrupted() {
    Thread _currentThread = Thread.currentThread();
    return _currentThread.isInterrupted();
  }
}
