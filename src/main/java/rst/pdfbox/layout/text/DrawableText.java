package rst.pdfbox.layout.text;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

/**
 * Represents a drawable text.
 */
public interface DrawableText extends Area {

    /**
     * Draws the text of the (PdfBox-) cursor position.
     * 
     * @param contentStream the content stream used to render.
     * @param upperLeft the upper left position to draw to.
     * @param alignment the text alignment.
     * @throws IOException by pdfbox.
     */
    void drawText(PDPageContentStream contentStream, Position upperLeft,
	    Alignment alignment) throws IOException;
}
