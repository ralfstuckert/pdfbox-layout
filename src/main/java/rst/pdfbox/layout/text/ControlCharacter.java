package rst.pdfbox.layout.text;

import java.util.regex.Pattern;

/**
 * A control character represents the regex and esca
 *
 */
public class ControlCharacter implements CharSequence {

	public static final ControlCharacter BOLD = new ControlCharacter("BOLD", "(?<!\\\\)(\\\\\\\\)*\\*", "*");
	public static final ControlCharacter ITALIC = new ControlCharacter("ITALIC",  "(?<!\\\\)(\\\\\\\\)*_", "_");
	public static final ControlCharacter NEWLINE = new ControlCharacter("NEWLINE", "(\r\n|\n)", null);

	private String description;
	private Pattern pattern;
	private String charaterToEscape;
	
	private ControlCharacter(final String description, final String regex, final String charaterToEscape ) {
		this.description = description;
		this.pattern = Pattern.compile(regex);
		this.charaterToEscape = charaterToEscape;
	}
	
	
	
	public Pattern getPattern() {
		return pattern;
	}

	public String getCharaterToEscape() {
		return charaterToEscape;
	}

	public boolean mustEscape() {
		return getCharaterToEscape() != null;
	}
	
	public String escape(final String text) {
		if (!mustEscape()) {
			return text;
		}
		return text.replaceAll(Pattern.quote(getCharaterToEscape()), "\\" + getCharaterToEscape());
	}
	
	public String unescape(final String text) {
		if (!mustEscape()) {
			return text;
		}
		return text.replaceAll("\\\\" + Pattern.quote(getCharaterToEscape()), getCharaterToEscape());
	}
	
	@Override
	public int length() {
		return 0;
	}

	@Override
	public char charAt(int index) {
		throw new ArrayIndexOutOfBoundsException(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return null;
	}

	@Override
	public String toString() {
		return description;
	}
}
