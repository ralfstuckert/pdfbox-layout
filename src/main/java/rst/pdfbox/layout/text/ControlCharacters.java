package rst.pdfbox.layout.text;

import java.awt.Color;
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
    public static interface ControlCharacterFactory<T extends ControlCharacter> {

	/**
	 * Creates the control character from the given matched pattern.
	 * 
	 * @param value
	 *            the matched pattern.
	 * @return the created character.
	 */
	T createControlCharacter(final String value);

	/**
	 * @return the pattern used to match the control character.
	 */
	Pattern getPattern();

	/**
	 * Escapes the pattern.
	 * 
	 * @param text
	 *            the text to escape.
	 * @return the escaped text.
	 */
	String escape(final String text);

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
    public static ControlCharacterFactory<BoldControlCharacter> BOLD_FACTORY = new StaticControlCharacterFactory<BoldControlCharacter>(
	    new BoldControlCharacter(), BoldControlCharacter.PATTERN);
    /**
     * The factory for italic control characters.
     */
    public static ControlCharacterFactory<ItalicControlCharacter> ITALIC_FACTORY = new StaticControlCharacterFactory<ItalicControlCharacter>(
	    new ItalicControlCharacter(), ItalicControlCharacter.PATTERN);
    /**
     * The factory for new line control characters.
     */
    public static ControlCharacterFactory<NewLineControlCharacter> NEWLINE_FACTORY = new StaticControlCharacterFactory<NewLineControlCharacter>(
	    new NewLineControlCharacter(), NewLineControlCharacter.PATTERN);
    /**
     * The factory for color control characters.
     */
    public static ControlCharacterFactory<ColorControlCharacter> COLOR_FACTORY = new ColorControlCharacterFactory();

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

	protected ColorControlCharacter(final String value) {
	    super("COLOR", ColorControlCharacterFactory.TO_ESCAPE);
	    String hex = value.substring(8, 14);
	    int r = Integer.parseUnsignedInt(hex.substring(0, 2), 16);
	    int g = Integer.parseUnsignedInt(hex.substring(2, 4), 16);
	    int b = Integer.parseUnsignedInt(hex.substring(4, 6), 16);
	    this.color = new Color(r, g, b);
	}

	public Color getColor() {
	    return color;
	}
    }

    private static class StaticControlCharacterFactory<T extends ControlCharacter>
	    implements ControlCharacterFactory<T> {

	private T controlCharacter;
	private Pattern pattern;

	public StaticControlCharacterFactory(final T controlCharacter,
		final Pattern pattern) {
	    this.controlCharacter = controlCharacter;
	    this.pattern = pattern;
	}

	@Override
	public T createControlCharacter(String value) {
	    return controlCharacter;
	}

	@Override
	public Pattern getPattern() {
	    return pattern;
	}

	@Override
	public String escape(String text) {
	    return controlCharacter.escape(text);
	}

	@Override
	public String unescape(String text) {
	    return controlCharacter.unescape(text);
	}

    }

    private static class ColorControlCharacterFactory implements
	    ControlCharacterFactory<ColorControlCharacter> {

	private final static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\{color:#\\p{XDigit}{6}\\}");

	private final static String TO_ESCAPE = "{";

	@Override
	public ColorControlCharacter createControlCharacter(String value) {
	    return new ColorControlCharacter(value);
	}

	@Override
	public Pattern getPattern() {
	    return PATTERN;
	}

	@Override
	public String escape(String text) {
	    return text.replaceAll(Pattern.quote(TO_ESCAPE), "\\" + TO_ESCAPE);
	}

	@Override
	public String unescape(String text) {
	    return text
		    .replaceAll("\\\\" + Pattern.quote(TO_ESCAPE), TO_ESCAPE);
	}

    }

}
