package rst.pdfbox.layout.text;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container class for all control character factories.
 */
public class ControlCharacters {

    /**
     * Unescapes the escape character backslash.
     * 
     * @param text
     *            the text to escape.
     * @return the unescaped text.
     */
    public static String unescapeBackslash(final String text) {
	return text.replaceAll(Pattern.quote("\\\\"), "\\\\");
    }

    /**
     * A control character factory is used to create control characters on the
     * fly from the control pattern. This allows to parameterize the characters
     * as needed for e.g. colors.
     */
    public static interface ControlCharacterFactory {

	/**
	 * Creates the control character from the given matched pattern.
	 * 
	 * @param text
	 *            the parsed text.
	 * @param matcher
	 *            the matcher.
	 * @param charactersSoFar
	 *            the characters created so far.
	 * @return the created character.
	 */
	ControlCharacter createControlCharacter(final String text, final Matcher matcher, final List<CharSequence> charactersSoFar);

	/**
	 * @return the pattern used to match the control character.
	 */
	Pattern getPattern();

	/**
	 * Indicates if the pattern should be applied to the begin of line only.
	 * @return <code>true</code> if the pattern is to be applied at the begin of a line.
	 */
	boolean patternMatchesBeginOfLine();
	
	/**
	 * Unescapes the pattern.
	 * 
	 * @param text
	 *            the text to unescape.
	 * @return the unescaped text.
	 */
	String unescape(final String text);
	
    }

    /**
     * The factory for bold control characters.
     */
    public static ControlCharacterFactory BOLD_FACTORY = new StaticControlCharacterFactory(
	    new BoldControlCharacter(), BoldControlCharacter.PATTERN);
    /**
     * The factory for italic control characters.
     */
    public static ControlCharacterFactory ITALIC_FACTORY = new StaticControlCharacterFactory(
	    new ItalicControlCharacter(), ItalicControlCharacter.PATTERN);
    /**
     * The factory for new line control characters.
     */
    public static ControlCharacterFactory NEWLINE_FACTORY = new StaticControlCharacterFactory(
	    new NewLineControlCharacter(), NewLineControlCharacter.PATTERN);
    /**
     * The factory for color control characters.
     */
    public static ControlCharacterFactory COLOR_FACTORY = new ColorControlCharacterFactory();

    /**
     * An asterisk ('*') indicates switching of bold font mode in markup. It can
     * be escaped with a backslash ('\').
     */
    public static class BoldControlCharacter extends ControlCharacter {
	public static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\*");

	protected BoldControlCharacter() {
	    super("BOLD", "*");
	}
    }

    /**
     * An underscore ('_') indicates switching of italic font mode in markup. It
     * can be escaped with a backslash ('\').
     */
    public static class ItalicControlCharacter extends ControlCharacter {
	private static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*_");

	protected ItalicControlCharacter() {
	    super("ITALIC", "_");
	}
    }

    /**
     * LF ('\n') and CRLF ('\r\n') indicates a new line.
     */
    public static class NewLineControlCharacter extends ControlCharacter {
	private static Pattern PATTERN = Pattern.compile("(\r\n|\n)");

	protected NewLineControlCharacter() {
	    super("NEWLINE", null);
	}
    }

    /**
     * An <code>{color:#ee22aa}</code> indicates switching the color in markup,
     * where the color is given as hex RGB code (ee22aa in this case). It can be
     * escaped with a backslash ('\').
     */
    public static class ColorControlCharacter extends ControlCharacter {
	private Color color;

	protected ColorControlCharacter(final String hex) {
	    super("COLOR", ColorControlCharacterFactory.TO_ESCAPE);
	    int r = Integer.parseUnsignedInt(hex.substring(0, 2), 16);
	    int g = Integer.parseUnsignedInt(hex.substring(2, 4), 16);
	    int b = Integer.parseUnsignedInt(hex.substring(4, 6), 16);
	    this.color = new Color(r, g, b);
	}

	public Color getColor() {
	    return color;
	}
    }

    private static class StaticControlCharacterFactory
	    implements ControlCharacterFactory {

	private ControlCharacter controlCharacter;
	private Pattern pattern;

	public StaticControlCharacterFactory(final ControlCharacter controlCharacter,
		final Pattern pattern) {
	    this.controlCharacter = controlCharacter;
	    this.pattern = pattern;
	}

	@Override
	public ControlCharacter createControlCharacter(String text, Matcher matcher, final List<CharSequence> charactersSoFar) {
	    return controlCharacter;
	}

	@Override
	public Pattern getPattern() {
	    return pattern;
	}

	@Override
	public String unescape(String text) {
	    return controlCharacter.unescape(text);
	}

	@Override
	public boolean patternMatchesBeginOfLine() {
	    return false;
	}
	
    }

    private static class ColorControlCharacterFactory implements
	    ControlCharacterFactory {

	private final static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\{color:#(\\p{XDigit}{6})\\}");

	private final static String TO_ESCAPE = "{";

	@Override
	public ControlCharacter createControlCharacter(String text,
		Matcher matcher, final List<CharSequence> charactersSoFar) {
	    return new ColorControlCharacter(matcher.group(2));
	}
	
	@Override
	public Pattern getPattern() {
	    return PATTERN;
	}

	@Override
	public String unescape(String text) {
	    return text
		    .replaceAll("\\\\" + Pattern.quote(TO_ESCAPE), TO_ESCAPE);
	}

	@Override
	public boolean patternMatchesBeginOfLine() {
	    return false;
	}

    }

}
