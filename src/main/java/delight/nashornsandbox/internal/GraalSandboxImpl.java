package delight.nashornsandbox.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;


/**
 * Nashorn sandbox implementation for GraalJS
 * @author marcoellwanger
 */
public class GraalSandboxImpl extends NashornSandboxImpl {

	static final Logger LOG = LoggerFactory.getLogger(GraalSandboxImpl.class);
	
	public GraalSandboxImpl() {
		this(new String[0]);
	}
	
	public GraalSandboxImpl(String... params) {
		super(GraalJSScriptEngine.create(null, Context.newBuilder().allowExperimentalOptions(true).allowPolyglotAccess(PolyglotAccess.ALL).allowHostAccess(HostAccess.ALL)), params);
		Bindings bindings = this.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> this.sandboxClassFilter.getStringCache().contains(s));
	}
	
	@Override
	protected void resetEngineBindings() {
		
    }
	
	@Override
	public void allow(final Class<?> clazz) {
		super.allow(clazz);
		resetEngineBindings();
	}
	
	@Override
	public void disallow(final Class<?> clazz) {
		super.disallow(clazz);
		resetEngineBindings();
	}
}
