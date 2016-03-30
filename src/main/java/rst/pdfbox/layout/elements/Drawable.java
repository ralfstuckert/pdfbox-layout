package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.render.Layout;
import rst.pdfbox.layout.text.Coords;

/**
 * Common interface for drawable objects.
 */
public interface Drawable {

	/**
	 * @return the width of the drawable.
	 * @throws IOException
	 */
	float getWidth() throws IOException;

	/**
	 * @return the height of the drawable.
	 * @throws IOException
	 */
	float getHeight() throws IOException;

	/**
	 * If an absolute position is given, the drawable will be drawn at this
	 * position ignoring any {@link Layout}.
	 * 
	 * @return the absolute position.
	 * @throws IOException
	 */
	Coords getAbsolutePosition();

	/**
	 * Draws the object at the given position.
	 * @param contentStream
	 * @param origin
	 * @throws IOException
	 */
	void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException;

}
