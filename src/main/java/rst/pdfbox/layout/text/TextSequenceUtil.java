package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Paragraph;

/**
 * Utility methods for dealing with text sequences.
 */
public class TextSequenceUtil {

    /**
     * Dissects the given sequence into {@link TextLine}s.
     * 
     * @param text
     *            the text to extract the lines from.
     * @return the list of text lines.
     * @throws IOException
     *             by pdfbox
     */
    public static List<TextLine> getLines(final TextSequence text)
	    throws IOException {
	final List<TextLine> result = new ArrayList<TextLine>();

	TextLine line = new TextLine();
	for (TextFragment fragment : text) {
	    if (fragment instanceof NewLine) {
		line.setNewLine((NewLine) fragment);
		result.add(line);
		line = new TextLine();
	    } else if (fragment instanceof ReplacedWhitespace) {
		// ignore replaced whitespace
	    } else {
		line.add((StyledText) fragment);
	    }
	}
	if (!line.isEmpty()) {
	    result.add(line);
	}
	return result;
    }

    /**
     * Word-wraps and divides the given text sequence.
     * 
     * @param text
     *            the text to divide.
     * @param maxWidth
     *            the max width used for word-wrapping.
     * @param maxHeight
     *            the max height for divide.
     * @return the Divided element containing the parts.
     * @throws IOException
     *             by pdfbox
     */
    public static Divided divide(final TextSequence text, final float maxWidth,
	    final float maxHeight) throws IOException {
	TextFlow wrapped = wordWrap(text, maxWidth);
	List<TextLine> lines = getLines(wrapped);

	Paragraph first = new Paragraph();
	Paragraph tail = new Paragraph();
	if (text instanceof TextFlow) {
	    TextFlow flow = (TextFlow) text;
	    first.setMaxWidth(flow.getMaxWidth());
	    first.setLineSpacing(flow.getLineSpacing());
	    tail.setMaxWidth(flow.getMaxWidth());
	    tail.setLineSpacing(flow.getLineSpacing());
	}

	int index = 0;
	do {
	    TextLine line = lines.get(index);
	    first.add(line);
	    ++index;
	} while (index < lines.size() && first.getHeight() < maxHeight);

	if (first.getHeight() > maxHeight) {
	    // remove last line
	    --index;
	    TextLine line = lines.get(index);
	    for (@SuppressWarnings("unused")
	    TextFragment textFragment : line) {
		first.removeLast();
	    }
	}

	for (int i = index; i < lines.size(); ++i) {
	    tail.add(lines.get(i));
	}
	return new Divided(first, tail);
    }

    /**
     * Word-wraps the given text sequence in order to fit the max width.
     * 
     * @param text
     *            the text to word-wrap.
     * @param maxWidth
     *            the max width to fit.
     * @return the word-wrapped text.
     * @throws IOException
     *             by pdfbox
     */
    public static TextFlow wordWrap(final TextSequence text,
	    final float maxWidth) throws IOException {

	float indentation = 0;
	TextFlow result = new TextFlow();
	float lineLength = indentation;
	for (TextFragment fragment : text) {
	    if (fragment instanceof NewLine) {
		result.add(fragment);
		lineLength = indentation;
		if (indentation > 0) {
		    result.add(new Indent(indentation).toStyledText());
		}
	    } else if (fragment instanceof Indent) {
		if (indentation > 0) {
		    // reset indentation
		    result.removeLast();
		    indentation = 0;
		}
		indentation = fragment.getWidth();
		lineLength = fragment.getWidth();
		result.add(((Indent) fragment).toStyledText());
	    } else {
		TextFlow words = splitWords(fragment);
		for (TextFragment word : words) {

		    if (lineLength == indentation) {
			TextFragment[] replaceLeadingBlanks = replaceLeadingBlanks(word);
			word = replaceLeadingBlanks[0];
			if (replaceLeadingBlanks.length > 1) {
			    result.add(replaceLeadingBlanks[1]);
			}
		    }

		    FontDescriptor fontDescriptor = word.getFontDescriptor();
		    float length = word.getWidth();

		    if (maxWidth > 0 && lineLength > indentation
			    && lineLength + length > maxWidth) {
			// word exceeds max width, so create new line
			result.add(new WrappingNewLine(fontDescriptor));
			if (indentation > 0) {
			    result.add(new Indent(indentation).toStyledText());
			}
			lineLength = indentation;
		    }

		    if (lineLength == indentation) {
			TextFragment[] replaceLeadingBlanks = replaceLeadingBlanks(word);
			word = replaceLeadingBlanks[0];
			length = word.getWidth();
			if (replaceLeadingBlanks.length > 1) {
			    result.add(replaceLeadingBlanks[1]);
			}
		    }

		    result.add(word);

		    if (length > 0) {
			lineLength += length;
		    }
		}
	    }
	}
	return result;
    }

    /**
     * Replaces leading whitespace by {@link ReplacedWhitespace}.
     * 
     * @param word
     *            the fragment to replace
     * @return
     */
    private static TextFragment[] replaceLeadingBlanks(final TextFragment word) {
	String text = word.getText();
	int splitIndex = 0;
	while (splitIndex < text.length()
		&& Character.isWhitespace(text.charAt(splitIndex))) {
	    ++splitIndex;
	}

	if (splitIndex == 0) {
	    return new TextFragment[] { word };
	} else {
	    ReplacedWhitespace whitespace = new ReplacedWhitespace(
		    text.substring(0, splitIndex), word.getFontDescriptor());
	    StyledText newWord = null;
	    if (word instanceof StyledText) {
		newWord = ((StyledText) word).inheritAttributes(text
			.substring(splitIndex));
	    } else {
		newWord = new StyledText(text.substring(splitIndex),
			word.getFontDescriptor(), word.getColor());
	    }
	    return new TextFragment[] { newWord, whitespace };
	}
    }

    /**
     * De-wraps the given text, means any new lines introduced by wrapping will
     * be removed. Also all whitespace removed by wrapping are re-introduced.
     * 
     * @param text
     *            the text to de-wrap.
     * @return the de-wrapped text.
     * @throws IOException
     *             by PDFBox
     */
    public static TextFlow deWrap(final TextSequence text) throws IOException {
	TextFlow result = new TextFlow();
	for (TextFragment fragment : text) {
	    if (fragment instanceof WrappingNewLine) {
		// skip
	    } else if (fragment instanceof ReplacedWhitespace) {
		result.add(((ReplacedWhitespace) fragment).toReplacedFragment());
	    } else {
		result.add(fragment);
	    }
	}

	if (text instanceof TextFlow) {
	    result.setLineSpacing(((TextFlow) text).getLineSpacing());
	}
	return result;
    }

    /**
     * Convencience function that {@link #wordWrap(TextSequence, float)
     * word-wraps} into {@link #getLines(TextSequence)}.
     * 
     * @param text
     *            the text to word-wrap.
     * @param maxWidth
     *            the max width to fit.
     * @return the word-wrapped text lines.
     * @throws IOException
     *             by pdfbox
     */
    public static List<TextLine> wordWrapToLines(final TextSequence text,
	    final float maxWidth) throws IOException {
	TextFlow wrapped = wordWrap(text, maxWidth);
	List<TextLine> lines = getLines(wrapped);
	return lines;
    }

    /**
     * Splits the fragment into words.
     * 
     * @param text
     *            the text to split.
     * @return the words as a text flow.
     */
    public static TextFlow splitWords(final TextFragment text) {
	TextFlow result = new TextFlow();
	if (text instanceof NewLine) {
	    result.add(text);
	} else {
	    float leftMargin = 0;
	    float rightMargin = 0;
	    if (text instanceof StyledText && ((StyledText) text).hasMargin()) {
		leftMargin = ((StyledText) text).getLeftMargin();
		rightMargin = ((StyledText) text).getRightMargin();
	    }

	    String[] words = text.getText().split(" ", -1);
	    for (int index = 0; index < words.length; ++index) {
		String newWord = index == 0 ? words[index] : " " + words[index];

		float currentLeftMargin = 0;
		float currentRightMargin = 0;
		if (index == 0) {
		    currentLeftMargin = leftMargin;
		}
		if (index == words.length - 1) {
		    currentRightMargin = rightMargin;
		}
		StyledText styledText = null;
		if (text instanceof StyledText) {
		    styledText = ((StyledText) text).inheritAttributes(newWord,
			    currentLeftMargin, currentRightMargin);
		} else {
		    styledText = new StyledText(newWord,
			    text.getFontDescriptor(), text.getColor(),
			    currentLeftMargin, currentRightMargin);
		}
		result.add(styledText);
	    }
	}
	return result;
    }

    /**
     * Draws the given text sequence to the PDPageContentStream at the given
     * position.
     * 
     * @param text
     *            the text to draw.
     * @param contentStream
     *            the stream to draw to
     * @param upperLeft
     *            the position of the start of the first line.
     * @param drawContext
     *            the context to
     *            {@link DrawContext#drawn(Object, Position, Area) notify} on
     *            drawn objects.
     * @param alignment
     *            how to align the text lines.
     * @param maxWidth
     *            if &gt; 0, the text may be word-wrapped to match the width.
     * @param lineSpacing
     *            the line spacing factor.
     * @param applyLineSpacingToFirstLine
     *            indicates if the line spacing should be applied to the first
     *            line also. Makes sense in most cases to do so.
     * @throws IOException
     *             by pdfbox
     */
    public static void drawText(TextSequence text,
	    PDPageContentStream contentStream, Position upperLeft,
	    DrawListener drawListener, Alignment alignment, float maxWidth,
	    final float lineSpacing, final boolean applyLineSpacingToFirstLine)
	    throws IOException {
	List<TextLine> lines = wordWrapToLines(text, maxWidth);
	float targetWidth = getMaxWidth(lines);
	Position position = upperLeft;
	float lastLineHeight = 0;
	for (int i = 0; i < lines.size(); i++) {
	    boolean applyLineSpacing = i > 0 || applyLineSpacingToFirstLine;
	    TextLine textLine = lines.get(i);
	    float currentLineHeight = textLine.getHeight();
	    float lead = lastLineHeight;
	    if (applyLineSpacing) {
		lead += (currentLineHeight * (lineSpacing - 1));
	    }
	    lastLineHeight = currentLineHeight;
	    position = position.add(0, -lead);
	    float offset = getOffset(textLine, targetWidth, alignment);
	    position = new Position(upperLeft.getX() + offset, position.getY());
	    textLine.drawText(contentStream, position, alignment, drawListener);
	}
    }

    /**
     * Gets the (left) offset of the line with respect to the target width and
     * alignment.
     * 
     * @param textLine
     *            the text
     * @param targetWidth
     *            the target width
     * @param alignment
     *            the alignment of the line.
     * @return the left offset.
     * @throws IOException
     *             by pdfbox
     */
    public static float getOffset(final TextSequence textLine,
	    final float targetWidth, final Alignment alignment)
	    throws IOException {
	switch (alignment) {
	case Right:
	    return targetWidth - textLine.getWidth();
	case Center:
	    return (targetWidth - textLine.getWidth()) / 2f;
	default:
	    return 0;
	}
    }

    /**
     * Calculates the max width of all text lines.
     * 
     * @param lines
     *            the lines for which to calculate the max width.
     * @return the max width of the lines.
     * @throws IOException
     *             by pdfbox.
     */
    public static float getMaxWidth(final Iterable<TextLine> lines)
	    throws IOException {
	float max = 0;
	for (TextLine line : lines) {
	    max = Math.max(max, line.getWidth());
	}
	return max;
    }

    /**
     * Calculates the width of the text
     * 
     * @param textSequence
     *            the text.
     * @param maxWidth
     *            if &gt; 0, the text may be word-wrapped to match the width.
     * @return the width of the text.
     * @throws IOException
     *             by pdfbox.
     */
    public static float getWidth(final TextSequence textSequence,
	    final float maxWidth) throws IOException {
	List<TextLine> lines = wordWrapToLines(textSequence, maxWidth);
	float max = 0;
	for (TextLine line : lines) {
	    max = Math.max(max, line.getWidth());
	}
	return max;
    }

    /**
     * Calculates the height of the text
     * 
     * @param textSequence
     *            the text.
     * @param maxWidth
     *            if &gt; 0, the text may be word-wrapped to match the width.
     * @param lineSpacing
     *            the line spacing factor.
     * @param applyLineSpacingToFirstLine
     *            indicates if the line spacing should be applied to the first
     *            line also. Makes sense in most cases to do so.
     * @return the height of the text.
     * @throws IOException
     *             by pdfbox
     */
    public static float getHeight(final TextSequence textSequence,
	    final float maxWidth, final float lineSpacing,
	    final boolean applyLineSpacingToFirstLine) throws IOException {
	List<TextLine> lines = wordWrapToLines(textSequence, maxWidth);
	float sum = 0;
	for (int i = 0; i < lines.size(); i++) {
	    boolean applyLineSpacing = i > 0 || applyLineSpacingToFirstLine;
	    TextLine line = lines.get(i);
	    float lineHeight = line.getHeight();
	    if (applyLineSpacing) {
		lineHeight *= lineSpacing;
	    }
	    sum += lineHeight;
	}
	return sum;
    }

}
