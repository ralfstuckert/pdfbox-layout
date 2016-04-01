package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import rst.pdfbox.layout.elements.Drawable;
import rst.pdfbox.layout.elements.Element;

/**
 * A layout is used to size and position the elements of a document according to
 * a specific strategy.
 */
public interface Layout extends Element {

    void render(final RenderContext renderContext, final Drawable drawable,
	    final LayoutHint layoutHint) throws IOException;

}
