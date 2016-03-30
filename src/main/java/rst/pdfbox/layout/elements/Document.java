package rst.pdfbox.layout.elements;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

/**
 * The central class for creating a document.
 */
public class Document implements RenderListener {

	private final float marginLeft;
	private final float marginRight;
	private final float marginTop;
	private final float marginBottom;
	private final PDRectangle mediaBox;

	private final Map<Element, LayoutHint> elements = new LinkedHashMap<>();
	private final List<RenderListener> renderListener = new CopyOnWriteArrayList<RenderListener>();

	private Layout layout = new VerticalLayout();

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
	 * @param marginRight
	 * @param marginTop
	 * @param marginBottom
	 */
	public Document(PDRectangle mediaBox, float marginLeft, float marginRight,
			float marginTop, float marginBottom) {
		this.mediaBox = mediaBox;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
	}

	/**
	 * Adds an element to the document using a {@link VerticalLayoutHint}.
	 * @param element
	 */
	public void add(final Element element) {
		add(element, new VerticalLayoutHint());
	}

	/**
	 * Adds an element with the given layout hint.
	 * @param element
	 * @param layoutHint
	 */
	public void add(final Element element, final LayoutHint layoutHint) {
		elements.put(element, layoutHint);
	}

	/**
	 * Removes the given element.
	 * @param element
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

	public PDRectangle getMediaBox() {
		return mediaBox;
	}

	/**
	 * Renders all elements and returns the resulting {@link PDDocument}.
	 * @return the resulting {@link PDDocument}
	 * @throws IOException
	 */
	public PDDocument render() throws IOException {
		PDDocument document = new PDDocument();
		RenderContext renderContext = new RenderContext(this, document);
		for (Entry<Element, LayoutHint> entry : elements.entrySet()) {
			Element element = entry.getKey();
			LayoutHint layoutHint = entry.getValue();

			if (element instanceof Drawable) {
				layout.render(renderContext, (Drawable) element, layoutHint);
			}
			if (element == ControlElement.NEWPAGE) {
				renderContext.newPage();
			}
			if (element instanceof Layout) {
				layout = (Layout) element;
			}
		}
		renderContext.close();
		return document;
	}

	/**
	 * {@link #render() Renders} the document and saves it to the given file.
	 * @param file
	 * @throws IOException
	 */
	public void save(final File file) throws IOException {
		try (PDDocument document = render()) {
			document.save(file);
		}
	}

	/**
	 * {@link #render() Renders} the document and saves it to the given output stream.
	 * @param output
	 * @throws IOException
	 */
	public void save(final OutputStream output) throws IOException {
		try (PDDocument document = render()) {
			document.save(output);
		}
	}

	/**
	 * Adds a {@link RenderListener} that will be notified during {@link #render() rendering}.
	 * @param listener
	 */
	public void addRenderListener(final RenderListener listener) {
		if (listener != null) {
			renderListener.add(listener);
		}
	}

	/**
	 * Removes a {@link RenderListener} .
	 * @param listener
	 */
	public void removeRenderListener(final RenderListener listener) {
		renderListener.remove(listener);
	}

	@Override
	public void beforePage(final RenderContext renderContext) {
		for (RenderListener listener : renderListener) {
			listener.beforePage(renderContext);
		}
	}

	@Override
	public void afterPage(final RenderContext renderContext) {
		for (RenderListener listener : renderListener) {
			listener.afterPage(renderContext);
		}
	}

}
