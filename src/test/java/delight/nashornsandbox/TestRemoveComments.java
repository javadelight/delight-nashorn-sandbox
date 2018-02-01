package delight.nashornsandbox;

import org.junit.Test;

import delight.nashornsandbox.internal.RemoveComments;
import junit.framework.Assert;

public class TestRemoveComments {

	@Test
	public void test() {
		
		Assert.assertEquals("var url = 'http://hello.com'", RemoveComments.perform("var url = 'http://hello.com'"));
		
		Assert.assertEquals("var url = \"http://hello.com\"", RemoveComments.perform("var url = \"http://hello.com\""));
		
		Assert.assertEquals("var url = 'http://hello.com';", RemoveComments.perform("var url = 'http://hello.com';// mycomment"));
		
		Assert.assertEquals("var url = 'http://hello.com'", RemoveComments.perform("/* whatisthis */var url = 'http://hello.com'"));
		
	}
	
}
