package rst.pdfbox.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextFlow implements TextSequence, WidthRespecting {

	public static final float DEFAULT_LINE_SPACING = 1.3f;

	private final List<TextFragment> text = new ArrayList<TextFragment>();
	private float lineSpacing = DEFAULT_LINE_SPACING;
	private float maxWidth = -1;


	public void addText(final String text, final float fontSize, final BaseFont baseFont) {
		add(TextFlowUtil.createTextFlow(text, fontSize, baseFont));
	}
	
	public void addText(final String text,
			final float fontSize, final PDFont plainFont,
			final PDFont boldFont, final PDFont italicFont,
			final PDFont boldItalicFont) {
		add(TextFlowUtil.createTextFlow(text, fontSize, plainFont, boldFont, italicFont, boldItalicFont));
	}
	
	public void addMarkup(final String markup, final float fontSize, final BaseFont baseFont) {
		add(TextFlowUtil.createTextFlowFromMarkup(markup, fontSize, baseFont));
	}
	
	public void addMarkup(final String text,
			final float fontSize, final PDFont plainFont,
			final PDFont boldFont, final PDFont italicFont,
			final PDFont boldItalicFont) {
		add(TextFlowUtil.createTextFlowFromMarkup(text, fontSize, plainFont, boldFont, italicFont, boldItalicFont));
	}
	
	public void add(final TextSequence sequence) {
		for (TextFragment fragment : sequence) {
			add(fragment);
		}
	}

	public void add(final TextFragment fragment) {
		text.add(fragment);
	}
	
	public TextFragment removeLast() {
		if (text.size() > 0) {
			return text.remove(text.size()-1);
		}
		return null;
	}

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

	public float getLineSpacing() {
		return lineSpacing;
	}

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
	public void drawText(PDPageContentStream contentStream,
			Coords origin, Alignment alignment) throws IOException {
		TextSequenceUtil.drawText(this, contentStream, origin, alignment,
				getMaxWidth(), getLineSpacing());
	}

	public void drawTextRightAligned(PDPageContentStream contentStream,
			Coords endOfFirstLine) throws IOException {
		drawText(contentStream, endOfFirstLine.add(-getWidth(), 0),
				Alignment.Right);
	}

	@Override
	public String toString() {
		return "TextFlow [text=" + text + "]";
	}

}
