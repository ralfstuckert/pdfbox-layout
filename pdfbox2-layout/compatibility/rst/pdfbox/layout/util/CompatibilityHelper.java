package rst.pdfbox.layout.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.util.Matrix;

import rst.pdfbox.layout.text.Position;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    private static final String IMAGE_CACHE = "IMAGE_CACHE";
    private static Map<PDDocument, Map<String, Map<?, ?>>> documentCaches = new WeakHashMap<PDDocument, Map<String, Map<?, ?>>>();

    public static String getBulletCharacter(final int level) {
	return "\u2022";
    }
    
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

    public static void moveTextPositionByAmount(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.newLineAtOffset(x, y);
    }

    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	// stay compatible with 2.0.0-RC3
	return new PDPageContentStream(pdDocument, page, true, true);
//	return new PDPageContentStream(pdDocument, page, AppendMode.APPEND, true);
    }

    public static void drawImage(final BufferedImage image,
	    final PDDocument document, final PDPageContentStream contentStream,
	    Position upperLeft, final float width, final float height)
	    throws IOException {
	PDImageXObject cachedImage = getCachedImage(document, image);
	float x = upperLeft.getX();
	float y = upperLeft.getY() - height;
	contentStream.drawImage(cachedImage, x, y, width, height);
    }


    private static synchronized Map<String, Map<?, ?>> getDocumentCache(
	    final PDDocument document) {
	Map<String, Map<?, ?>> cache = documentCaches.get(document);
	if (cache == null) {
	    cache = new HashMap<String, Map<?, ?>>();
	    documentCaches.put(document, cache);
	}
	return cache;
    }

    private static synchronized Map<BufferedImage, PDImageXObject> getImageCache(
	    final PDDocument document) {
	Map<String, Map<?, ?>> documentCache = getDocumentCache(document);
	@SuppressWarnings("unchecked")
	Map<BufferedImage, PDImageXObject> imageCache = (Map<BufferedImage, PDImageXObject>) documentCache
		.get(IMAGE_CACHE);
	if (imageCache == null) {
	    imageCache = new HashMap<BufferedImage, PDImageXObject>();
	    documentCache.put(IMAGE_CACHE, imageCache);
	}
	return imageCache;
    }

    private static synchronized PDImageXObject getCachedImage(final PDDocument document,
	    final BufferedImage image) throws IOException {
	Map<BufferedImage, PDImageXObject> imageCache = getImageCache(document);
	PDImageXObject pdxObjectImage = imageCache.get(image);
	if (pdxObjectImage == null) {
	    pdxObjectImage = LosslessFactory.createFromImage(document, image);
	    imageCache.put(image, pdxObjectImage);
	}
	return pdxObjectImage;
    }

}
