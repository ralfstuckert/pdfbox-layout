package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * A text of line containing only {@link StyledText}s. It may be terminated by a
 * {@link #getNewLine() new line}.
 */
public class TextLine implements TextSequence {

    private static final String ASCENT = "ascent";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";

    private final List<StyledText> styledTextList = new ArrayList<StyledText>();
    private NewLine newLine;
    private Map<String, Object> cache = new HashMap<String, Object>();

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
     * Adds a styled text.
     * 
     * @param fragment
     *            the fagment to add.
     */
    public void add(final StyledText fragment) {
	styledTextList.add(fragment);
	clearCache();
    }

    /**
     * Adds all styled texts of the given text line.
     * 
     * @param textLine
     *            the text line to add.
     */
    public void add(final TextLine textLine) {
	for (StyledText fragment : textLine.getStyledTexts()) {
	    add(fragment);
	}
    }

    /**
     * @return the terminating new line, may be <code>null</code>.
     */
    public NewLine getNewLine() {
	return newLine;
    }

    /**
     * Sets the new line.
     * 
     * @param newLine
     *            the new line.
     */
    public void setNewLine(NewLine newLine) {
	this.newLine = newLine;
	clearCache();
    }

    /**
     * @return the styled texts building up this line.
     */
    public List<StyledText> getStyledTexts() {
	return Collections.unmodifiableList(styledTextList);
    }

    @Override
    public Iterator<TextFragment> iterator() {
	return new TextLineIterator(styledTextList.iterator(), newLine);
    }

    /**
     * @return <code>true</code> if the line contains neither styled text nor a
     *         new line.
     */
    public boolean isEmpty() {
	return styledTextList.isEmpty() && newLine == null;
    }

    @Override
    public float getWidth() throws IOException {
	Float width = getCachedValue(WIDTH, Float.class);
	if (width == null) {
	    width = 0f;
	    for (TextFragment fragment : this) {
		width += fragment.getWidth();
	    }
	    setCachedValue(WIDTH, width);
	}
	return width;
    }

    @Override
    public float getHeight() throws IOException {
	Float height = getCachedValue(HEIGHT, Float.class);
	if (height == null) {
	    height = 0f;
	    for (TextFragment fragment : this) {
		height = Math.max(height, fragment.getHeight());
	    }
	    setCachedValue(HEIGHT, height);
	}
	return height;
    }

    /**
     * @return the (max) ascent of this line.
     * @throws IOException
     *             by pdfbox.
     */
    protected float getAscent() throws IOException {
	Float ascent = getCachedValue(ASCENT, Float.class);
	if (ascent == null) {
	    ascent = 0f;
	    for (TextFragment fragment : this) {
		float currentAscent = fragment.getFontDescriptor().getSize()
			* fragment.getFontDescriptor().getFont()
				.getFontDescriptor().getAscent() / 1000;
		ascent = Math.max(ascent, currentAscent);
	    }
	    setCachedValue(ASCENT, ascent);
	}
	return ascent;
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position upperLeft,
	    Alignment alignment) throws IOException {
	contentStream.saveGraphicsState();
	contentStream.beginText();
	float x = upperLeft.getX();
	float y = upperLeft.getY() - getAscent();
	CompatibilityHelper.setTextTranslation(contentStream, x, y);
	FontDescriptor lastFontDesc = null;
	Color lastColor = null;
	float gap = 0f;
	for (StyledText styledText : styledTextList) {
	    if (!styledText.getFontDescriptor().equals(lastFontDesc)) {
		lastFontDesc = styledText.getFontDescriptor();
		contentStream.setFont(lastFontDesc.getFont(),
			lastFontDesc.getSize());
	    }
	    if (!styledText.getColor().equals(lastColor)) {
		lastColor = styledText.getColor();
		contentStream.setNonStrokingColor(lastColor);
	    }
	    if (styledText.getLeftMargin() > 0) {
		gap += styledText.getLeftMargin();
	    }
	    if (gap > 0) {
		CompatibilityHelper.moveTextPosition(contentStream, gap, 0);
		gap = 0;
	    }
	    if (styledText.getText().length() > 0) {
		CompatibilityHelper.showText(contentStream,
			styledText.getText());
	    }
	    if (styledText.getRightMargin() > 0) {
		gap = styledText.getRightMargin();
	    }
	}
	contentStream.endText();
	contentStream.restoreGraphicsState();
    }

    @Override
    public String toString() {
	return "TextLine [styledText=" + styledTextList + ", newLine="
		+ newLine + "]";
    }

    private static class TextLineIterator implements Iterator<TextFragment> {

	private Iterator<StyledText> styledText;
	private NewLine newLine;

	public TextLineIterator(Iterator<StyledText> styledText, NewLine newLine) {
	    super();
	    this.styledText = styledText;
	    this.newLine = newLine;
	}

	@Override
	public boolean hasNext() {
	    return styledText.hasNext() || newLine != null;
	}

	@Override
	public TextFragment next() {
	    TextFragment next = null;
	    if (styledText.hasNext()) {
		next = styledText.next();
	    } else if (newLine != null) {
		next = newLine;
		newLine = null;
	    }
	    return next;
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}

    }

}
