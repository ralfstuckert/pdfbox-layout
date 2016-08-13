package rst.pdfbox.layout.text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rst.pdfbox.layout.text.Annotations.AnchorAnnotation;
import rst.pdfbox.layout.text.Annotations.HyperlinkAnnotation;
import rst.pdfbox.layout.text.Annotations.HyperlinkAnnotation.LinkStyle;
import rst.pdfbox.layout.text.ControlCharacters.ControlCharacterFactory;

public class AnnotationCharacters {

    /**
     * The factory for hyperlink control characters.
     */
    public static ControlCharacterFactory HYPERLINK_FACTORY = new HyperlinkControlCharacterFactory();

    /**
     * The factory for anchor control characters.
     */
    public static ControlCharacterFactory ANCHOR_FACTORY = new AnchorControlCharacterFactory();

    private static class HyperlinkControlCharacterFactory implements
	    ControlCharacterFactory {

	private final static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\{link(:(ul|none))?(\\[(([^}]+))\\])?\\}");

	private final static String TO_ESCAPE = "{";

	@Override
	public ControlCharacter createControlCharacter(String text,
		Matcher matcher, final List<CharSequence> charactersSoFar) {
	    return new HyperlinkControlCharacter(matcher.group(5),
		    matcher.group(3));
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

    private static class AnchorControlCharacterFactory implements
	    ControlCharacterFactory {

	private final static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\{anchor:((\\w+))\\}");

	private final static String TO_ESCAPE = "{";

	@Override
	public ControlCharacter createControlCharacter(String text,
		Matcher matcher, final List<CharSequence> charactersSoFar) {
	    return new AnchorControlCharacter(matcher.group(2));
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

    /**
     * Common base class for annotation control characters.
     */
    public static abstract class AnnotationControlCharacter<T extends Annotation>
	    extends ControlCharacter {

	protected AnnotationControlCharacter(final String description,
		final String charaterToEscape) {
	    super(description, charaterToEscape);
	}

	/**
	 * @return the associated annotation.
	 */
	public abstract T getAnnotation();

	/**
	 * @return the type of the annotation.
	 */
	public abstract Class<T> getAnnotationType();

    }

    /**
     * A <code>{link:#title1}</code> indicates an internal link to the
     * {@link #AnchorControlCharacter anchor} <code>title1</code>. Any other
     * link (not starting with <code>#</code> will be treated as an external
     * link. It can be escaped with a backslash ('\').
     */
    public static class HyperlinkControlCharacter extends
	    AnnotationControlCharacter<HyperlinkAnnotation> {
	private HyperlinkAnnotation hyperlink;

	protected HyperlinkControlCharacter(final String hyperlink,
		final String linkStyle) {
	    super("HYPERLINK", HyperlinkControlCharacterFactory.TO_ESCAPE);
	    if (hyperlink != null) {
		LinkStyle style = LinkStyle.ul;
		if (linkStyle != null) {
		    style = LinkStyle.valueOf(linkStyle);
		}
		this.hyperlink = new HyperlinkAnnotation(hyperlink, style);
	    }
	}

	@Override
	public HyperlinkAnnotation getAnnotation() {
	    return hyperlink;
	}

	@Override
	public Class<HyperlinkAnnotation> getAnnotationType() {
	    return HyperlinkAnnotation.class;
	}
    }

    /**
     * An <code>{color:#ee22aa}</code> indicates switching the color in markup,
     * where the color is given as hex RGB code (ee22aa in this case). It can be
     * escaped with a backslash ('\').
     */
    public static class AnchorControlCharacter extends
	    AnnotationControlCharacter<AnchorAnnotation> {
	private AnchorAnnotation anchor;

	protected AnchorControlCharacter(final String anchor) {
	    super("ANCHOR", AnchorControlCharacterFactory.TO_ESCAPE);
	    this.anchor = new AnchorAnnotation(anchor);
	}

	@Override
	public AnchorAnnotation getAnnotation() {
	    return anchor;
	}

	@Override
	public Class<AnchorAnnotation> getAnnotationType() {
	    return AnchorAnnotation.class;
	}

    }

    public static void main(String[] args) {
	Pattern PATTERN = Pattern//
		.compile("(?<!\\\\)(\\\\\\\\)*\\{link(:(ul|none))?(\\[(([^}]+))\\])?\\}");
	Matcher matcher = PATTERN.matcher("{link:none[sdfsfd]}");
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
