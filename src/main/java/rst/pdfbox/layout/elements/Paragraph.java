package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.Alignment;
import rst.pdfbox.layout.Coords;
import rst.pdfbox.layout.TextFlow;

public class Paragraph extends TextFlow implements Element {

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

}
