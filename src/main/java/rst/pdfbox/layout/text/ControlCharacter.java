package rst.pdfbox.layout.text;

import java.util.regex.Pattern;

/**
 * A control character represents the pattern to match and escape sequence for
 * character sequences with a special meaning. Currently there is newline for
 * all kinds of text, and bold and italic for markup.
 */
public class ControlCharacter implements CharSequence {

	/**
	 * An asterisk ('*') indicates switching of bold font mode in markup. It can
	 * be escaped with a backslash ('\').
	 */
	public static final ControlCharacter BOLD = new ControlCharacter("BOLD",
			"(?<!\\\\)(\\\\\\\\)*\\*", "*");
	/**
	 * An underscore ('_') indicates switching of italic font mode in markup. It can
	 * be escaped with a backslash ('\').
	 */
	public static final ControlCharacter ITALIC = new ControlCharacter(
			"ITALIC", "(?<!\\\\)(\\\\\\\\)*_", "_");
	/**
	 * LF ('\n') and CRLF ('\r\n') indicates a new line.
	 */
	public static final ControlCharacter NEWLINE = new ControlCharacter(
			"NEWLINE", "(\r\n|\n)", null);
	
	/**
	 * Unescapes the escape character backslash.
	 * @param text
	 * @return the unescaped text.
	 */
	public static String unescapeBackslash(final String text) {
		return text.replaceAll(Pattern.quote("\\\\"), "\\\\");
	}

	private String description;
	private Pattern pattern;
	private String charaterToEscape;

	private ControlCharacter(final String description, final String regex,
			final String charaterToEscape) {
		this.description = description;
		this.pattern = Pattern.compile(regex);
		this.charaterToEscape = charaterToEscape;
	}

	/**
	 * @return the pattern to match the control character (sequence).
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return the character to escape, e.g. '*' for {@link #BOLD}.
	 */
	public String getCharacterToEscape() {
		return charaterToEscape;
	}

	/**
	 * @return <code>true</code> if this control character must be escaped in text.
	 */
	public boolean mustEscape() {
		return getCharacterToEscape() != null;
	}

	/**
	 * Escapes the control character in the given text if necessary.
	 * @param text the text to escape.
	 * @return the escaped text.
	 */
	public String escape(final String text) {
		if (!mustEscape()) {
			return text;
		}
		return text.replaceAll(Pattern.quote(getCharacterToEscape()), "\\"
				+ getCharacterToEscape());
	}

	/**
	 * Un-escapes the control character in the given text if necessary.
	 * @param text the text to un-escape.
	 * @return the un-escaped text.
	 */
	public String unescape(final String text) {
		if (!mustEscape()) {
			return text;
		}
		return text.replaceAll("\\\\" + Pattern.quote(getCharacterToEscape()),
				getCharacterToEscape());
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
