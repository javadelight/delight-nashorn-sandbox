package delight.nashornsandbox.internal

import java.util.Set
import jdk.nashorn.api.scripting.ClassFilter

class SandboxClassFilter implements ClassFilter {
	
	final Set<String> allowed;
	
	override exposeToScripts(String className) {
		return allowed.contains(className)
	}
	
	new(Set<String> allowed) {
		this.allowed = allowed
	}
}