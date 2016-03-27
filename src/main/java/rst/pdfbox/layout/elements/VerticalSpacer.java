package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Coords;

public class VerticalSpacer implements DrawableElement, Dividable {

	private float height;

	public VerticalSpacer(float height) {
		this.height = height;
	}

	@Override
	public float getWidth() throws IOException {
		return 0;
	}

	@Override
	public float getHeight() throws IOException {
		return height;
	}

	@Override
	public Coords getAbsolutePosition() {
		return null;
	}

	@Override
	public void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException {
		// nothing to draw
	}

	@Override
	public Divided divide(float maxHeight) throws IOException {
		return new Divided(new VerticalSpacer(maxHeight), new VerticalSpacer(
				getHeight() - maxHeight));
	}
}
