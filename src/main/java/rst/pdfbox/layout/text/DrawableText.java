package rst.pdfbox.layout.text;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Represents a drawable text.
 */
public interface DrawableText extends Area {

	/**
	 * Draws the text of the (PdfBox-) cursor position.
	 * @param contentStream
	 * @param cursorPosition
	 * @param alignment
	 * @throws IOException
	 */
	void drawText(PDPageContentStream contentStream, Position cursorPosition,
			Alignment alignment) throws IOException;
}
