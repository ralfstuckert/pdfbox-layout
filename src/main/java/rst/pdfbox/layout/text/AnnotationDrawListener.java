package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

import rst.pdfbox.layout.text.Annotations.AnchorAnnotation;
import rst.pdfbox.layout.text.Annotations.HyperlinkAnnotation;
import rst.pdfbox.layout.text.Annotations.HyperlinkAnnotation.LinkStyle;

public class AnnotationDrawListener implements DrawListener {

    private static PDBorderStyleDictionary noBorder;
    
    private final DrawContext drawContext;
    private Map<String, PageAnchor> anchorMap = new HashMap<String, PageAnchor>();
    private Map<PDPage, List<Hyperlink>> linkMap = new HashMap<PDPage, List<Hyperlink>>();

    public AnnotationDrawListener(final DrawContext drawContext) {
	this.drawContext = drawContext;
    }

    @Override
    public void drawn(Object drawnObject, Position upperLeft, float width,
	    float height) {
	if (!(drawnObject instanceof AnnotatedStyledText)) {
	    return;
	}

	AnnotatedStyledText annotatedText = (AnnotatedStyledText) drawnObject;
	handleHyperlinkAnnotations(annotatedText, upperLeft, width, height);
	handleAnchorAnnotations(annotatedText, upperLeft);
    }

    /**
     * @param annotatedText
     * @param upperLeft
     */
    protected void handleAnchorAnnotations(AnnotatedStyledText annotatedText,
	    Position upperLeft) {
	Iterable<AnchorAnnotation> anchorAnnotations = annotatedText
		.getAnnotationsOfType(AnchorAnnotation.class);
	for (AnchorAnnotation anchorAnnotation : anchorAnnotations) {
	    anchorMap.put(
		    anchorAnnotation.getAnchor(),
		    new PageAnchor(drawContext.getCurrentPage(), upperLeft
			    .getX(), upperLeft.getY()));
	}
    }

    protected void handleHyperlinkAnnotations(
	    AnnotatedStyledText annotatedText, Position upperLeft, float width,
	    float height) {
	Iterable<HyperlinkAnnotation> hyperlinkAnnotations = annotatedText
		.getAnnotationsOfType(HyperlinkAnnotation.class);
	for (HyperlinkAnnotation hyperlinkAnnotation : hyperlinkAnnotations) {
	    List<Hyperlink> links = linkMap.get(drawContext.getCurrentPage());
	    if (links == null) {
		links = new ArrayList<Hyperlink>();
		linkMap.put(drawContext.getCurrentPage(), links);
	    }
	    PDRectangle bounds = new PDRectangle();
	    bounds.setLowerLeftX(upperLeft.getX());
	    bounds.setLowerLeftY(upperLeft.getY() - height);
	    bounds.setUpperRightX(upperLeft.getX() + width);
	    bounds.setUpperRightY(upperLeft.getY());

	    links.add(new Hyperlink(bounds,
		    toPDGamma(annotatedText.getColor()),
		    toBorderStyle(hyperlinkAnnotation.getLinkStyle()),
		    hyperlinkAnnotation.getHyperlink()));
	}
    }

    public void finalizeAnnotations() throws IOException {
	for (Entry<PDPage, List<Hyperlink>> entry : linkMap.entrySet()) {
	    PDPage page = entry.getKey();
	    List<Hyperlink> links = entry.getValue();
	    for (Hyperlink hyperlink : links) {
		PDAnnotationLink pdLink = new PDAnnotationLink();
		if (hyperlink.getBorderStyle() != null) {
			pdLink.setBorderStyle(hyperlink.getBorderStyle());
		} else {
		    pdLink.setBorderStyle(getNoBorder());
		}
		pdLink.setRectangle(hyperlink.getRect());
		pdLink.setColour(hyperlink.getColor());

		String uri = hyperlink.getHyperlink();
		PDAction action = null;
		if (uri.startsWith("#")) {
		    action = createGotoAction(uri.substring(1));
		} else {
		    PDActionURI actionUri = new PDActionURI();
		    actionUri.setURI(uri);
		    action = actionUri;
		}
		pdLink.setAction(action);
		page.getAnnotations().add(pdLink);
	    }

	}
    }

    private PDActionGoTo createGotoAction(String anchor) {
	PageAnchor pageAnchor = anchorMap.get(anchor);
	if (pageAnchor == null) {
	    throw new IllegalArgumentException(String.format(
		    "anchor named '%s' not found", anchor));
	}
	PDPageXYZDestination xyzDestination = new PDPageXYZDestination();
	xyzDestination.setPage(pageAnchor.getPage());
	xyzDestination.setLeft((int) pageAnchor.getX());
	xyzDestination.setTop((int) pageAnchor.getY());
	PDActionGoTo gotoAction = new PDActionGoTo();
	gotoAction.setDestination(xyzDestination);
	return gotoAction;
    }

    private PDGamma toPDGamma(final Color color) {
	COSArray values = new COSArray();
	values.add(new COSFloat(color.getRed() / 255f));
	values.add(new COSFloat(color.getGreen() / 255f));
	values.add(new COSFloat(color.getBlue() / 255f));
	return new PDGamma(values);
    }

    private PDBorderStyleDictionary toBorderStyle(final LinkStyle linkStyle) {
	if (linkStyle == LinkStyle.none) {
	    return null;
	}
	PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
	borderStyle.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
	return borderStyle;
    }
    
    private static PDBorderStyleDictionary getNoBorder() {
	if (noBorder == null) {
	    noBorder = new PDBorderStyleDictionary();
	    noBorder.setWidth(0);
	}
	return noBorder;
    }

    private static class PageAnchor {
	private final PDPage page;
	private final float x;
	private final float y;

	public PageAnchor(PDPage page, float x, float y) {
	    this.page = page;
	    this.x = x;
	    this.y = y;
	}

	public PDPage getPage() {
	    return page;
	}

	public float getX() {
	    return x;
	}

	public float getY() {
	    return y;
	}

	@Override
	public String toString() {
	    return "PageAnchor [page=" + page + ", x=" + x + ", y=" + y + "]";
	}

    }

    private static class Hyperlink {
	private final PDRectangle rect;
	private final PDGamma color;
	private final String hyperlink;
	private final PDBorderStyleDictionary borderStyle;

	public Hyperlink(PDRectangle rect, PDGamma color,
		PDBorderStyleDictionary borderStyle, String hyperlink) {
	    this.rect = rect;
	    this.color = color;
	    this.hyperlink = hyperlink;
	    this.borderStyle = borderStyle;
	}

	public PDRectangle getRect() {
	    return rect;
	}

	public PDGamma getColor() {
	    return color;
	}

	public String getHyperlink() {
	    return hyperlink;
	}

	public PDBorderStyleDictionary getBorderStyle() {
	    return borderStyle;
	}

	@Override
	public String toString() {
	    return "Hyperlink [rect=" + rect + ", color=" + color
		    + ", hyperlink=" + hyperlink + ", borderStyle="
		    + borderStyle + "]";
	}

    }
}
