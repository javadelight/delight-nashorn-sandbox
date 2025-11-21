package delight.nashornsandbox.internal;

import delight.nashornsandbox.SandboxScriptContext;

import javax.script.ScriptContext;

class SandboxScriptContextImpl implements SandboxScriptContext {

  private final ScriptContext context;

  SandboxScriptContextImpl(ScriptContext context) {
    this.context = context;
  }

  @Override
  public ScriptContext getContext() {
    return this.context;
  }

}
