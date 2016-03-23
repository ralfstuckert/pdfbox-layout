package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.Alignment;
import rst.pdfbox.layout.Coords;
import rst.pdfbox.layout.PdfUtil;
import rst.pdfbox.layout.TextFlow;
import rst.pdfbox.layout.WidthRespecting;

public class Paragraph extends TextFlow implements DrawableElement,
		WidthRespecting, Dividable {

	private Coords absolutePosition;
	private Alignment alignment = Alignment.Left;

	@Override
	public Coords getAbsolutePosition() {
		return absolutePosition;
	}

	public void setAbsolutePosition(Coords absolutePosition) {
		this.absolutePosition = absolutePosition;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	@Override
	public void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException {
		drawText(contentStream, origin, getAlignment());
	}

	@Override
	public Divided divide(float maxHeight) throws IOException {
		return PdfUtil.divide(this, getMaxWidth(), maxHeight);
	}

}
