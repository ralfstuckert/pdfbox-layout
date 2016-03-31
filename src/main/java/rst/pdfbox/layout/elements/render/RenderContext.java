package rst.pdfbox.layout.elements.render;

import java.io.Closeable;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.text.Position;

/**
 * The render context is a container providing all state of the current
 * rendering process. 
 */
public class RenderContext implements Closeable {

	private final Document document;
	private final PDDocument pdDocument;
	private PDPage page;
	private int pageIndex = 0;
	private PDPageContentStream contentStream;
	private Position currentPosition;

	/**
	 * Creates a render context.
	 * 
	 * @param document
	 * @param pdDocument
	 * @throws IOException
	 */
	public RenderContext(Document document, PDDocument pdDocument)
			throws IOException {
		this.document = document;
		this.pdDocument = pdDocument;
		newPage();
	}

	/**
	 * @return the upper left position in the document respecting the
	 *         {@link Document document} margins.
	 */
	public Position getUpperLeft() {
		return new Position(document.getMarginLeft(), page.getMediaBox()
				.getHeight() - document.getMarginTop());
	}

	/**
	 * @return the current rendering position.
	 */
	public Position getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * Moves the {@link #getCurrentPosition() current position}  relatively by the given offset.
	 * @param x
	 * @param y
	 */
	public void movePositionBy(final float x, final float y) {
		currentPosition = currentPosition.add(x, y);
	}

	/**
	 * @return the width of the page respecting the margins.
	 */
	public float getWidth() {
		return page.getMediaBox().getWidth() - document.getMarginLeft()
				- document.getMarginRight();
	}

	/**
	 * @return the height of the page respecting the margins.
	 */
	public float getHeight() {
		return page.getMediaBox().getHeight() - document.getMarginTop()
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

	/**
	 * Triggers a new page.
	 * @throws IOException
	 */
	public void newPage() throws IOException {
		if (closePage()) {
			++pageIndex;
		}
		this.page = new PDPage(document.getMediaBox());
		this.pdDocument.addPage(page);
		this.contentStream = new PDPageContentStream(pdDocument, page, true,
				true);
		currentPosition = getUpperLeft();

		document.beforePage(this);
	}

	/**
	 * Closes the current page.
	 * @return <code>true</code> if the current page has not been closed before.
	 * @throws IOException
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
