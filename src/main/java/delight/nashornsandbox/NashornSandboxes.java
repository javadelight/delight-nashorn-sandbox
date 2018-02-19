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
	
	/**
	 * <p>Creates a new sandbox instance.
	 * 
	 * @return The newly created sandbox instance.
	 */
	public static NashornSandbox create() {
		return new NashornSandboxImpl();
	}
	
	/**
	 * <p>Create a sandbox while supplying arguments for the engine such as '--no-java'.
	 * <p>More information on available parameters can be found in the following:
	 * <ul>
	 * <li><a href='http://hg.openjdk.java.net/jdk8/jdk8/nashorn/file/tip/docs/DEVELOPER_README'>Nashorn DEVELOPER README</a></li>
	 * <li><a href='https://github.com/JetBrains/jdk8u_nashorn/blob/master/src/jdk/nashorn/internal/runtime/resources/Options.properties'>Source Code Options.properties</a></li>
	 * <ul>
	 * @param args Options for initializing the Nashorn engine.
	 * @return A newly created sandbox instance.
	 */
	public static NashornSandbox create(String... args) {
		return new NashornSandboxImpl(args);
	}
}
