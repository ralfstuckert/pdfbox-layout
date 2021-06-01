package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * A text flow is a text sequence that {@link WidthRespecting respects a given
 * width} by word wrapping the text. The text may contain line breaks ('\n').<br>
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
 * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</em></b><em> markup</em>.
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
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";

    private Map<String, Object> cache = new HashMap<String, Object>();

    private final List<TextFragment> text = new ArrayList<TextFragment>();
    private float lineSpacing = DEFAULT_LINE_SPACING;
    private float maxWidth = -1;
    private boolean applyLineSpacingToFirstLine = true;

    private void clearCache() {
	cache.clear();
    }

    private void setCachedValue(final String key, Object value) {
	cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedValue(final String key, Class<T> type) {
	return (T) cache.get(key);
    }

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
     * @throws IOException by PDFBox
     */
    public void addText(final String text, final float fontSize,
	    final PDFont font) throws IOException {
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
     * @throws IOException by PDFBox
     */
    public void addMarkup(final String markup, final float fontSize,
	    final BaseFont baseFont) throws IOException {
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
     * @throws IOException by PDFBox
     */
    public void addMarkup(final String markup, final float fontSize,
	    final PDFont plainFont, final PDFont boldFont,
	    final PDFont italicFont, final PDFont boldItalicFont) throws IOException {
	add(TextFlowUtil.createTextFlowFromMarkup(markup, fontSize, plainFont,
		boldFont, italicFont, boldItalicFont));
    }

    /**
     * Adds a text sequence to this flow.
     * 
     * @param sequence
     *            the sequence to add.
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
     *            the fragment to add.
     */
    public void add(final TextFragment fragment) {
	text.add(fragment);
	clearCache();
    }

    /**
     * Removes the last added fragment.
     * 
     * @return the removed fragment (if any).
     */
    public TextFragment removeLast() {
	if (text.size() > 0) {
	    clearCache();
	    return text.remove(text.size() - 1);
	}
	return null;
    }

    /**
     * @return the last added fragment (if any).
     */
    public TextFragment getLast() {
	if (text.size() > 0) {
	    clearCache();
	    return text.get(text.size() - 1);
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
	clearCache();
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
     *            the line spacing factor.
     */
    public void setLineSpacing(float lineSpacing) {
	this.lineSpacing = lineSpacing;
	clearCache();
    }

    /**
     * Indicates if the line spacing should be applied to the first line. Makes
     * sense if there is text above to achieve an equal spacing. In case you
     * want to position the text precisely on top, you may set this value to
     * <code>false</code>. Default is <code>true</code>.
     * 
     * @return <code>true</code> if the line spacing should be applied to the
     *         first line.
     */
    public boolean isApplyLineSpacingToFirstLine() {
	return applyLineSpacingToFirstLine;
    }

    /**
     * Sets the indicator whether to apply line spacing to the first line.
     * 
     * @param applyLineSpacingToFirstLine
     *            <code>true</code> if the line spacing should be applied to the
     *            first line.
     * @see TextFlow#isApplyLineSpacingToFirstLine()
     */
    public void setApplyLineSpacingToFirstLine(
	    boolean applyLineSpacingToFirstLine) {
	this.applyLineSpacingToFirstLine = applyLineSpacingToFirstLine;
    }

    @Override
    public float getWidth() throws IOException {
	Float width = getCachedValue(WIDTH, Float.class);
	if (width == null) {
	    width = TextSequenceUtil.getWidth(this, getMaxWidth());
	    setCachedValue(WIDTH, width);
	}
	return width;
    }

    @Override
    public float getHeight() throws IOException {
	Float height = getCachedValue(HEIGHT, Float.class);
	if (height == null) {
	    height = TextSequenceUtil.getHeight(this, getMaxWidth(),
		    getLineSpacing(), isApplyLineSpacingToFirstLine());
	    setCachedValue(HEIGHT, height);
	}
	return height;
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position upperLeft,
	    Alignment alignment, DrawListener drawListener) throws IOException {
	TextSequenceUtil.drawText(this, contentStream, upperLeft, drawListener, alignment,
		getMaxWidth(), getLineSpacing(),
		isApplyLineSpacingToFirstLine());
    }

    public void drawTextRightAligned(PDPageContentStream contentStream,
	    Position endOfFirstLine, DrawListener drawListener) throws IOException {
	drawText(contentStream, endOfFirstLine.add(-getWidth(), 0),
		Alignment.Right, drawListener);
    }

    /**
     * @return a copy of this text flow where all leading {@link NewLine}s are removed.
     * @throws IOException by pdfbox.
     */
    public TextFlow removeLeadingEmptyLines() throws IOException {
	if (text.size() == 0 || !(text.get(0) instanceof NewLine)) {
	    return this;
	}
	TextFlow result = createInstance();
	result.setApplyLineSpacingToFirstLine(this.isApplyLineSpacingToFirstLine());
	result.setLineSpacing(this.getLineSpacing());
	result.setMaxWidth(this.getMaxWidth());
	for (TextFragment fragment : this) {
	    if (!result.isEmpty() || !(fragment instanceof NewLine)) {
		result.add(fragment);
	    }
	}
	return result;
    }
    
    protected TextFlow createInstance() {
	return new TextFlow();
    }
    
    @Override
    public String toString() {
	return "TextFlow [text=" + text + "]";
    }

}
