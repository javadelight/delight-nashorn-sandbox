package delight.nashornsandbox.internal;

/**
 * Based on
 * https://codegolf.stackexchange.com/questions/48326/remove-single-line-and-multiline-comments-from-string
 * 
 * @author adminuser
 *
 */
public class RemoveComments {

	public static final int DEFAULT = 1;
	public static final int ESCAPE = 2;
	public static final int STRING = 3;
	public static final int ONE_LINE = 4;
	public static final int MULTI_LINE = 5;

	public static String perform(String s) {
		StringBuilder out = new StringBuilder();
		int mod = DEFAULT;
		for (int i = 0; i < s.length(); i++) {
			String substring = s.substring(i, Math.min(i + 2, s.length()));
			char c = s.charAt(i);
			switch (mod) {
			case DEFAULT: // default
				mod = substring.equals("/*") ? MULTI_LINE
						: substring.equals("//") ? ONE_LINE : c == '"' || c =='\'' ? STRING : DEFAULT;
				break;
			case STRING: // string
				mod = c == '"' || c == '\'' ? DEFAULT : c == '\\' ? ESCAPE : STRING;
				break;
			case ESCAPE: // string
				mod = STRING;
				break;
			case ONE_LINE: // one line comment
				mod = c == '\n' ? DEFAULT : ONE_LINE;
				continue;
			case MULTI_LINE: // multi line comment
				mod = substring.equals("*/") ? DEFAULT : MULTI_LINE;
				i += mod == DEFAULT ? 1 : 0;
				continue;
			}
			if (mod < 4)
				out.append(c);
		}

		return out.toString();
	}

}
