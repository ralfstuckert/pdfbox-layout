package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * A text flow is a text sequence that {@link WidthRespecting respects a given
 * width} by word wrapping the text. The text may contain line breaks ('\n').<br/>
 * In order to ease creation of styled text, this class supports a kind of
 * {@link #addMarkup(String, float, BaseFont) markup}. The following raw text
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
 */
public class TextFlow implements TextSequence, WidthRespecting {

    public static final float DEFAULT_LINE_SPACING = 1.2f;

    private final List<TextFragment> text = new ArrayList<TextFragment>();
    private float lineSpacing = DEFAULT_LINE_SPACING;
    private float maxWidth = -1;

    /**
     * Adds some text associated with the font to draw. The text may contain
     * line breaks ('\n').
     * 
     * @param text
     *            the text to add.
     * @param fontSize
     *            the size of the font.
     * @param font
     *            the font to use to draw the text.
     */
    public void addText(final String text, final float fontSize,
	    final PDFont font) {
	add(TextFlowUtil.createTextFlow(text, fontSize, font));
    }

    /**
     * Adds some markup to the text flow.
     * 
     * @param markup
     *            the markup to add.
     * @param fontSize
     *            the font size to use.
     * @param baseFont
     *            the base font describing the bundle of
     *            plain/blold/italic/bold-italic fonts.
     */
    public void addMarkup(final String markup, final float fontSize,
	    final BaseFont baseFont) {
	add(TextFlowUtil.createTextFlowFromMarkup(markup, fontSize, baseFont));
    }

    /**
     * Adds some markup to the text flow.
     * 
     * @param markup
     *            the markup to add.
     * @param fontSize
     *            the font size to use.
     * @param plainFont
     *            the plain font to use.
     * @param boldFont
     *            the bold font to use.
     * @param italicFont
     *            the italic font to use.
     * @param boldItalicFont
     *            the bold-italic font to use.
     */
    public void addMarkup(final String markup, final float fontSize,
	    final PDFont plainFont, final PDFont boldFont,
	    final PDFont italicFont, final PDFont boldItalicFont) {
	add(TextFlowUtil.createTextFlowFromMarkup(markup, fontSize, plainFont,
		boldFont, italicFont, boldItalicFont));
    }

    /**
     * Adds a text sequence to this flow.
     * 
     * @param sequence
     */
    public void add(final TextSequence sequence) {
	for (TextFragment fragment : sequence) {
	    add(fragment);
	}
    }

    /**
     * Adds a text fragment to this flow.
     * 
     * @param fragment
     */
    public void add(final TextFragment fragment) {
	text.add(fragment);
    }

    /**
     * Removes the last added fragment.
     * 
     * @return the removed fragment (if any).
     */
    public TextFragment removeLast() {
	if (text.size() > 0) {
	    return text.remove(text.size() - 1);
	}
	return null;
    }

    /**
     * @return <code>true</code> if this flow does not contain any fragments.
     */
    public boolean isEmpty() {
	return text.isEmpty();
    }

    @Override
    public Iterator<TextFragment> iterator() {
	return text.iterator();
    }

    @Override
    public float getMaxWidth() {
	return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
	this.maxWidth = maxWidth;
    }

    /**
     * @return the factor multiplied with the height to calculate the line
     *         spacing.
     */
    public float getLineSpacing() {
	return lineSpacing;
    }

    /**
     * Sets the factor multiplied with the height to calculate the line spacing.
     * 
     * @param lineSpacing
     */
    public void setLineSpacing(float lineSpacing) {
	this.lineSpacing = lineSpacing;
    }

    @Override
    public float getWidth() throws IOException {
	return TextSequenceUtil.getWidth(this, getMaxWidth());
    }

    @Override
    public float getHeight() throws IOException {
	return TextSequenceUtil
		.getHeight(this, getMaxWidth(), getLineSpacing());
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position origin,
	    Alignment alignment) throws IOException {
	TextSequenceUtil.drawText(this, contentStream, origin, alignment,
		getMaxWidth(), getLineSpacing());
    }

    public void drawTextRightAligned(PDPageContentStream contentStream,
	    Position endOfFirstLine) throws IOException {
	drawText(contentStream, endOfFirstLine.add(-getWidth(), 0),
		Alignment.Right);
    }

    @Override
    public String toString() {
	return "TextFlow [text=" + text + "]";
    }

}
