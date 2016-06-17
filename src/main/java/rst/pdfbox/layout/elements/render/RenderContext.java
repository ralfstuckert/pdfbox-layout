package rst.pdfbox.layout.elements.render;

import java.io.Closeable;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Element;
import rst.pdfbox.layout.elements.Orientation;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.PositionControl;
import rst.pdfbox.layout.elements.PositionControl.MarkPosition;
import rst.pdfbox.layout.elements.PositionControl.MovePosition;
import rst.pdfbox.layout.elements.PositionControl.SetPosition;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * The render context is a container providing all state of the current
 * rendering process.
 */
public class RenderContext implements Layout, Closeable {

    private final Document document;
    private final PDDocument pdDocument;
    private PDPage page;
    private int pageIndex = 0;
    private PDPageContentStream contentStream;
    private Position currentPosition;
    private Position markedPosition;
    private Layout layout = new VerticalLayout();
    private PageFormat pageFormat;

    /**
     * Creates a render context.
     * 
     * @param document
     *            the document to render.
     * @param pdDocument
     *            the underlying pdfbox document.
     * @throws IOException
     *             by pdfbox.
     */
    public RenderContext(Document document, PDDocument pdDocument)
	    throws IOException {
	this.document = document;
	this.pdDocument = pdDocument;
	newPage();
    }

    /**
     * @return the current {@link Layout} used for rendering.
     */
    public Layout getLayout() {
	return layout;
    }

    /**
     * Sets the current {@link Layout} used for rendering.
     * 
     * @param layout
     *            the new layout.
     */
    public void setLayout(Layout layout) {
	this.layout = layout;
    }

    /**
     * @return the orientation to use for the page. If no special
     *         {@link #setPageFormat(PageFormat) page format} is set, the
     *         {@link Document#getOrientation() document orientation} is used.
     */
    public Orientation getOrientation() {
	if (pageFormat == null) {
	    return document.getOrientation();
	}
	return pageFormat.getOrientation();
    }

    /**
     * @return the media box to use for the page. If no special
     *         {@link #setPageFormat(PageFormat) page format} is set, the
     *         {@link Document#getMediaBox() document media box} is used.
     */
    public PDRectangle getMediaBox() {
	if (pageFormat == null) {
	    return document.getMediaBox();
	}
	return pageFormat.getMediaBox();
    }

    public void setPageFormat(final PageFormat pageFormat) {
	this.pageFormat = pageFormat;
    }

    /**
     * @return the upper left position in the document respecting the
     *         {@link Document document} margins.
     */
    public Position getUpperLeft() {
	return new Position(document.getMarginLeft(), getPageHeight()
		- document.getMarginTop());
    }

    /**
     * @return the lower right position in the document respecting the
     *         {@link Document document} margins.
     */
    public Position getLowerRight() {
	return new Position(getPageWidth() - document.getMarginRight(),
		document.getMarginBottom());
    }

    /**
     * @return the current rendering position.
     */
    public Position getCurrentPosition() {
	return currentPosition;
    }

    /**
     * @return the {@link PositionControl#MARKED_POSITION}.
     */
    public Position getMarkedPosition() {
	return markedPosition;
    }

    protected void setMarkedPosition(Position markedPosition) {
	this.markedPosition = markedPosition;
    }

    /**
     * Moves the {@link #getCurrentPosition() current position} relatively by
     * the given offset.
     * 
     * @param x
     *            to move horizontally.
     * @param y
     *            to move vertically.
     */
    public void movePositionBy(final float x, final float y) {
	currentPosition = currentPosition.add(x, y);
    }

    /**
     * Resets the position to {@link #getUpperLeft()}.
     */
    public void resetPositionToUpperLeft() {
	currentPosition = getUpperLeft();
    }

    /**
     * @return the orientation of the current page
     */
    protected Orientation getPageOrientation() {
	if (getPageWidth() > getPageHeight()) {
	    return Orientation.Landscape;
	}
	return Orientation.Portrait;
    }

    /**
     * @return <code>true</code> if the page is rotated by 90/270 degrees.
     */
    public boolean isPageTilted() {
	return page.getRotation() != null
		&& (page.getRotation() == 90 || page.getRotation() == 270);
    }

    /**
     * @return the page' width, or - if {@link #isPageTilted() rotated} - the
     *         height.
     */
    public float getPageWidth() {
	if (isPageTilted()) {
	    return page.getMediaBox().getHeight();
	}
	return page.getMediaBox().getWidth();
    }

    /**
     * @return the page' height, or - if {@link #isPageTilted() rotated} - the
     *         width.
     */
    public float getPageHeight() {
	if (isPageTilted()) {
	    return page.getMediaBox().getWidth();
	}
	return page.getMediaBox().getHeight();
    }

    /**
     * @return the {@link #getPageWidth() width of the page} respecting the
     *         margins.
     */
    public float getWidth() {
	return getPageWidth() - document.getMarginLeft()
		- document.getMarginRight();
    }

    /**
     * @return the {@link #getPageHeight() height of the page} respecting the
     *         margins.
     */
    public float getHeight() {
	return getPageHeight() - document.getMarginTop()
		- document.getMarginBottom();
    }

    /**
     * @return the remaining height on the page.
     */
    public float getRemainingHeight() {
	return getCurrentPosition().getY() - document.getMarginBottom();
    }

    /**
     * @return the document.
     */
    public Document getDocument() {
	return document;
    }

    /**
     * @return the PDDocument.
     */
    public PDDocument getPdDocument() {
	return pdDocument;
    }

    /**
     * @return the current PDPage.
     */
    public PDPage getPage() {
	return page;
    }

    /**
     * @return the current PDPageContentStream.
     */
    public PDPageContentStream getContentStream() {
	return contentStream;
    }

    /**
     * @return the current page index (starting from 0).
     */
    public int getPageIndex() {
	return pageIndex;
    }

    @Override
    public boolean render(RenderContext renderContext, Element element,
	    LayoutHint layoutHint) throws IOException {
	boolean success = getLayout()
		.render(renderContext, element, layoutHint);
	if (success) {
	    return true;
	}
	if (element == ControlElement.NEWPAGE) {
	    newPage();
	    return true;
	}
	if (element instanceof PositionControl) {
	    return render((PositionControl) element);
	}
	if (element instanceof PageFormat) {
	    setPageFormat((PageFormat)element);
	    return true;
	}
	if (element instanceof Layout) {
	    setLayout((Layout) element);
	    return true;
	}
	return false;
    }

    protected boolean render(final PositionControl positionControl) {
	if (positionControl instanceof MarkPosition) {
	    setMarkedPosition(getCurrentPosition());
	    return true;
	}
	if (positionControl instanceof SetPosition) {
	    SetPosition setPosition = (SetPosition) positionControl;
	    Float x = setPosition.getX();
	    if (x == PositionControl.MARKED_POSITION) {
		x = getMarkedPosition().getX();
	    }
	    if (x == null) {
		x = getCurrentPosition().getX();
	    }
	    Float y = setPosition.getY();
	    if (y == PositionControl.MARKED_POSITION) {
		y = getMarkedPosition().getY();
	    }
	    if (y == null) {
		y = getCurrentPosition().getY();
	    }
	    Position newPosition = new Position(x, y);
	    currentPosition = newPosition;
	    return true;
	}
	if (positionControl instanceof MovePosition) {
	    MovePosition movePosition = (MovePosition) positionControl;
	    movePositionBy(movePosition.getX(), movePosition.getY());
	    return true;
	}
	return false;
    }

    /**
     * Triggers a new page.
     * 
     * @throws IOException
     *             by pdfbox
     */
    public void newPage() throws IOException {
	if (closePage()) {
	    ++pageIndex;
	}
	this.page = new PDPage(getMediaBox());
	this.pdDocument.addPage(page);
	this.contentStream = CompatibilityHelper
		.createAppendablePDPageContentStream(pdDocument, page);
	
	// fix orientation
	if (getPageOrientation() != getOrientation()) {
	    if (isPageTilted()) {
		page.setRotation(0);
	    } else {
		page.setRotation(90);
		contentStream.concatenate2CTM(0, 1, -1, 0, getPageHeight(), 0);
	    }
	}

	resetPositionToUpperLeft();
	document.beforePage(this);
    }

    /**
     * Closes the current page.
     * 
     * @return <code>true</code> if the current page has not been closed before.
     * @throws IOException
     *             by pdfbox
     */
    public boolean closePage() throws IOException {
	if (contentStream != null) {
	    document.afterPage(this);

	    contentStream.close();
	    contentStream = null;
	    return true;
	}
	return false;
    }

    @Override
    public void close() throws IOException {
	closePage();
    }

}
