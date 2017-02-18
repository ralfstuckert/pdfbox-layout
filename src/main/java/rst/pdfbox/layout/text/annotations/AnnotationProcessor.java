package rst.pdfbox.layout.text.annotations;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import rst.pdfbox.layout.text.DrawContext;
import rst.pdfbox.layout.text.Position;

/**
 * Processes an annotation.
 */
public interface AnnotationProcessor {

    /**
     * Called if an annotated object has been drawn.
     * 
     * @param drawnObject
     *            the drawn object.
     * @param annotation
     *            the annotation.
     * @param drawContext
     *            the drawing context.
     * @param upperLeft
     *            the upper left position the object has been drawn to.
     * @param width
     *            the width of the drawn object.
     * @param height
     *            the height of the drawn object.
     * @throws IOException
     *             by pdfbox.
     */
    void annotatedObjectDrawn(final Annotated drawnObject,
	    final DrawContext drawContext, Position upperLeft, float width,
	    float height) throws IOException;

    /**
     * Called before a page is drawn.
     * 
     * @param drawContext
     *            the drawing context.
     * @throws IOException
     *             by pdfbox.
     */
    void beforePage(final DrawContext drawContext) throws IOException;

    /**
     * Called after a page is drawn.
     * 
     * @param drawContext
     *            the drawing context.
     * @throws IOException
     *             by pdfbox.
     */
    void afterPage(final DrawContext drawContext) throws IOException;

    /**
     * Called after all rendering has been performed.
     * 
     * @param document
     *            the document.
     * @throws IOException
     *             by pdfbox.
     */
    void afterRender(final PDDocument document) throws IOException;

}
