package rst.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public interface RenderListener {

	void beforePage(Document document, PDDocument pdDocument, int pageIndex,
			PDPage page, PDPageContentStream contentStream);

	void afterPage(Document document, PDDocument pdDocument, int pageIndex,
			PDPage page, PDPageContentStream contentStream);
}
