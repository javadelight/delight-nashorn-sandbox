package delight.nashornsandbox.internal;

import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class InterruptTest {
  public static boolean isInterrupted() {
    boolean _xblockexpression = false;
    {
      Thread _currentThread = Thread.currentThread();
      boolean _isInterrupted = _currentThread.isInterrupted();
      String _plus = ("test " + Boolean.valueOf(_isInterrupted));
      InputOutput.<String>println(_plus);
      Thread _currentThread_1 = Thread.currentThread();
      _xblockexpression = _currentThread_1.isInterrupted();
    }
    return _xblockexpression;
  }
}
