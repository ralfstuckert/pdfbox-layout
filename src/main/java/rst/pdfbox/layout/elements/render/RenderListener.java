package rst.pdfbox.layout.elements.render;

/**
 * A render listener is called before and after a page has been rendered. It may
 * be used, to perform some custom operations (drawings) to the page.
 *
 */
public interface RenderListener {

	void beforePage(final RenderContext renderContext);

	void afterPage(final RenderContext renderContext);
}
