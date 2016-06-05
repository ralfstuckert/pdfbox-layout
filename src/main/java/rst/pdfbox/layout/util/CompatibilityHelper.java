package rst.pdfbox.layout.util;

import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import rst.pdfbox.layout.text.Position;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    private final static String BULLET = Character.toString((char) 149);
    private final static String DOUBLE_ANGLE = Character.toString((char) 187);

    private static final String IMAGE_CACHE = "IMAGE_CACHE";
    private static Map<PDDocument, Map<String, Map<?, ?>>> documentCaches = new WeakHashMap<PDDocument, Map<String, Map<?, ?>>>();

    /**
     * Returns the bullet character for the given level. Actually only two
     * bullets are used for odd and even levels. For odd levels the
     * {@link #BULLET bullet} character is used, for even it is the
     * {@link #DOUBLE_ANGLE double angle}. You may customize this by setting the
     * system properties <code>pdfbox.layout.bullet.odd</code> and/or
     * <code>pdfbox.layout.bullet.even</code>.
     * 
     * @param level
     *            the level to return the bullet for.
     * @return the bullet character for the level.
     */
    public static String getBulletCharacter(final int level) {
	if (level % 2 == 1) {
	    return System.getProperty("pdfbox.layout.bullet.odd", BULLET);
	}
	return System.getProperty("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
    }


    public static void clip(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.clipPath(PathIterator.WIND_NON_ZERO);
    }

    public static void showText(final PDPageContentStream contentStream,
	    final String text) throws IOException {
	contentStream.drawString(text);
    }

    public static void setTextTranslation(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.setTextTranslation(x, y);
    }

    public static void moveTextPosition(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.concatenate2CTM(1, 0, 0, 1, x, y);
    }

    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	return new PDPageContentStream(pdDocument, page, true, true);
    }

    public static void drawImage(final BufferedImage image,
	    final PDDocument document, final PDPageContentStream contentStream,
	    Position upperLeft, final float width, final float height)
	    throws IOException {
	PDXObjectImage cachedImage = getCachedImage(document, image);
	float x = upperLeft.getX();
	float y = upperLeft.getY() - height;
	contentStream.drawXObject(cachedImage, x, y, width, height);
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

    private static synchronized Map<BufferedImage, PDXObjectImage> getImageCache(
	    final PDDocument document) {
	Map<String, Map<?, ?>> documentCache = getDocumentCache(document);
	@SuppressWarnings("unchecked")
	Map<BufferedImage, PDXObjectImage> imageCache = (Map<BufferedImage, PDXObjectImage>) documentCache
		.get(IMAGE_CACHE);
	if (imageCache == null) {
	    imageCache = new HashMap<BufferedImage, PDXObjectImage>();
	    documentCache.put(IMAGE_CACHE, imageCache);
	}
	return imageCache;
    }

    private static synchronized PDXObjectImage getCachedImage(
	    final PDDocument document, final BufferedImage image)
	    throws IOException {
	Map<BufferedImage, PDXObjectImage> imageCache = getImageCache(document);
	PDXObjectImage pdxObjectImage = imageCache.get(image);
	if (pdxObjectImage == null) {
	    pdxObjectImage = new PDPixelMap(document, image);
	    imageCache.put(image, pdxObjectImage);
	}
	return pdxObjectImage;
    }

}
