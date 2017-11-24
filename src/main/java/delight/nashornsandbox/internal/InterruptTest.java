package delight.nashornsandbox.internal;

/**
 * Class avaliable in JavaScript engine to check if engine thread is interrupted.
 *
 * <p>Created on 2015-08-07</p>
 *
 * @author <a href="mailto:mxro@nowhere.com>mxro</a>
 * @version $Id$
 */
@SuppressWarnings("all")
public class InterruptTest {
  public static void test() throws InterruptedException {
    if(Thread.interrupted()) {
        throw new InterruptedException();
    }
  }
}
