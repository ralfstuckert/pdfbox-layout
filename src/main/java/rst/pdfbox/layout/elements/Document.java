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

    /**
     * A4 portrait without margins.
     */
    public final static PageFormat DEFAULT_PAGE_FORMAT = new PageFormat(
	    Constants.A4, Orientation.Portrait, 0f, 0f, 0f, 0f);

    private final List<Entry<Element, LayoutHint>> elements = new ArrayList<>();
    private final List<RenderListener> renderListener = new CopyOnWriteArrayList<RenderListener>();

    private PDDocument pdDocument;
    private PageFormat pageFormat;

    /**
     * Creates a Document using the {@link #DEFAULT_PAGE_FORMAT}.
     */
    public Document() {
	this(DEFAULT_PAGE_FORMAT);
    }

    /**
     * Creates a Document based on the given media box. By default, a
     * {@link VerticalLayout} is used.
     * 
     * @param mediaBox
     *            the media box to use.
     * @deprecated use {@link #Document(PageFormat)} instead.
     */
    @Deprecated
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
     * @deprecated use {@link #Document(PageFormat)} instead.
     */
    @Deprecated
    public Document(PDRectangle mediaBox, float marginLeft, float marginRight,
	    float marginTop, float marginBottom) {
	this(new PageFormat(mediaBox, Orientation.Portrait, marginLeft,
		marginRight, marginTop, marginBottom));
    }

    /**
     * Creates a Document based on the given page format. By default, a
     * {@link VerticalLayout} is used.
     * 
     * @param pageFormat
     *            the page format box to use.
     */
    public Document(final PageFormat pageFormat) {
	this.pageFormat = pageFormat.useDefaults(DEFAULT_PAGE_FORMAT);
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
     * @return the page format to use as default.
     */
    public PageFormat getPageFormat() {
	return pageFormat;
    }

    /**
     * @return the left document margin.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public float getMarginLeft() {
	return getPageFormat().getMarginLeft();
    }

    /**
     * @return the right document margin.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public float getMarginRight() {
	return getPageFormat().getMarginRight();
    }

    /**
     * @return the top document margin.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public float getMarginTop() {
	return getPageFormat().getMarginTop();
    }

    /**
     * @return the bottom document margin.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public float getMarginBottom() {
	return getPageFormat().getMarginBottom();
    }

    /**
     * @return the media box to use.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public PDRectangle getMediaBox() {
	return getPageFormat().getMediaBox();
    }

    /**
     * @return the orientation to use.
     * @deprecated use {@link #getPageFormat()} instead.
     */
    @Deprecated
    public Orientation getOrientation() {
	return getPageFormat().getOrientation();
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

}
