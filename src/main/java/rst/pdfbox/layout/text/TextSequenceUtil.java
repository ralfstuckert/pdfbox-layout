package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.util.Tuple;

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
	boolean isWrappedLine = false;
	for (TextFragment fragment : text) {
	    if (fragment instanceof NewLine) {
		isWrappedLine = fragment instanceof WrappingNewLine;
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
		    WordWrapContext context = new WordWrapContext(word,
			    lineLength, indentation, isWrappedLine);
		    do {
			context = wordWrap(context, maxWidth, result);
		    } while (context.isMoreToWrap());

		    indentation = context.getIndentation();
		    lineLength = context.getLineLength();
		    isWrappedLine = context.isWrappedLine();
		}
	    }
	}
	return result;
    }

    private static WordWrapContext wordWrap(final WordWrapContext context,
	    final float maxWidth, final TextFlow result) throws IOException {
	TextFragment word = context.getWord();
	TextFragment moreToWrap = null;
	float indentation = context.getIndentation();
	float lineLength = context.getLineLength();
	boolean isWrappedLine = context.isWrappedLine();

	if (lineLength == indentation) {
	    // start of line
	    TextFragment[] replaceLeadingBlanks = isWrappedLine ? replaceLeadingBlanks(word)
		    : new TextFragment[] { word };
	    word = replaceLeadingBlanks[0];
	    if (replaceLeadingBlanks.length > 1) {
		result.add(replaceLeadingBlanks[1]);
	    }
	}

	FontDescriptor fontDescriptor = word.getFontDescriptor();
	float length = word.getWidth();

	if (maxWidth > 0 // && lineLength > indentation
		&& lineLength + length > maxWidth) {
	    // word exceeds max width, so create new line

	    boolean breakHard = indentation + length > maxWidth;
	    Tuple<TextFragment> brokenWord = breakWord(word, length, maxWidth
		    - lineLength, maxWidth - indentation, breakHard);
	    if (brokenWord != null) {
		word = brokenWord.getFirst();
		length = word.getWidth();
		moreToWrap = brokenWord.getSecond();

		result.add(word);
		if (length > 0) {
		    lineLength += length;
		}

	    } else {
		moreToWrap = word;
		if (result.getLast() != null) {
		    // since the current word is not used, take 
		    // font descriptor of last line. Otherwise
		    // the line break might be to high
		    fontDescriptor = result.getLast().getFontDescriptor();
		}
	    }

	    // wrap line only if not empty
	    if (lineLength > indentation) {
		// and terminate it with a new line
		result.add(new WrappingNewLine(fontDescriptor));
		isWrappedLine = true;
		if (indentation > 0) {
		    result.add(new Indent(indentation).toStyledText());
		}
		lineLength = indentation;
	    }

	} else {
	    result.add(word);
	    if (length > 0) {
		lineLength += length;
	    }
	}

	return new WordWrapContext(moreToWrap, lineLength, indentation,
		isWrappedLine);
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
		TextFragment derived = deriveFromExisting(text, newWord,
			currentLeftMargin, currentRightMargin);
		result.add(derived);
	    }
	}
	return result;
    }

    protected static TextFragment deriveFromExisting(
	    final TextFragment toDeriveFrom, final String text,
	    final float leftMargin, final float rightMargin) {
	if (toDeriveFrom instanceof StyledText) {
	    return ((StyledText) toDeriveFrom).inheritAttributes(text,
		    leftMargin, rightMargin);
	}
	return new StyledText(text, toDeriveFrom.getFontDescriptor(),
		toDeriveFrom.getColor(), leftMargin, rightMargin);
    }

    // public static TextFlow breakWord(final TextFragment text,
    // final float remainingLineWidth, final float maxWidth)
    // throws IOException {
    // TextFlow result = new TextFlow();
    // TextFragment current = text;
    // float width = current.getWidth();
    // float max = remainingLineWidth;
    // float remaining = remainingLineWidth;
    // while (width > max) {
    // current = breakWord(current, width, remaining, max, result);
    // width = current.getWidth();
    // max = maxWidth;
    // remaining = 0;
    // }
    // result.add(current);
    //
    // return result;
    // }

    private static Tuple<TextFragment> breakWord(TextFragment word,
	    float wordWidth, final float remainingLineWidth, float maxWidth,
	    boolean breakHard) throws IOException {

	float leftMargin = 0;
	float rightMargin = 0;
	if (word instanceof StyledText) {
	    StyledText styledText = (StyledText) word;
	    leftMargin = styledText.getLeftMargin();
	    rightMargin = styledText.getRightMargin();
	}

	int breakIndex = calculateSoftBreakIndex(word.getText(),
		word.getFontDescriptor(), remainingLineWidth - leftMargin);
	if (breakIndex == -1) {
	    // soft break is not possible
	    if (!breakHard) {
		// nothing we can do about it, so return null
		return null;
	    }
	    breakIndex = calculateBreakIndex(word.getText(),
		    word.getFontDescriptor(), maxWidth - leftMargin);
	}
	// break at calculated index
	TextFragment head = deriveFromExisting(word,
		word.getText().substring(0, breakIndex), leftMargin, 0);
	TextFragment tail = deriveFromExisting(word,
		word.getText().substring(breakIndex), 0, rightMargin);

	return new Tuple<TextFragment>(head, tail);
    }

    public static int calculateBreakIndex(final String word,
	    final FontDescriptor fontDescriptor, final float maxWidth)
	    throws IOException {
	int cutIndex = (int) (maxWidth / getEmWidth(fontDescriptor));
	float currentWidth = 0;
	do {
	    currentWidth = getStringWidth(word.substring(0, cutIndex),
		    fontDescriptor);
	    --cutIndex;
	} while (currentWidth > maxWidth);

	return ++cutIndex;
    }

    public static int calculateSoftBreakIndex(final String word,
	    final FontDescriptor fontDescriptor, final float maxWidth)
	    throws IOException {

	String alpha = "[A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]";
	Pattern pattern = Pattern.compile(alpha + "([\\-\\.\\,/])");
	Matcher matcher = pattern.matcher(word);
	int breakIndex = -1;
	boolean maxWidthExceeded = false;
	while (!maxWidthExceeded && matcher.find()) {
	    int currentIndex = matcher.end();
	    if (currentIndex < word.length() - 1) {
		if (getStringWidth(word.substring(0, currentIndex),
			fontDescriptor) < maxWidth) {
		    breakIndex = currentIndex;
		} else {
		    maxWidthExceeded = true;
		}
	    }
	}

	return breakIndex;
    }

    public static void main(String[] args) {
	Matcher matcher = softWordBreakMatcher("dsüüöä/dsf.79879");
	System.out.println(matcher.find());
	System.out.println(matcher.start());
	System.out.println(matcher.end());
	System.out.println(matcher.find());
	System.out.println(matcher.start());
	System.out.println(softWordBreakMatcher("dsf_dsf").find());
	System.out.println(softWordBreakMatcher("dsf_dsf").start());
    }

    private static Matcher softWordBreakMatcher(String text) {
	String alpha = "[A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]";
	Pattern pattern = Pattern.compile(alpha + "[\\-\\.\\,/]");
	return pattern.matcher(text);
    }

    private static float getEmWidth(final FontDescriptor fontDescriptor)
	    throws IOException {
	return getStringWidth("M", fontDescriptor);
    }

    private static float getStringWidth(final String text,
	    final FontDescriptor fontDescriptor) throws IOException {
	return fontDescriptor.getSize()
		* fontDescriptor.getFont().getStringWidth(text) / 1000;
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
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
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

    private static class WordWrapContext {
	private TextFragment word;
	private float lineLength;
	private float indentation;
	boolean isWrappedLine;

	public WordWrapContext(TextFragment word, float lineLength,
		float indentation, boolean isWrappedLine) {
	    this.word = word;
	    this.lineLength = lineLength;
	    this.indentation = indentation;
	    this.isWrappedLine = isWrappedLine;
	}

	public TextFragment getWord() {
	    return word;
	}

	public float getLineLength() {
	    return lineLength;
	}

	public float getIndentation() {
	    return indentation;
	}

	public boolean isWrappedLine() {
	    return isWrappedLine;
	}

	public boolean isMoreToWrap() {
	    return getWord() != null;
	}

    }
}
