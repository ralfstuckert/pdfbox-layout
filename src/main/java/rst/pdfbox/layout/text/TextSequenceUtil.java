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

	TextFlow result = new TextFlow();
	float lineLength = 0;
	for (TextFragment fragment : text) {
	    if (fragment instanceof NewLine) {
		result.add(fragment);
		lineLength = 0;
	    } else {
		TextFlow words = splitWords(fragment);
		for (TextFragment word : words) {

		    if (lineLength == 0) {
			TextFragment[] replaceLeadingBlanks = replaceLeadingBlanks(word);
			word = replaceLeadingBlanks[0];
			if (replaceLeadingBlanks.length > 1) {
			    result.add(replaceLeadingBlanks[1]);
			}
		    }

		    FontDescriptor fontDescriptor = word.getFontDescriptor();
		    float length = word.getWidth();

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

		    if (lineLength == 0) {
			TextFragment[] replaceLeadingBlanks = replaceLeadingBlanks(word);
			word = replaceLeadingBlanks[0];
			if (replaceLeadingBlanks.length > 1) {
			    result.add(replaceLeadingBlanks[1]);
			}
		    }

		    result.add(word);

		    if (length > 0) {
			lineLength += length + extraSpace;
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
	    StyledText newWord = new StyledText(text.substring(splitIndex),
		    word.getFontDescriptor());
	    newWord.setColor(word.getColor());
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
	    String[] words = text.getText().split(" ", -1);
	    boolean firstWord = true;
	    for (String word : words) {
		String newWord = word;
		if (!firstWord) {
		    newWord = " " + newWord;
		}
		StyledText styledText = new StyledText(newWord, text.getFontDescriptor());
		styledText.setColor(text.getColor());
		result.add(styledText);
		firstWord = false;
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
     * @param alignment
     *            how to align the text lines.
     * @param maxWidth
     *            if &gt; 0, the text may be word-wrapped to match the width.
     * @param lineSpacing
     *            the line spacing factor.
     * @throws IOException
     *             by pdfbox
     */
    public static void drawText(TextSequence text,
	    PDPageContentStream contentStream, Position upperLeft,
	    Alignment alignment, float maxWidth, final float lineSpacing)
	    throws IOException {
	List<TextLine> lines = wordWrapToLines(text, maxWidth);
	float targetWidth = getMaxWidth(lines);
	Position position = upperLeft;
	float lastLineHeight = 0;
	for (int i = 0; i < lines.size(); i++) {
	    TextLine textLine = lines.get(i);
	    float currentLineHeight = textLine.getHeight();
	    float lead = lastLineHeight
		    + (currentLineHeight * (lineSpacing - 1));
	    lastLineHeight = currentLineHeight;
	    position = position.add(0, -lead);
	    float offset = getOffset(textLine, targetWidth, alignment);
	    position = new Position(upperLeft.getX() + offset, position.getY());
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
     * @return the height of the text.
     * @throws IOException
     *             by pdfbox
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
