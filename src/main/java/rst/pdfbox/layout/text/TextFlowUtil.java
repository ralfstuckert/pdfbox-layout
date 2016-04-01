package rst.pdfbox.layout.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextFlowUtil {

    /**
     * Creates a text flow from the given text. The text may contain line
     * breaks.
     * 
     * @param text
     *            the text
     * @param fontSize
     *            the font size to use.
     * @param font
     *            the font to use.
     * @return the created text flow.
     */
    public static TextFlow createTextFlow(final String text,
	    final float fontSize, final PDFont font) {
	final Iterable<CharSequence> parts = fromPlainText(text);
	return createTextFlow(parts, fontSize, font, font, font, font);
    }

    /**
     * Convenience alternative to
     * {@link #createTextFlowFromMarkup(String, float, PDFont, PDFont, PDFont, PDFont)}
     * which allows to specifies the fonts to use by using the {@link BaseFont}
     * enum.
     * 
     * @param markup
     *            the markup text.
     * @param fontSize
     *            the font size to use.
     * @param baseFont
     *            the base font describing the bundle of
     *            plain/blold/italic/bold-italic fonts.
     * @return the created text flow.
     */
    public static TextFlow createTextFlowFromMarkup(final String markup,
	    final float fontSize, final BaseFont baseFont) {
	return createTextFlowFromMarkup(markup, fontSize,
		baseFont.getPlainFont(), baseFont.getBoldFont(),
		baseFont.getItalicFont(), baseFont.getBoldItalicFont());
    }

    /**
     * Creates a text flow from the given text. The text may contain line
     * breaks, and also supports some markup for creating bold and italic fonts.
     * The following raw text
     * 
     * <pre>
     * Markup supports *bold*, _italic_, and *even _mixed* markup_.
     * </pre>
     * 
     * is rendered like this:
     * 
     * <pre>
     * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</b> markup</em>.
     * </pre>
     * 
     * Use backslash to escape special characters '*', '_' and '\' itself:
     * 
     * <pre>
     * Escape \* with \\\* and \_ with \\\_ in markup.
     * </pre>
     * 
     * is rendered like this:
     * 
     * <pre>
     * Escape * with \* and _ with \_ in markup.
     * </pre>
     * 
     * @param markup
     *            the markup text.
     * @param fontSize
     *            the font size to use.
     * @param plainFont
     *            the plain font.
     * @param boldFont
     *            the bold font.
     * @param italicFont
     *            the italic font.
     * @param boldItalicFont
     *            the bold-italic font.
     * @return the created text flow.
     */
    public static TextFlow createTextFlowFromMarkup(final String markup,
	    final float fontSize, final PDFont plainFont,
	    final PDFont boldFont, final PDFont italicFont,
	    final PDFont boldItalicFont) {
	final Iterable<CharSequence> parts = fromMarkup(markup);
	return createTextFlow(parts, fontSize, plainFont, boldFont, italicFont,
		boldItalicFont);
    }

    /**
     * Actually creates the text flow from the given (markup) text.
     * 
     * @param parts
     *            the parts to create the text flow from.
     * @param fontSize
     *            the font size to use.
     * @param plainFont
     *            the plain font.
     * @param boldFont
     *            the bold font.
     * @param italicFont
     *            the italic font.
     * @param boldItalicFont
     *            the bold-italic font.
     * @return the created text flow.
     */
    protected static TextFlow createTextFlow(
	    final Iterable<CharSequence> parts, final float fontSize,
	    final PDFont plainFont, final PDFont boldFont,
	    final PDFont italicFont, final PDFont boldItalicFont) {
	final TextFlow result = new TextFlow();
	boolean bold = false;
	boolean italic = false;
	for (final CharSequence fragment : parts) {
	    if (fragment instanceof ControlCharacter) {
		if (fragment == ControlCharacter.NEWLINE) {
		    result.add(new NewLine(fontSize));
		}
		if (fragment == ControlCharacter.BOLD) {
		    bold = !bold;
		}
		if (fragment == ControlCharacter.ITALIC) {
		    italic = !italic;
		}
	    } else {
		PDFont font = getFont(bold, italic, plainFont, boldFont,
			italicFont, boldItalicFont);
		StyledText styledText = new StyledText(fragment.toString(),
			fontSize, font);
		result.add(styledText);
	    }
	}
	return result;
    }

    /**
     * @return the appropriate font to use.
     */
    protected static PDFont getFont(boolean bold, boolean italic,
	    final PDFont plainFont, final PDFont boldFont,
	    final PDFont italicFont, final PDFont boldItalicFont) {
	PDFont font = plainFont;
	if (bold && !italic) {
	    font = boldFont;
	} else if (!bold && italic) {
	    font = italicFont;
	} else if (bold && italic) {
	    font = boldItalicFont;
	}
	return font;
    }

    /**
     * Creates a char sequence where new-line is replaced by the corresponding
     * {@link ControlCharacter}.
     * 
     * @param text
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromPlainText(final CharSequence text) {
	return fromPlainText(Collections.singleton(text));
    }

    /**
     * Creates a char sequence where new-line is replaced by the corresponding
     * {@link ControlCharacter}.
     * 
     * @param text
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromPlainText(
	    final Iterable<CharSequence> text) {
	return splitByControlCharacter(ControlCharacter.NEWLINE, text, true);
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     * 
     * @param text
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(final CharSequence markup) {
	return fromMarkup(Collections.singleton(markup));
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     * 
     * @param text
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(
	    final Iterable<CharSequence> markup) {
	Iterable<CharSequence> text = markup;
	text = splitByControlCharacter(ControlCharacter.BOLD, text, false);
	text = splitByControlCharacter(ControlCharacter.ITALIC, text, false);
	text = splitByControlCharacter(ControlCharacter.NEWLINE, text, true);
	return text;
    }

    /**
     * Splits the sequence by the given control character and replaces its
     * markup representation by the {@link ControlCharacter}.
     * 
     * @param ctrl
     *            the control character to split by.
     * @param markup
     *            the markup to split.
     * @param unescapeBackslash
     *            indicates if backslash should be unescaped ('\\' to '\').
     * @return the splitted and replaced sequence.
     */
    protected static Iterable<CharSequence> splitByControlCharacter(
	    ControlCharacter ctrl, final Iterable<CharSequence> markup,
	    final boolean unescapeBackslash) {
	List<CharSequence> result = new ArrayList<CharSequence>();
	for (CharSequence current : markup) {
	    if (current instanceof String) {
		String string = (String) current;
		String[] parts = ctrl.getPattern().split(string, -1);
		for (int i = 0; i < parts.length; i++) {
		    if (i > 0) {
			result.add(ctrl);
		    }
		    if (!parts[i].isEmpty()) {
			String unescaped = ctrl.unescape(parts[i]);
			if (unescapeBackslash) {
			    unescaped = ControlCharacter
				    .unescapeBackslash(unescaped);
			}
			result.add(unescaped);
		    }
		}
	    } else {
		result.add(current);
	    }
	}
	return result;
    }

}
