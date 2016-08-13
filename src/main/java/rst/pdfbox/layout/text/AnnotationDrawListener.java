package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

import rst.pdfbox.layout.text.Annotations.AnchorAnnotation;
import rst.pdfbox.layout.text.Annotations.HyperlinkAnnotation;

public class AnnotationDrawListener implements DrawListener {

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
	    if (anchorMap.get(anchorAnnotation.getAnchor()) != null) {
		throw new IllegalArgumentException(
			String.format("anchor with name '%s' already exists"));
	    }
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
	    bounds.setLowerLeftY(upperLeft.getY());
	    bounds.setUpperRightX(upperLeft.getX() + width);
	    bounds.setUpperRightY(upperLeft.getY() + height);
	    links.add(new Hyperlink(bounds, hyperlinkAnnotation.getHyperlink()));
	}
    }

    public void finalizeAnnotations() throws IOException {
	for (Entry<PDPage, List<Hyperlink>> entry : linkMap.entrySet()) {
	    PDPage page = entry.getKey();
	    List<Hyperlink> links = entry.getValue();
	    for (Hyperlink hyperlink : links) {
		PDAnnotationLink pdLink = new PDAnnotationLink();
		PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
		borderStyle.setStyle(PDBorderStyleDictionary.STYLE_BEVELED);
		pdLink.setBorderStyle(borderStyle);
		pdLink.setRectangle(hyperlink.getRect());

		String uri = hyperlink.getHyperlink();
		PDAction action = null;
		if (uri.startsWith("#")) {
		    action = createGotoAction(page, uri);
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

    private PDActionGoTo createGotoAction(PDPage page, String uri) {
	PageAnchor pageAnchor = anchorMap.get(uri.substring(1));
	if (pageAnchor == null) {
	    throw new IllegalArgumentException(String.format(
		    "anchor named '%s' not found", uri.substring(1)));
	}
	PDPageXYZDestination xyzDestination = new PDPageXYZDestination();
	xyzDestination.setPage(page);
	xyzDestination.setLeft((int) pageAnchor.getX());
	xyzDestination.setTop((int) pageAnchor.getY());
	PDActionGoTo gotoAction = new PDActionGoTo();
	gotoAction.setDestination(xyzDestination);
	return gotoAction;
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

    }

    private static class Hyperlink {
	private final PDRectangle rect;
	private final String hyperlink;

	public Hyperlink(PDRectangle rect, String hyperlink) {
	    this.rect = rect;
	    this.hyperlink = hyperlink;
	}

	public PDRectangle getRect() {
	    return rect;
	}

	public String getHyperlink() {
	    return hyperlink;
	}

    }
}
