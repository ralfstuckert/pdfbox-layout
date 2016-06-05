package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.font.PDFont;

import rst.pdfbox.layout.text.ControlCharacters.ControlCharacterFactory;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.Enumerator;
import rst.pdfbox.layout.util.EnumeratorFactory;

/**
 * Container class for current supported indention control characters.
 */
public class IndentCharacters {

    /**
     * The factory for indent control characters.
     */
    public static ControlCharacterFactory INDENT_FACTORY = new IndentCharacterFactory();

    /**
     * Represent un-indention, means effectively indent of 0.
     */
    public static IndentCharacter UNINDENT_CHARACTER = new IndentCharacter("0",
	    "0", "pt");

    /**
     * An <code>--{7em}</code> indicates an indention of 7 characters in markup,
     * where the number, the unit, and the brackets are optional. Default
     * indention is 4 characters, default unit is <code>7em</code> It can be
     * escaped with a backslash ('\').
     */
    public static class IndentCharacter extends ControlCharacter {

	protected int level = 1;
	protected float indentWidth = 4;
	protected SpaceUnit indentUnit = SpaceUnit.em;

	protected IndentCharacter(final String level, final String indentWidth,
		final String indentUnit) {
	    super("INDENT", IndentCharacterFactory.TO_ESCAPE);
	    try {
		this.level = level == null ? 0 : level.length() + 1;
	    } catch (NumberFormatException e) {
	    }
	    try {
		this.indentUnit = indentUnit == null ? SpaceUnit.em : SpaceUnit
			.valueOf(indentUnit);
	    } catch (NumberFormatException e) {
	    }
	    float defaultIndent = this.indentUnit == SpaceUnit.em ? 4 : 10;
	    try {
		this.indentWidth = indentWidth == null ? defaultIndent
			: Integer.parseInt(indentWidth);
	    } catch (NumberFormatException e) {
	    }

	}

	/**
	 * @return the level of indention, where 0 means no indent.
	 */
	public int getLevel() {
	    return level;
	}

	/**
	 * @return the next label to use on a subsequent indent. Makes only
	 *         sense for enumerating indents.
	 */
	protected String nextLabel() {
	    return "";
	}

	/**
	 * Creates the actual {@link Indent} fragment from this control
	 * character.
	 * 
	 * @param fontSize
	 *            the current font size.
	 * @param font
	 *            the current font.
	 * @param color
	 *            the color to use.
	 * @return the new Indent.
	 * @throws IOException
	 *             by pdfbox
	 */
	public Indent createNewIndent(final float fontSize, final PDFont font,
		final Color color) throws IOException {
	    return new Indent(nextLabel(), level * indentWidth, indentUnit,
		    fontSize, font, Alignment.Right, color);
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
		    + ((indentUnit == null) ? 0 : indentUnit.hashCode());
	    result = prime * result + Float.floatToIntBits(indentWidth);
	    result = prime * result + level;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    IndentCharacter other = (IndentCharacter) obj;
	    if (indentUnit != other.indentUnit)
		return false;
	    if (Float.floatToIntBits(indentWidth) != Float
		    .floatToIntBits(other.indentWidth))
		return false;
	    if (level != other.level)
		return false;
	    return true;
	}

    }

    /**
     * An <code>-+{--:7em}</code> indicates a list indention of 7 characters in
     * markup, using <code>--</code> as the bullet. The number, the unit, bullet
     * character and the brackets are optional. Default indention is 4
     * characters, default unit is <code>em</code> and the default bullet
     * depends on {@link CompatibilityHelper#getBulletCharacter(int)}. It can be
     * escaped with a backslash ('\').
     */
    public static class ListCharacter extends IndentCharacter {

	protected String label;

	protected ListCharacter(String level, String indentWidth,
		String indentUnit, String bulletCharacter) {
	    super(level, indentWidth, indentUnit);
	    if (bulletCharacter != null) {
		label = bulletCharacter;
		if (!label.endsWith(" ")) {
		    label += " ";
		}
	    } else {
		label = CompatibilityHelper.getBulletCharacter(getLevel()) + " ";
	    }
	}

	@Override
	protected String nextLabel() {
	    return label;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result + ((label == null) ? 0 : label.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (!super.equals(obj))
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    ListCharacter other = (ListCharacter) obj;
	    if (label == null) {
		if (other.label != null)
		    return false;
	    } else if (!label.equals(other.label))
		return false;
	    return true;
	}

    }

    /**
     * An <code>-#{a):7em}</code> indicates an enumeration indention of 7
     * characters in markup, using <code>a)...b)...etc</code> as the
     * enumeration. The number, the unit, enumeration type/separator, and the
     * brackets are optional. Default indention is 4 characters, default unit is
     * <code>em</code>. Default enumeration are arabian numbers, the separator
     * depends on the enumerator by default ('.' for arabian). For available
     * enumerators see {@link EnumeratorFactory}.It can be escaped with a
     * backslash ('\').
     */
    public static class EnumerationCharacter extends IndentCharacter {

	protected Enumerator enumerator;
	protected String separator;

	protected EnumerationCharacter(String level, String indentWidth,
		String indentUnit, String enumerationType, String separator) {
	    super(level, indentWidth, indentUnit);

	    if (enumerationType == null) {
		enumerationType = "1";
	    }
	    enumerator = EnumeratorFactory.createEnumerator(enumerationType);
	    this.separator = separator != null ? separator : enumerator
		    .getDefaultSeperator();
	}

	@Override
	protected String nextLabel() {
	    String next = enumerator.next();
	    StringBuilder bob = new StringBuilder(next.length()
		    + separator.length() + 1);
	    bob.append(next);
	    bob.append(separator);
	    if (!separator.endsWith(" ")) {
		bob.append(" ");
	    }
	    return bob.toString();
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result
		    + ((enumerator == null) ? 0 : enumerator.hashCode());
	    result = prime * result
		    + ((separator == null) ? 0 : separator.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (!super.equals(obj))
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    EnumerationCharacter other = (EnumerationCharacter) obj;
	    if (enumerator == null) {
		if (other.enumerator != null)
		    return false;
	    } else if (other.enumerator == null) {
		return false;
	    } else if (!enumerator.getClass().equals(
		    other.enumerator.getClass()))
		return false;
	    if (separator == null) {
		if (other.separator != null)
		    return false;
	    } else if (!separator.equals(other.separator))
		return false;
	    return true;
	}

    }

    private static class IndentCharacterFactory implements
	    ControlCharacterFactory {

	private final static Pattern PATTERN = Pattern
		.compile("^-(!)|^([ ]*)-(-)(\\{(\\d*)(em|pt)?\\})?|^([ ]*)-(\\+)(\\{(.+)?:(\\d*)(em|pt)?\\})?|^([ ]*)-(#)(\\{((?!:).)?(.+)?:((\\d*))((em|pt))?\\})?");
	private final static Pattern UNESCAPE_PATTERN = Pattern
		.compile("^\\\\([ ]*-[-|+|#])");

	private final static String TO_ESCAPE = "--";

	@Override
	public ControlCharacter createControlCharacter(String text,
		Matcher matcher, final List<CharSequence> charactersSoFar) {
	    if ("!".equals(matcher.group(1))) {
		return UNINDENT_CHARACTER;
	    }

	    if ("-".equals(matcher.group(3))) {
		return new IndentCharacter(matcher.group(2), matcher.group(5),
			matcher.group(6));
	    }

	    if ("+".equals(matcher.group(8))) {
		return new ListCharacter(matcher.group(7), matcher.group(11),
			matcher.group(12), matcher.group(10));
	    }

	    if ("#".equals(matcher.group(14))) {
		return new EnumerationCharacter(matcher.group(13),
			matcher.group(18), matcher.group(20),
			matcher.group(16), matcher.group(17));
	    }

	    throw new IllegalArgumentException("unkown indention " + text);
	}

	@Override
	public Pattern getPattern() {
	    return PATTERN;
	}

	@Override
	public String unescape(String text) {
	    Matcher matcher = UNESCAPE_PATTERN.matcher(text);
	    if (!matcher.find()) {
		return text;
	    }
	    return matcher.group(1) + text.substring(matcher.end());
	}

	@Override
	public boolean patternMatchesBeginOfLine() {
	    return true;
	}

    }

    public static void main(String[] args) {
	Pattern PATTERN = Pattern//
		.compile("^-(!)|^([ ]*)-(-)(\\{(\\d*)(em|pt)?\\})?|^([ ]*)-(\\+)(\\{(.)?:(\\d*)(em|pt)?\\})?|^([ ]*)-(#)(\\{((?!:).)?(.+)?:((\\d*))((em|pt))?\\})?");
	Matcher matcher = PATTERN.matcher("  -#{d:3em}");
	System.out.println("matches: " + matcher.find());
	if (!matcher.matches()) {
	    System.err.println("exit");
	    return;
	}
	System.out.println("start: " + matcher.start());
	System.out.println("end: " + matcher.end());
	System.out.println("groups: " + matcher.groupCount());
	for (int i = 0; i < matcher.groupCount(); i++) {
	    System.out.println("group " + i + ": '" + matcher.group(i) + "'");
	}
	// 2 - -> 1: blanks, 4: size, 5: unit
	// 7 + -> 6: blanks, 9: sign, 10: size, 11: unit
	// 11 # -> 12: blanks, 15: number-sign, 16: size, 18: unit
    }
}
