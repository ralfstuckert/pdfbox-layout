package rst.pdfbox.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class TextFlow implements TextSequence, WidthRespecting {

	public static final float DEFAULT_LINE_SPACING = 1.3f;

	private final List<TextFragment> text = new ArrayList<TextFragment>();
	private float lineSpacing = DEFAULT_LINE_SPACING;
	private float maxWidth = -1;


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
		return PdfUtil.getWidth(this, getMaxWidth());
	}

	@Override
	public float getHeight() throws IOException {
		return PdfUtil
				.getHeight(this, getMaxWidth(), getLineSpacing());
	}

	@Override
	public void drawText(PDPageContentStream contentStream,
			Coords origin, Alignment alignment) throws IOException {
		PdfUtil.drawText(this, contentStream, origin, alignment,
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
