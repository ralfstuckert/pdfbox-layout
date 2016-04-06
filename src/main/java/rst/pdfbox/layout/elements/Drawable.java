package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.elements.render.Layout;
import rst.pdfbox.layout.text.Position;

/**
 * Common interface for drawable objects.
 */
public interface Drawable {

    /**
     * @return the width of the drawable.
     * @throws IOException by pdfbox
     */
    float getWidth() throws IOException;

    /**
     * @return the height of the drawable.
     * @throws IOException by pdfbox
     */
    float getHeight() throws IOException;

    /**
     * If an absolute position is given, the drawable will be drawn at this
     * position ignoring any {@link Layout}.
     * 
     * @return the absolute position.
     * @throws IOException by pdfbox
     */
    Position getAbsolutePosition() throws IOException;

    /**
     * Draws the object at the given position.
     * 
     * @param contentStream the stream to draw to.
     * @param upperLeft the upper left position to start drawing. 
     * @throws IOException by pdfbox
     */
    void draw(PDPageContentStream contentStream, Position upperLeft)
	    throws IOException;

}
