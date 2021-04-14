package delight.nashornsandbox;

import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.junit.Test;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;


public class TestIssue92 {
 
    @Test
  public void test_function_and_switch() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    sandbox.setExecutor(Executors.newSingleThreadExecutor());
    String function = "var foo = [{\"di\":0,\"da\":1},{\"di\":10,\"da\":11}];\n"+
"foo.map(function(toto){\n"+
  "switch (toto.da){\n"+
  "  case 0:\n"+
  "      return (\"ko\");\n"+
  "      break;\n"+
  "  case 1:\n"+
  "      return (\"ok1\");\n"+
  "      break;\n"+
  "  case 11:\n"+
  "      return (\"ok1\");\n"+
  "      break;\n"+
  "  default:\n"+ 
  "      return (\"ko\");\n"+
  "      break;\n"+
  "  }\n"+  
"});\n";
    sandbox.eval(function);
    sandbox.getExecutor().shutdown();
  }

  @Test
  public void test_while_and_switch() throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

    NashornSandbox sandbox = NashornSandboxes.create();
    String function = "var i=10;\n"+
"while(i>10){\n"+
  "switch (i){\n"+
  "  case 0:\n"+
  "      break;\n"+
  "  case 1:\n"+
  "      break;\n"+
  "  case 11:\n"+
  "      break;\n"+
  "  default:\n"+ 
  "      break;\n"+
  "  }\n"+  
"};\n";
    sandbox.eval(function);
    
  }
}
