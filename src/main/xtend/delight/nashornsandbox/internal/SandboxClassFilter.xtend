package delight.nashornsandbox.internal

import java.util.Set
import java.util.HashSet
import jdk.nashorn.api.scripting.ClassFilter

class SandboxClassFilter implements ClassFilter {
	
	final Set<String> allowed;
	
	override exposeToScripts(String className) {
		return allowed.contains(className)
	}

	def void add(String className) {
		this.allowed.add(className)
	}

	def void remove(String className) {
		this.allowed.remove(className)
	}

	def void clear() {
		this.allowed.clear()
	}

	def boolean contains(String className) {
		allowed.contains(className)
	}

	new() {
		this.allowed = new HashSet()
	}
}