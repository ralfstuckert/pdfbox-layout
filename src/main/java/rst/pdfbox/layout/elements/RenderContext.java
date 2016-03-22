package rst.pdfbox.layout.elements;

import java.io.Closeable;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.Coords;

public class RenderContext implements Closeable {

	private final Document document;
	private final PDDocument pdDocument;
	private PDPage page;
	private PDPageContentStream contentStream;
	private Coords currentPosition;

	public RenderContext(Document document, PDDocument pdDocument)
			throws IOException {
		this.document = document;
		this.pdDocument = pdDocument;
		newPage();
	}

	public RenderContext(Document document, PDDocument pdDocument, PDPage page,
			PDPageContentStream contentStream) {
		this.document = document;
		this.pdDocument = pdDocument;
		this.page = page;
		this.contentStream = contentStream;
		currentPosition = getUpperLeft();
	}

	public Coords getUpperLeft() {
		return new Coords(document.getMarginLeft(), page.getMediaBox()
				.getHeight() - document.getMarginTop());
	}

	public Coords getCurrentPosition() {
		return currentPosition;
	}

	public void movePositionBy(final float x, final float y) {
		currentPosition = currentPosition.add(x, y);
	}

	public float getWidth() {
		return page.getMediaBox().getWidth() - document.getMarginLeft()
				- document.getMarginRight();
	}

	public float getHeight() {
		return page.getMediaBox().getHeight() - document.getMarginTop()
				- document.getMarginBottom();
	}

	public float getRemainingHeight() {
		return getCurrentPosition().getY() - document.getMarginBottom();
	}

	public Document getDocument() {
		return document;
	}

	public PDDocument getPdDocument() {
		return pdDocument;
	}

	public PDPage getPage() {
		return page;
	}

	public PDPageContentStream getContentStream() {
		return contentStream;
	}

	public void newPage() throws IOException {
		closePage();
		this.page = new PDPage(document.getMediaBox());
		this.pdDocument.addPage(page);
		this.contentStream = new PDPageContentStream(pdDocument, page, true,
				true);
		currentPosition = getUpperLeft();
	}

	public void draw(final Drawable drawable) throws IOException {
		drawable.draw(getContentStream(), getCurrentPosition());
		movePositionBy(0, -drawable.getHeight());
	}

	public void closePage() throws IOException {
		if (contentStream != null) {
			contentStream.close();
		}
	}
	
	@Override
	public void close() throws IOException {
		closePage();
	}
}
