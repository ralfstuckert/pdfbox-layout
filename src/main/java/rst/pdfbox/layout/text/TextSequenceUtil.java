package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

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
     * @return the list of text lines.
     * @throws IOException
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
     */
    public static Divided divide(final TextSequence text, final float maxWidth,
	    final float maxHeight) throws IOException {
	TextFlow wrapped = wordWrap(text, maxWidth);
	List<TextLine> lines = getLines(wrapped);

	Paragraph first = new Paragraph();
	Paragraph last = new Paragraph();
	if (text instanceof TextFlow) {
	    TextFlow flow = (TextFlow) text;
	    first.setMaxWidth(flow.getMaxWidth());
	    first.setLineSpacing(flow.getLineSpacing());
	    last.setMaxWidth(flow.getMaxWidth());
	    last.setLineSpacing(flow.getLineSpacing());
	}

	int index = 0;
	do {
	    TextLine line = lines.get(index);
	    first.add(line);
	    ++index;
	} while (index < lines.size() && first.getHeight() < maxHeight);

	if (first.getHeight() < maxHeight) {
	    first.removeLast();
	    --index;
	}

	for (int i = index; i < lines.size(); ++i) {
	    last.add(lines.get(i));
	}
	return new Divided(first, last);
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
     */
    public static TextFlow wordWrap(final TextSequence text,
	    final float maxWidth) throws IOException {

	TextFlow result = new TextFlow();
	float lineLength = 0;
	for (TextFragment fragment : text) {
	    if (fragment instanceof NewLine) {
		result.add(fragment);
		lineLength = 0;
	    } else {
		TextFlow words = splitWords(fragment);
		for (TextFragment word : words) {

		    FontDescriptor fontDescriptor = word.getFontDescriptor();
		    float length = fontDescriptor.getSize()
			    * fontDescriptor.getFont().getStringWidth(
				    word.getText()) / 1000;

		    float extraSpace = 0;
		    if (lineLength > 0 && word.getText().length() > 0) {
			// not the first word in line, so separate by blank
			extraSpace = fontDescriptor.getSize()
				* fontDescriptor.getFont().getSpaceWidth()
				/ 1000;
		    }
		    if (maxWidth > 0 && lineLength > 0
			    && lineLength + length + extraSpace > maxWidth) {
			// word exceeds max width, so create new line
			result.add(new WrappingNewLine(fontDescriptor));
			lineLength = 0;
		    }
		    if (lineLength > 0 && word.getText().length() > 0) {
			// not the first word in line, so separate by blank
			word = new StyledText(" " + word.getText(),
				word.getFontDescriptor());
		    }
		    result.add(word);
		    lineLength += length + extraSpace;
		}
	    }
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
     * @return the words as a text flow.
     */
    public static TextFlow splitWords(final TextFragment text) {
	TextFlow result = new TextFlow();
	if (text instanceof NewLine) {
	    result.add(text);
	} else {
	    String[] words = text.getText().split(" ");
	    for (String word : words) {
		result.add(new StyledText(word, text.getFontDescriptor()));
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
     * @param cursorPosition
     *            the position of the start of the first line.
     * @param alignment
     *            how to align the text lines.
     * @param maxWidth
     *            if > 0, the text may be word-wrapped to match the width.
     * @param lineSpacing
     *            the line spacing factor.
     * @throws IOException
     */
    public static void drawText(TextSequence text,
	    PDPageContentStream contentStream, Position cursorPosition,
	    Alignment alignment, float maxWidth, final float lineSpacing)
	    throws IOException {
	List<TextLine> lines = wordWrapToLines(text, maxWidth);
	float targetWidth = getMaxWidth(lines);
	Position position = cursorPosition;
	float lastLineHeight = 0;
	for (int i = 0; i < lines.size(); i++) {
	    TextLine textLine = lines.get(i);
	    float currentLineHeight = textLine.getHeight();
	    float lead = lastLineHeight
		    + (currentLineHeight * (lineSpacing - 1));
	    lastLineHeight = currentLineHeight;
	    position = position.add(0, -lead);
	    float offset = getOffset(textLine, targetWidth, alignment);
	    position = new Position(cursorPosition.getX() + offset,
		    position.getY());
	    textLine.drawText(contentStream, position, alignment);
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
     * @return the max width of the lines.
     * @throws IOException
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
     *            if > 0, the text may be word-wrapped to match the width.
     * @return the width of the text.
     * @throws IOException
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
     *            if > 0, the text may be word-wrapped to match the width.
     * @return the height of the text.
     * @throws IOException
     */
    public static float getHeight(final TextSequence textSequence,
	    final float maxWidth, final float lineSpacing) throws IOException {
	List<TextLine> lines = wordWrapToLines(textSequence, maxWidth);
	float sum = 0;
	for (int i = 0; i < lines.size(); i++) {
	    TextLine line = lines.get(i);
	    float lineHeight = line.getHeight() * lineSpacing;
	    sum += lineHeight;
	}
	return sum;
    }

}
