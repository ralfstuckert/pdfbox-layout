package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.Coords;

public interface Element {

	float getMaxWidth();

	void setMaxWidth(float maxWidth);

	float getWidth() throws IOException;

	float getHeight() throws IOException;

	Coords getAbsolutePosition();

	void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException;
}
