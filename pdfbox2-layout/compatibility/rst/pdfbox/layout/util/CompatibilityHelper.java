package rst.pdfbox.layout.util;

import java.awt.Color;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.util.Matrix;

import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.LinkStyle;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    private final static String BULLET = "\u2022";
    private final static String DOUBLE_ANGLE = "\u00bb";

    private static final String IMAGE_CACHE = "IMAGE_CACHE";
    private static Map<PDDocument, Map<String, Map<?, ?>>> documentCaches = new WeakHashMap<PDDocument, Map<String, Map<?, ?>>>();
    private static PDBorderStyleDictionary noBorder;

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
     * @return the bullet character for the leve.
     */
    public static String getBulletCharacter(final int level) {
	if (level % 2 == 1) {
	    return System.getProperty("pdfbox.layout.bullet.odd", BULLET);
	}
	return System.getProperty("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
    }

    public static void clip(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.clip();
    }

    public static void transform(final PDPageContentStream contentStream,
	    float a, float b, float c, float d, float e, float f)
	    throws IOException {
	contentStream.transform(new Matrix(a, b, c, d, e, f));
    }

    public static void curveTo(final PDPageContentStream contentStream, float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
	contentStream.curveTo(x1, y1, x2, y2, x3, y3);
    }
    
    public static void curveTo1(final PDPageContentStream contentStream, float x1, float y1, float x3, float y3) throws IOException {
	contentStream.curveTo1(x1, y1, x3, y3);
    }

    public static void fillNonZero(final PDPageContentStream contentStream) throws IOException {
	contentStream.fill();
    }

    public static void showText(final PDPageContentStream contentStream,
	    final String text) throws IOException {
	contentStream.showText(text);
    }

    public static void setTextTranslation(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.setTextMatrix(Matrix.getTranslateInstance(x, y));
    }

    public static void moveTextPosition(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.transform(new Matrix(1, 0, 0, 1, x, y));
    }

    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	// stay compatible with 2.0.0-RC3
	return new PDPageContentStream(pdDocument, page, true, true);
	// return new PDPageContentStream(pdDocument, page, AppendMode.APPEND,
	// true);
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

    public static int getPageRotation(final PDPage page) {
	return page.getRotation();
    }

    public static PDAnnotationLink createLink(PDRectangle rect, Color color,
	    LinkStyle linkStyle, final String uri) {
	PDAnnotationLink pdLink = createLink(rect, color, linkStyle);

	PDActionURI actionUri = new PDActionURI();
	actionUri.setURI(uri);
	pdLink.setAction(actionUri);
	return pdLink;
    }

    public static PDAnnotationLink createLink(PDRectangle rect, Color color,
	    LinkStyle linkStyle, final PDDestination destination) {
	PDAnnotationLink pdLink = createLink(rect, color, linkStyle);

	PDActionGoTo gotoAction = new PDActionGoTo();
	gotoAction.setDestination(destination);
	pdLink.setAction(gotoAction);
	return pdLink;
    }

    private static PDAnnotationLink createLink(PDRectangle rect, Color color,
	    LinkStyle linkStyle) {
	PDAnnotationLink pdLink = new PDAnnotationLink();
	pdLink.setBorderStyle(toBorderStyle(linkStyle));
	pdLink.setRectangle(rect);
	pdLink.setColor(toPDColor(color));
	return pdLink;
    }

    private static PDBorderStyleDictionary toBorderStyle(
	    final LinkStyle linkStyle) {
	if (linkStyle == LinkStyle.none) {
	    return getNoBorder();
	}
	PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
	borderStyle.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
	return borderStyle;
    }

    private static PDColor toPDColor(final Color color) {
        float[] components = new float[] {
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f };
	return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    private static PDBorderStyleDictionary getNoBorder() {
	if (noBorder == null) {
	    noBorder = new PDBorderStyleDictionary();
	    noBorder.setWidth(0);
	}
	return noBorder;
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

    private static synchronized PDImageXObject getCachedImage(
	    final PDDocument document, final BufferedImage image)
	    throws IOException {
	Map<BufferedImage, PDImageXObject> imageCache = getImageCache(document);
	PDImageXObject pdxObjectImage = imageCache.get(image);
	if (pdxObjectImage == null) {
	    pdxObjectImage = LosslessFactory.createFromImage(document, image);
	    imageCache.put(image, pdxObjectImage);
	}
	return pdxObjectImage;
    }

}
