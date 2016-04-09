package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.elements.Cutter;
import rst.pdfbox.layout.elements.Dividable;
import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Drawable;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.WidthRespecting;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * The column layout divides the page vertically into columns. You can specify
 * the number of columns and the inter-column spacing. The layouting inside a
 * column is similar to the {@link VerticalLayout}. See there for more details
 * on the possiblities.
 */
public class ColumnLayout implements Layout {

    private final int columnCount;
    private float columnSpacing;
    private int columnIndex = 0;

    public ColumnLayout(int columnCount) {
	this(columnCount, 0);
    }

    public ColumnLayout(int columnCount, float columnSpacing) {
	this.columnCount = columnCount;
	this.columnSpacing = columnSpacing;
    }

    public float getColumnWidth(final RenderContext renderContext) {
	return (renderContext.getWidth() - ((columnCount - 1) * columnSpacing))
		/ columnCount;
    }

    public void nextColumn(final RenderContext renderContext)
	    throws IOException {
	if (++columnIndex >= columnCount) {
	    renderContext.newPage();
	    columnIndex = 0;
	} else {
	    float nextColumnX = (getColumnWidth(renderContext) + columnSpacing)
		    * columnIndex;
	    renderContext.resetPositionToUpperLeft();
	    renderContext.movePositionBy(nextColumnX, 0);
	}
    }

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
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @param position
     *            the left upper position to start drawing at.
     * @throws IOException
     *             by pdfbox
     */
    protected void renderAbsolute(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint,
	    final Position position) throws IOException {
	drawable.draw(renderContext.getPdDocument(),
		renderContext.getContentStream(), position);
    }

    /**
     * Renders the drawable at the {@link RenderContext#getCurrentPosition()
     * current position}. This method is responsible taking any top or bottom
     * margin described by the (Vertical-)LayoutHint into account. The actual
     * rendering of the drawable is performed by
     * {@link #layoutAndDrawReleative(RenderContext, Drawable, LayoutHint)}.
     * 
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @throws IOException
     *             by pdfbox
     */
    protected void renderReleative(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint) throws IOException {
	VerticalLayoutHint columnLayoutHint = null;
	if (layoutHint instanceof VerticalLayoutHint) {
	    columnLayoutHint = (VerticalLayoutHint) layoutHint;
	    if (columnLayoutHint.getMarginTop() > 0) {
		layoutAndDrawReleative(renderContext, new VerticalSpacer(
			columnLayoutHint.getMarginTop()), columnLayoutHint);
	    }
	}

	layoutAndDrawReleative(renderContext, drawable, columnLayoutHint);

	if (columnLayoutHint != null) {
	    if (columnLayoutHint.getMarginBottom() > 0) {
		layoutAndDrawReleative(renderContext, new VerticalSpacer(
			columnLayoutHint.getMarginBottom()), columnLayoutHint);
	    }
	}
    }

    /**
     * Adjusts the width of the drawable (if it is {@link WidthRespecting}), and
     * divides it onto multiple pages if necessary. Actual drawing is delegated
     * to
     * {@link #drawReletivePartAndMovePosition(RenderContext, Drawable, LayoutHint, boolean)}
     * .
     * 
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @throws IOException
     *             by pdfbox
     */
    protected void layoutAndDrawReleative(final RenderContext renderContext,
	    Drawable drawable, final LayoutHint layoutHint) throws IOException {

	float targetWidth = getColumnWidth(renderContext);
	boolean movePosition = true;
	VerticalLayoutHint columnLayoutHint = null;
	if (layoutHint instanceof VerticalLayoutHint) {
	    columnLayoutHint = (VerticalLayoutHint) layoutHint;
	    targetWidth -= columnLayoutHint.getMarginLeft();
	    targetWidth -= columnLayoutHint.getMarginRight();
	    movePosition = !columnLayoutHint.isResetY();
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
		    layoutHint, true);

	    nextColumn(renderContext);

	    drawablePart = divided.getTail();
	}

	drawReletivePartAndMovePosition(renderContext, drawablePart,
		layoutHint, movePosition);

	if (drawable instanceof WidthRespecting) {
	    if (oldMaxWidth < 0) {
		((WidthRespecting) drawable).setMaxWidth(oldMaxWidth);
	    }
	}
    }

    /**
     * Actually draws the (drawble) part at the
     * {@link RenderContext#getCurrentPosition()} and - depending on flag
     * <code>movePosition</code> - moves to the new Y position. Any left or
     * right margin is taken into account to calculate the position and
     * alignment.
     * 
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @param movePosition
     *            indicates if the position should be moved (vertically) after
     *            drawing.
     * @throws IOException
     *             by pdfbox
     */
    protected void drawReletivePartAndMovePosition(
	    final RenderContext renderContext, Drawable drawable,
	    final LayoutHint layoutHint, final boolean movePosition)
	    throws IOException {
	PDPageContentStream contentStream = renderContext.getContentStream();
	Document document = renderContext.getDocument();
	float targetWidth = getColumnWidth(renderContext);
	float offsetX = 0;
	if (layoutHint instanceof VerticalLayoutHint) {
	    VerticalLayoutHint columnLayoutHint = (VerticalLayoutHint) layoutHint;
	    Alignment alignment = columnLayoutHint.getAlignment();
	    float horizontalExtraSpace = targetWidth - drawable.getWidth();
	    switch (alignment) {
	    case Right:
		offsetX = horizontalExtraSpace
			- columnLayoutHint.getMarginRight();
		break;
	    case Center:
		offsetX = horizontalExtraSpace / 2f;
		break;
	    default:
		offsetX = 0;
		break;
	    }
	}

	contentStream.saveGraphicsState();
	contentStream.addRect(0, document.getMarginBottom(), document
		.getMediaBox().getWidth(), renderContext.getHeight());
	CompatibilityHelper.clip(contentStream);

	drawable.draw(renderContext.getPdDocument(), contentStream,
		renderContext.getCurrentPosition().add(offsetX, 0));

	contentStream.restoreGraphicsState();

	if (movePosition) {
	    renderContext.movePositionBy(0, -drawable.getHeight());
	}
    }

}
