package rst.pdfbox.layout.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.render.Layout;
import rst.pdfbox.layout.elements.render.LayoutHint;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.elements.render.RenderListener;
import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Constants;

/**
 * The central class for creating a document.
 */
public class Document implements RenderListener {

    private final float marginLeft; 
    private final float marginRight;
    private final float marginTop;
    private final float marginBottom;
    private final PDRectangle mediaBox;
    private final Orientation orientation;

    private final List<Entry<Element, LayoutHint>> elements = new ArrayList<>();
    private final List<RenderListener> renderListener = new CopyOnWriteArrayList<RenderListener>();

    private PDDocument pdDocument;

    /**
     * Creates a Document based on the given media box. By default, a
     * {@link VerticalLayout} is used.
     * 
     * @param mediaBox
     *            the media box to use.
     */
    public Document(PDRectangle mediaBox) {
	this(mediaBox, 0, 0, 0, 0);
    }

    /**
     * Creates a Document based on the given media box and margins. By default,
     * a {@link VerticalLayout} is used.
     * 
     * @param mediaBox
     *            the media box to use.
     * @param marginLeft
     *            the left margin
     * @param marginRight
     *            the right margin
     * @param marginTop
     *            the top margin
     * @param marginBottom
     *            the bottom margin
     */
    public Document(PDRectangle mediaBox, float marginLeft, float marginRight,
	    float marginTop, float marginBottom) {
	this(mediaBox, Orientation.Portrait, marginLeft, marginRight,
		marginTop, marginBottom);
    }

    /**
     * Creates a Document based on the given media box and margins. By default,
     * a {@link VerticalLayout} is used.
     * 
     * @param mediaBox
     *            the media box to use.
     * @param orientation
     *            the orientation to use.
     * @param marginLeft
     *            the left margin
     * @param marginRight
     *            the right margin
     * @param marginTop
     *            the top margin
     * @param marginBottom
     *            the bottom margin
     */
    public Document(PDRectangle mediaBox, Orientation orientation,
	    float marginLeft, float marginRight, float marginTop,
	    float marginBottom) {
	this.mediaBox = mediaBox;
	this.orientation = orientation;
	this.marginLeft = marginLeft;
	this.marginRight = marginRight;
	this.marginTop = marginTop;
	this.marginBottom = marginBottom;
    }

    /**
     * Adds an element to the document using a {@link VerticalLayoutHint}.
     * 
     * @param element
     *            the element to add
     */
    public void add(final Element element) {
	add(element, new VerticalLayoutHint());
    }

    /**
     * Adds an element with the given layout hint.
     * 
     * @param element
     *            the element to add
     * @param layoutHint
     *            the hint for the {@link Layout}.
     */
    public void add(final Element element, final LayoutHint layoutHint) {
	elements.add(createEntry(element, layoutHint));
    }

    private Entry<Element, LayoutHint> createEntry(final Element element,
	    final LayoutHint layoutHint) {
	return new SimpleEntry<Element, LayoutHint>(element, layoutHint);
    }

    /**
     * Removes the given element.
     * 
     * @param element
     *            the element to remove.
     */
    public void remove(final Element element) {
	elements.remove(element);
    }

    /**
     * @return the left document margin.
     */
    public float getMarginLeft() {
	return marginLeft;
    }

    /**
     * @return the right document margin.
     */
    public float getMarginRight() {
	return marginRight;
    }

    /**
     * @return the top document margin.
     */
    public float getMarginTop() {
	return marginTop;
    }

    /**
     * @return the bottom document margin.
     */
    public float getMarginBottom() {
	return marginBottom;
    }

    /**
     * @return the media box to use.
     */
    public PDRectangle getMediaBox() {
	return mediaBox;
    }

    /**
     * @return the orientation to use.
     */
    public Orientation getOrientation() {
	return orientation;
    }
    
    /**
     * @return the media box width minus margins.
     */
    public float getPageWidth() {
	return getMediaBox().getWidth() - getMarginLeft() - getMarginRight();
    }

    /**
     * @return the media box height minus margins.
     */
    public float getPageHeight() {
	return getMediaBox().getHeight() - getMarginTop() - getMarginBottom();
    }

    /**
     * Returns the {@link PDDocument} to be created by method {@link #render()}.
     * Beware that this PDDocument is released after rendering. This means each
     * rendering process creates a new PDDocument.
     * 
     * @return the PDDocument to be used on the next call to {@link #render()}.
     */
    public PDDocument getPDDocument() {
	if (pdDocument == null) {
	    pdDocument = new PDDocument();
	}
	return pdDocument;
    }

    /**
     * Called after {@link #render()} in order to release the current document.
     */
    protected void resetPDDocument() {
	this.pdDocument = null;
    }

    /**
     * Renders all elements and returns the resulting {@link PDDocument}.
     * 
     * @return the resulting {@link PDDocument}
     * @throws IOException
     *             by pdfbox
     */
    public PDDocument render() throws IOException {
	PDDocument document = getPDDocument();
	RenderContext renderContext = new RenderContext(this, document);
	for (Entry<Element, LayoutHint> entry : elements) {
	    Element element = entry.getKey();
	    LayoutHint layoutHint = entry.getValue();
	    boolean success = renderContext.render(renderContext, element,
		    layoutHint);
	    if (!success) {
		throw new IllegalArgumentException(
			String.format(
				"neither layout %s nor the render context knows what to do with %s",
				renderContext.getLayout(), element));

	    }
	}
	renderContext.close();

	resetPDDocument();
	return document;
    }

    /**
     * {@link #render() Renders} the document and saves it to the given file.
     * 
     * @param file
     *            the file to save to.
     * @throws IOException
     *             by pdfbox
     */
    public void save(final File file) throws IOException {
	try (OutputStream out = new FileOutputStream(file)) {
	    save(out);
	}
    }

    /**
     * {@link #render() Renders} the document and saves it to the given output
     * stream.
     * 
     * @param output
     *            the stream to save to.
     * @throws IOException
     *             by pdfbox
     */
    public void save(final OutputStream output) throws IOException {
	try (PDDocument document = render()) {
	    try {
		document.save(output);
	    } catch (IOException ioe) {
		throw ioe;
	    } catch (Exception e) {
		throw new IOException(e);
	    }
	}
    }

    /**
     * Adds a {@link RenderListener} that will be notified during
     * {@link #render() rendering}.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addRenderListener(final RenderListener listener) {
	if (listener != null) {
	    renderListener.add(listener);
	}
    }

    /**
     * Removes a {@link RenderListener} .
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeRenderListener(final RenderListener listener) {
	renderListener.remove(listener);
    }

    @Override
    public void beforePage(final RenderContext renderContext)
	    throws IOException {
	for (RenderListener listener : renderListener) {
	    listener.beforePage(renderContext);
	}
    }

    @Override
    public void afterPage(final RenderContext renderContext) throws IOException {
	for (RenderListener listener : renderListener) {
	    listener.afterPage(renderContext);
	}
    }
    
    /**
     * @return a document builder.
     */
    public static DocumentBuilder builder() {
	return new DocumentBuilder();
    }
    
    public static class DocumentBuilder {
	    private float marginLeft;
	    private float marginRight;
	    private float marginTop;
	    private float marginBottom;
	    private PDRectangle mediaBox = Constants.A4;
	    private Orientation orientation = Orientation.Portrait;

	    protected DocumentBuilder() {}

	    public Document build() {
		return new Document(mediaBox, orientation, marginLeft, marginRight, marginTop, marginBottom);
	    }
	    public DocumentBuilder marginLeft(float marginLeft) {
	        this.marginLeft = marginLeft;
	        return this;
	    }

	    public DocumentBuilder marginRight(float marginRight) {
	        this.marginRight = marginRight;
	        return this;
	    }

	    public DocumentBuilder marginTop(float marginTop) {
	        this.marginTop = marginTop;
	        return this;
	    }

	    public DocumentBuilder marginBottom(float marginBottom) {
	        this.marginBottom = marginBottom;
	        return this;
	    }

	    public DocumentBuilder mediaBox(PDRectangle mediaBox) {
	        this.mediaBox = mediaBox;
	        return this;
	    }

	    public DocumentBuilder orientation(Orientation orientation) {
	        this.orientation = orientation;
	        return this;
	    }
	    
	    
    }

}
