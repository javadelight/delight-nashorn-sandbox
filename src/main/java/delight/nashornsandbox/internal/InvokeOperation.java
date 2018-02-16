package delight.nashornsandbox.internal;

import javax.script.Invocable;
import javax.script.ScriptEngine;

public class InvokeOperation implements ScriptEngineOperation {

	private final Object thisObj;
	private final String name;
	private final Object[] args;

	public InvokeOperation(Object thisObj, String name, Object[] args) {
		this.thisObj = thisObj;
		this.name = name;
		this.args = args;
	}

	@Override
	public Object executeScriptEngineOperation(ScriptEngine scriptEngine) throws Exception {
		if  (thisObj == null) {
			return ((Invocable)scriptEngine).invokeFunction(name, args);
		} else {
			return ((Invocable)scriptEngine).invokeMethod(thisObj, name, args);
		}
	}

}
