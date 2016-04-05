package rst.pdfbox.layout.util;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.util.Matrix;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    public static void clip(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.clip();
    }

    public static void showText(final PDPageContentStream contentStream, final String text)
	    throws IOException {
	contentStream.showText(text);
    }
    
    public static void setTextTranslation(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
        contentStream.setTextMatrix(Matrix.getTranslateInstance(x, y));
    }

    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	return new PDPageContentStream(pdDocument, page, AppendMode.APPEND, true);
    }

}
