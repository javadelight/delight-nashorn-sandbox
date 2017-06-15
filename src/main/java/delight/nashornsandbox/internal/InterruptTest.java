package delight.nashornsandbox.internal;

@SuppressWarnings("all")
public class InterruptTest {
  public static boolean isInterrupted() {
    return Thread.currentThread().isInterrupted();
  }
}
