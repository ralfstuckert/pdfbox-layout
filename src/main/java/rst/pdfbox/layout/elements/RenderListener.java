package rst.pdfbox.layout.elements;


public interface RenderListener {

	void beforePage(final RenderContext renderContext);

	void afterPage(final RenderContext renderContext);
}
