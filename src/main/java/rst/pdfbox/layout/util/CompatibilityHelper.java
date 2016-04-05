package rst.pdfbox.layout.util;

import java.awt.geom.PathIterator;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    @SuppressWarnings("deprecation")
    public static void clip(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.clipPath(PathIterator.WIND_NON_ZERO);
    }

    @SuppressWarnings("deprecation")
    public static void showText(final PDPageContentStream contentStream, final String text)
	    throws IOException {
	contentStream.drawString(text);
    }
    
    @SuppressWarnings("deprecation")
    public static void setTextTranslation(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.setTextTranslation(x, y);
    }

    @SuppressWarnings("deprecation")
    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	return new PDPageContentStream(pdDocument, page, true, true);
    }

}
