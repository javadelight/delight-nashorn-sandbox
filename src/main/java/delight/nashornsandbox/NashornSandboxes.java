package delight.nashornsandbox;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.internal.NashornSandboxImpl;

@SuppressWarnings("all")
public class NashornSandboxes {
  public static NashornSandbox create() {
    return new NashornSandboxImpl();
  }
}
