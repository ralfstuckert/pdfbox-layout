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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.LinkStyle;
import rst.pdfbox.layout.text.Position;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    private final static String BULLET = Character.toString((char) 149);
    private final static String DOUBLE_ANGLE = Character.toString((char) 187);

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

    public static void transform(final PDPageContentStream contentStream,
	    float a, float b, float c, float d, float e, float f)
	    throws IOException {
	contentStream.concatenate2CTM(a, b, c, d, e, f);
    }
    
    public static void curveTo(final PDPageContentStream contentStream, float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
	contentStream.addBezier312(x1, y1, x2, y2, x3, y3);
    }
    
    public static void curveTo1(final PDPageContentStream contentStream, float x1, float y1, float x3, float y3) throws IOException {
	contentStream.addBezier31(x1, y1, x3, y3);
    }

    public static void fillNonZero(final PDPageContentStream contentStream) throws IOException {
	contentStream.fill(PathIterator.WIND_NON_ZERO);
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

    public static int getPageRotation(final PDPage page) {
	return page.getRotation() == null ? 0 : page.getRotation();
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
	pdLink.setColour(toPDGamma(color));
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

    private static PDGamma toPDGamma(final Color color) {
	COSArray values = new COSArray();
	values.add(new COSFloat(color.getRed() / 255f));
	values.add(new COSFloat(color.getGreen() / 255f));
	values.add(new COSFloat(color.getBlue() / 255f));
	return new PDGamma(values);
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
