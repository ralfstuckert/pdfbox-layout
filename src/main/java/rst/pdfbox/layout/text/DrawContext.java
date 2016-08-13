package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * Provides the current page and document to draw to.
 */
public interface DrawContext {

    /**
     * @return the document to draw to.
     */
    public PDDocument getPdDocument();

    /**
     * @return the current page to draw to.
     */
    public PDPage getCurrentPage();
}
