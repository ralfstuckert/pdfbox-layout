package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import rst.pdfbox.layout.elements.Element;

/**
 * A layout is used to size and position the elements of a document according to
 * a specific strategy.
 */
public interface Layout extends Element {

    /**
     * Renders an element.
     * 
     * @param renderContext
     *            the render context.
     * @param element
     *            the element to draw.
     * @param layoutHint
     *            the associated layout hint
     * @return <code>true</code> if the layout is able to render the element.
     * @throws IOException
     *             by pdfbox
     */
    boolean render(final RenderContext renderContext, final Element element,
	    final LayoutHint layoutHint) throws IOException;

}
