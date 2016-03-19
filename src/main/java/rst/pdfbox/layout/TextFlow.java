package rst.pdfbox.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class TextFlow implements TextSequence {

	public static final float DEFAULT_LINE_SPACING = 1.3f;
	
	private final List<TextFragment> text = new ArrayList<TextFragment>();
	private float lineSpacing = DEFAULT_LINE_SPACING;
	private float preferredMaxWidth = -1;

	TextFlow() {
		super();
	}

	public void add(final TextSequence sequence) {
		for (TextFragment fragment : sequence) {
			add(fragment);
		}
	}

	public void add(final TextFragment fragment) {
		text.add(fragment);
	}

	@Override
	public Iterator<TextFragment> iterator() {
		return text.iterator();
	}

	public float getPreferredMaxWidth() {
		return preferredMaxWidth;
	}

	public void setPreferredMaxWidth(float maxWidth) {
		this.preferredMaxWidth = maxWidth;
	}
	
	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	@Override
	public float getWidth() throws IOException {
		List<? extends TextSequence> lines = PdfUtil.wordWrapToLines(this, getPreferredMaxWidth());
		float max = 0;
		for (TextSequence line : lines) {
			max = Math.max(max, line.getWidth());
		}
		return max;
	}

	@Override
	public float getHeight() throws IOException {
		List<? extends TextSequence> lines = PdfUtil.wordWrapToLines(this, preferredMaxWidth);
		float sum = 0;
		for (int i = 0; i < lines.size(); i++) {
			TextSequence line = lines.get(i);
			float lineHeight = line.getHeight();
			if (i < lines.size() - 1) {
				lineHeight *= getLineSpacing();
			}
			sum += lineHeight;
		}
		return sum;
	}

	@Override
	public Coords drawText(PDPageContentStream contentStream,
			Coords beginOfFirstLine, Alignment alignment) throws IOException {
		return PdfUtil.drawText(this, contentStream, beginOfFirstLine, alignment,
				preferredMaxWidth);
	}

	public Coords drawTextRightAligned(PDPageContentStream contentStream,
			Coords endOfFirstLine) throws IOException {
		return PdfUtil.drawTextRightAligned(this, contentStream,
				endOfFirstLine, preferredMaxWidth);
	}

	@Override
	public String toString() {
		return "TextFlow [text=" + text + "]";
	}

}
