package delight.nashornsandbox;

import delight.nashornsandbox.internal.NashornSandboxImpl;

/**
 * The Nashorn sandbox factory.
 *
 * <p>
 * Created on 2015-08-07
 * </p>
 * 
 * @author <a href="mailto:mxro@nowhere.com">mxro</a>
 * @version $Id$
 */
@SuppressWarnings("all")
public class NashornSandboxes {

	public static NashornSandbox create() {
		return new NashornSandboxImpl();
	}
	
	/**
	 * <p>Create a sandbox while supplying arguments for the engine such as '--no-java'.
	 * 
	 * @param args
	 * @return
	 */
	public static NashornSandbox create(String... args) {
		return new NashornSandboxImpl(args);
	}
}
