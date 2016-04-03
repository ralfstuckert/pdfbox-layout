package rst.pdfbox.layout.elements.render;

import java.awt.geom.PathIterator;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.Cutter;
import rst.pdfbox.layout.elements.Dividable;
import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Drawable;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.WidthRespecting;

/**
 * Layout implementation that stacks drawables vertically onto the page. If the
 * remaining height on the page is not sufficient for the drawable, it will be
 * {@link Dividable divided}. Any given {@link VerticalLayoutHint} will be taken
 * into account to calculate the position, width, alignment etc.
 */
public class VerticalLayout implements Layout {

    @Override
    public void render(final RenderContext renderContext, Drawable drawable,
	    final LayoutHint layoutHint) throws IOException {
	if (drawable.getAbsolutePosition() != null) {
	    renderAbsolute(renderContext, drawable, layoutHint,
		    drawable.getAbsolutePosition());
	} else {
	    renderReleative(renderContext, drawable, layoutHint);
	}
    }

    /**
     * Draws at the given position, ignoring all layouting rules.
     * 
     * @param renderContext
     * @param drawable
     * @param layoutHint
     * @param position
     * @throws IOException
     */
    protected void renderAbsolute(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint,
	    final Position position) throws IOException {
	PDPageContentStream contentStream = renderContext.getContentStream();
	drawable.draw(contentStream, position);
    }

    /**
     * Renders the drawable at the {@link RenderContext#getCurrentPosition()
     * current position}. This method is responsible taking any top or bottom
     * margin described by the (Vertical-)LayoutHint into account. The actual
     * rendering of the drawable is performed by
     * {@link #layoutAndDrawReleative(RenderContext, Drawable, LayoutHint)}.
     * 
     * @param renderContext
     * @param drawable
     * @param layoutHint
     * @throws IOException
     */
    protected void renderReleative(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint) throws IOException {
	VerticalLayoutHint verticalLayoutHint = null;
	if (layoutHint instanceof VerticalLayoutHint) {
	    verticalLayoutHint = (VerticalLayoutHint) layoutHint;
	    if (verticalLayoutHint.getMarginTop() > 0) {
		layoutAndDrawReleative(renderContext, new VerticalSpacer(
			verticalLayoutHint.getMarginTop()), verticalLayoutHint);
	    }
	}

	layoutAndDrawReleative(renderContext, drawable, verticalLayoutHint);

	if (verticalLayoutHint != null) {
	    if (verticalLayoutHint.getMarginBottom() > 0) {
		layoutAndDrawReleative(renderContext, new VerticalSpacer(
			verticalLayoutHint.getMarginBottom()),
			verticalLayoutHint);
	    }
	}
    }

    /**
     * Adjusts the width of the drawable (if it is {@link WidthRespecting}), and
     * divides it onto multiple pages if necessary. Actual drawing is delegated
     * to
     * {@link #drawReletivePartAndMovePosition(RenderContext, Drawable, LayoutHint)}
     * .
     * 
     * @param renderContext
     * @param drawable
     * @param layoutHint
     * @throws IOException
     */
    protected void layoutAndDrawReleative(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint) throws IOException {

	float targetWidth = renderContext.getWidth();
	VerticalLayoutHint verticalLayoutHint = null;
	if (layoutHint instanceof VerticalLayoutHint) {
	    verticalLayoutHint = (VerticalLayoutHint) layoutHint;
	    targetWidth -= verticalLayoutHint.getMarginLeft();
	    targetWidth -= verticalLayoutHint.getMarginRight();
	}

	float oldMaxWidth = -1;
	if (drawable instanceof WidthRespecting) {
	    WidthRespecting flowing = (WidthRespecting) drawable;
	    oldMaxWidth = flowing.getMaxWidth();
	    if (oldMaxWidth < 0) {
		flowing.setMaxWidth(targetWidth);
	    }
	}

	Drawable drawablePart = drawable;
	while (renderContext.getRemainingHeight() < drawablePart.getHeight()) {
	    Dividable dividable = null;
	    if (drawablePart instanceof Dividable) {
		dividable = (Dividable) drawablePart;
	    } else {
		dividable = new Cutter(drawablePart);
	    }
	    Divided divided = dividable.divide(
		    renderContext.getRemainingHeight(),
		    renderContext.getHeight());
	    drawReletivePartAndMovePosition(renderContext, divided.getFirst(),
		    layoutHint);

	    // new page
	    renderContext.newPage();

	    drawablePart = divided.getTail();
	}

	drawReletivePartAndMovePosition(renderContext, drawablePart, layoutHint);

	if (drawable instanceof WidthRespecting) {
	    if (oldMaxWidth < 0) {
		((WidthRespecting) drawable).setMaxWidth(oldMaxWidth);
	    }
	}
    }

    /**
     * Actually draws the (drawble) part at the
     * {@link RenderContext#getCurrentPosition()} and moves to the new position.
     * Any left or right margin is taken into account to calculate the position
     * and alignment.
     * 
     * @param renderContext
     * @param drawable
     * @param layoutHint
     * @throws IOException
     */
    protected void drawReletivePartAndMovePosition(
	    final RenderContext renderContext, Drawable drawable,
	    final LayoutHint layoutHint) throws IOException {
	PDPageContentStream contentStream = renderContext.getContentStream();
	Document document = renderContext.getDocument();
	float offsetX = 0;
	if (layoutHint instanceof VerticalLayoutHint) {
	    VerticalLayoutHint verticalLayoutHint = (VerticalLayoutHint) layoutHint;
	    Alignment alignment = verticalLayoutHint.getAlignment();
	    float horizontalExtraSpace = renderContext.getWidth()
		    - drawable.getWidth();
	    switch (alignment) {
	    case Right:
		offsetX = horizontalExtraSpace
			- verticalLayoutHint.getMarginRight();
		break;
	    case Center:
		offsetX = horizontalExtraSpace / 2f;
		break;
	    default:
		offsetX = verticalLayoutHint.getMarginLeft();
		break;
	    }
	}

	contentStream.saveGraphicsState();
	contentStream.addRect(document.getMarginLeft(),
		document.getMarginBottom(), renderContext.getWidth(),
		renderContext.getHeight());
	@SuppressWarnings("deprecation")
	contentStream.clipPath(PathIterator.WIND_NON_ZERO);

	drawable.draw(contentStream,
		renderContext.getCurrentPosition().add(offsetX, 0));

	contentStream.restoreGraphicsState();

	renderContext.movePositionBy(0, -drawable.getHeight());
    }
    
    
}
