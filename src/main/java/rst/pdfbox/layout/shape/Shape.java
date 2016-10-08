package rst.pdfbox.layout.shape;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.DrawListener;
import rst.pdfbox.layout.text.Position;

public interface Shape {

    /**
     * Draws (strokes) the shape.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the shape.
     * @param height
     *            the height of the shape.
     * @param color
     *            the color to use.
     * @param stroke
     *            the stroke to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws IOException
     *             by pdfbox
     */
    void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height, Color color,
	    Stroke stroke, DrawListener drawListener) throws IOException;

    /**
     * Fills the shape.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the shape.
     * @param height
     *            the height of the shape.
     * @param color
     *            the color to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws IOException
     *             by pdfbox
     */
    void fill(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height, Color color,
	    DrawListener drawListener) throws IOException;

    /**
     * Adds (the path of) the shape without drawing anything.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the shape.
     * @param height
     *            the height of the shape.
     * @throws IOException
     *             by pdfbox
     */
    void add(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height) throws IOException;

}
