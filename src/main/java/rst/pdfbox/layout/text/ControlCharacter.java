package rst.pdfbox.layout.text;

import java.util.regex.Pattern;

/**
 * A control character represents the pattern to match and escape sequence for
 * character sequences with a special meaning. Currently there is newline for
 * all kinds of text, and bold and italic for markup.
 */
public class ControlCharacter implements CharSequence {

    private String description;
    private String charaterToEscape;

    protected ControlCharacter(final String description, 
	    final String charaterToEscape) {
	this.description = description;
	this.charaterToEscape = charaterToEscape;
    }
    /**
     * @return the character to escape, e.g. '*' for bold.
     */
    public String getCharacterToEscape() {
	return charaterToEscape;
    }

    /**
     * @return <code>true</code> if this control character must be escaped in
     *         text.
     */
    public boolean mustEscape() {
	return getCharacterToEscape() != null;
    }

    /**
     * Escapes the control character in the given text if necessary.
     * 
     * @param text
     *            the text to escape.
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
     * 
     * @param text
     *            the text to un-escape.
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
