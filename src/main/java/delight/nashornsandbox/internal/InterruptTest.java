package delight.nashornsandbox.internal;

import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class InterruptTest {
  public static boolean isInterrupted() {
    boolean _xblockexpression = false;
    {
      Thread _currentThread = Thread.currentThread();
      String _plus = ("test " + _currentThread);
      String _plus_1 = (_plus + " ");
      Thread _currentThread_1 = Thread.currentThread();
      boolean _isInterrupted = _currentThread_1.isInterrupted();
      String _plus_2 = (_plus_1 + Boolean.valueOf(_isInterrupted));
      InputOutput.<String>println(_plus_2);
      Thread _currentThread_2 = Thread.currentThread();
      _xblockexpression = _currentThread_2.isInterrupted();
    }
    return _xblockexpression;
  }
}
