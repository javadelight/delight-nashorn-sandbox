package delight.nashornsandbox;

import delight.nashornsandbox.internal.GraalSandboxImpl;
import delight.nashornsandbox.internal.NashornSandboxImpl;

/**
 * The Nashorn sandbox factory for GraalJS
 * @author marcoellwanger
 */
public class GraalSandboxes {
	
	public static NashornSandbox create() {
		return new GraalSandboxImpl();
	}
	
	public static NashornSandbox create(String... args) {
		return new GraalSandboxImpl(args);
	}
}
