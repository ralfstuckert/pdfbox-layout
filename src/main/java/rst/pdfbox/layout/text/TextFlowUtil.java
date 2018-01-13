package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

import org.apache.pdfbox.pdmodel.font.PDFont;

import rst.pdfbox.layout.text.ControlCharacters.BoldControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.ColorControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.ControlCharacterFactory;
import rst.pdfbox.layout.text.ControlCharacters.ItalicControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.MetricsControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.NewLineControlCharacter;
import rst.pdfbox.layout.text.IndentCharacters.IndentCharacter;
import rst.pdfbox.layout.text.annotations.AnnotatedStyledText;
import rst.pdfbox.layout.text.annotations.Annotation;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacter;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacterFactory;

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
     * @throws IOException
     *             by pdfbox
     */
    public static TextFlow createTextFlow(final String text,
	    final float fontSize, final PDFont font) throws IOException {
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
     * @throws IOException
     *             by pdfbox
     */
    public static TextFlow createTextFlowFromMarkup(final String markup,
	    final float fontSize, final BaseFont baseFont) throws IOException {
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
     * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</em> markup</b>.
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
     * @throws IOException
     *             by pdfbox
     */
    public static TextFlow createTextFlowFromMarkup(final String markup,
	    final float fontSize, final PDFont plainFont,
	    final PDFont boldFont, final PDFont italicFont,
	    final PDFont boldItalicFont) throws IOException {
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
     * @throws IOException
     *             by pdfbox
     */
    protected static TextFlow createTextFlow(
	    final Iterable<CharSequence> parts, final float fontSize,
	    final PDFont plainFont, final PDFont boldFont,
	    final PDFont italicFont, final PDFont boldItalicFont)
	    throws IOException {
	final TextFlow result = new TextFlow();
	boolean bold = false;
	boolean italic = false;
	Color color = Color.black;
	MetricsControlCharacter metricsControl = null;
	Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
	Stack<IndentCharacter> indentStack = new Stack<IndentCharacter>();
	for (final CharSequence fragment : parts) {

	    if (fragment instanceof ControlCharacter) {
		if (fragment instanceof NewLineControlCharacter) {
		    result.add(new NewLine(fontSize));
		}
		if (fragment instanceof BoldControlCharacter) {
		    bold = !bold;
		}
		if (fragment instanceof ItalicControlCharacter) {
		    italic = !italic;
		}
		if (fragment instanceof ColorControlCharacter) {
		    color = ((ColorControlCharacter) fragment).getColor();
		}
		if (fragment instanceof AnnotationControlCharacter) {
		    AnnotationControlCharacter<?> annotationControlCharacter = (AnnotationControlCharacter<?>) fragment;
		    if (annotationMap.containsKey(annotationControlCharacter.getAnnotationType())) {
			annotationMap.remove(annotationControlCharacter
				.getAnnotationType());
		    } else {
			annotationMap.put(
				annotationControlCharacter.getAnnotationType(),
				annotationControlCharacter.getAnnotation());
		    }
		}
		if (fragment instanceof MetricsControlCharacter) {
		    if (metricsControl != null && metricsControl.toString().equals(fragment.toString())) {
			// end marker
			metricsControl = null;
		    } else {
			metricsControl = (MetricsControlCharacter)fragment;
		    }
		}
		if (fragment instanceof IndentCharacter) {
		    IndentCharacter currentIndent = (IndentCharacter) fragment;
		    if (currentIndent.getLevel() == 0) {
			// indentation of 0 resets indent
			indentStack.clear();
			result.add(Indent.UNINDENT);
			continue;
		    } else {
			IndentCharacter last = null;
			while (!indentStack.isEmpty()
				&& indentStack.peek() != null
				&& currentIndent.getLevel() <= indentStack
					.peek().getLevel()) {
			    last = indentStack.pop();
			}
			if (last != null && last.equals(currentIndent)) {
			    currentIndent = last;
			}
			indentStack.push(currentIndent);
			result.add(currentIndent.createNewIndent(fontSize,
				plainFont, color));
		    }
		}
	    } else {
		PDFont font = getFont(bold, italic, plainFont, boldFont,
			italicFont, boldItalicFont);
		float baselineOffset = 0;
		float currentFontSize = fontSize;
		if (metricsControl != null) {
		     baselineOffset = metricsControl.getBaselineOffsetScale() * fontSize;
		     currentFontSize *= metricsControl.getFontScale();
		}
		if (annotationMap.isEmpty()) {
		    StyledText styledText = new StyledText(fragment.toString(),
			    currentFontSize, font, color, baselineOffset);
		    result.add(styledText);
		} else {
		    AnnotatedStyledText styledText = new AnnotatedStyledText(
			    fragment.toString(), currentFontSize, font, color, baselineOffset,
			    annotationMap.values());
		    result.add(styledText);
		}
	    }
	}
	return result;
    }

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
     *            the original text.
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
     *            the original text.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromPlainText(
	    final Iterable<CharSequence> text) {
	Iterable<CharSequence> result = splitByControlCharacter(
		ControlCharacters.NEWLINE_FACTORY, text);
	result = unescapeBackslash(result);
	return result;
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     * 
     * @param markup
     *            the markup.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(final CharSequence markup) {
	return fromMarkup(Collections.singleton(markup));
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     * 
     * @param markup
     *            the markup.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(
	    final Iterable<CharSequence> markup) {
	Iterable<CharSequence> text = markup;
	text = splitByControlCharacter(ControlCharacters.NEWLINE_FACTORY, text);
	text = splitByControlCharacter(ControlCharacters.METRICS_FACTORY, text);
	text = splitByControlCharacter(ControlCharacters.BOLD_FACTORY, text);
	text = splitByControlCharacter(ControlCharacters.ITALIC_FACTORY, text);
	text = splitByControlCharacter(ControlCharacters.COLOR_FACTORY, text);
	
	for (AnnotationControlCharacterFactory<?> annotationControlCharacterFactory : AnnotationCharacters
		.getFactories()) {
	    text = splitByControlCharacter(annotationControlCharacterFactory,
		    text);
	}
	
	text = splitByControlCharacter(IndentCharacters.INDENT_FACTORY, text);

	text = unescapeBackslash(text);

	return text;
    }

    /**
     * Splits the sequence by the given control character and replaces its
     * markup representation by the {@link ControlCharacter}.
     * 
     * @param controlCharacterFactory
     *            the control character to split by.
     * @param markup
     *            the markup to split.
     * @return the splitted and replaced sequence.
     */
    protected static Iterable<CharSequence> splitByControlCharacter(
	    ControlCharacterFactory controlCharacterFactory,
	    final Iterable<CharSequence> markup) {
	List<CharSequence> result = new ArrayList<CharSequence>();
	boolean beginOfLine = true;
	for (CharSequence current : markup) {
	    if (current instanceof String) {
		String string = (String) current;
		int begin = 0;

		if (!controlCharacterFactory.patternMatchesBeginOfLine()
			|| beginOfLine) {
		    Matcher matcher = controlCharacterFactory.getPattern()
			    .matcher(string);
		    while (matcher.find()) {
			String part = string.substring(begin, matcher.start());
			begin = matcher.end();

			if (!part.isEmpty()) {
			    String unescaped = controlCharacterFactory
				    .unescape(part);
			    result.add(unescaped);
			}

			result.add(controlCharacterFactory
				.createControlCharacter(string, matcher, result));
		    }
		}

		if (begin < string.length()) {
		    String part = string.substring(begin);
		    String unescaped = controlCharacterFactory.unescape(part);
		    result.add(unescaped);
		}

		beginOfLine = false;
	    } else {
		if (current instanceof NewLineControlCharacter) {
		    beginOfLine = true;
		}
		result.add(current);
	    }

	}
	return result;
    }

    private static Iterable<CharSequence> unescapeBackslash(
	    final Iterable<CharSequence> chars) {
	List<CharSequence> result = new ArrayList<CharSequence>();
	for (CharSequence current : chars) {
	    if (current instanceof String) {
		result.add(ControlCharacters
			.unescapeBackslash((String) current));
	    } else {
		result.add(current);
	    }
	}
	return result;
    }

}
