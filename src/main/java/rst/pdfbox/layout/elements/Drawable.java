package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Coords;

public interface Drawable {

	float getWidth() throws IOException;

	float getHeight() throws IOException;

	Coords getAbsolutePosition();

	void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException;

}
