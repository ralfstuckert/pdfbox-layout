package rst.pdfbox.layout.elements.render;

import rst.pdfbox.layout.text.Alignment;

/**
 * The column layout hint provides currently the same possibilities as the
 * {@link VerticalLayoutHint}. See there for more details.
 */
public class ColumnLayoutHint extends VerticalLayoutHint {

    public final static ColumnLayoutHint LEFT = new ColumnLayoutHint(
	    Alignment.Left);
    public final static ColumnLayoutHint CENTER = new ColumnLayoutHint(
	    Alignment.Center);
    public final static ColumnLayoutHint RIGHT = new ColumnLayoutHint(
	    Alignment.Right);

    /**
     * Creates a layout hint with {@link Alignment#Left left alignment}.
     */
    public ColumnLayoutHint() {
	super();
    }

    /**
     * Creates a layout hint with the given alignment.
     * 
     * @param alignment
     *            the element alignment.
     */
    public ColumnLayoutHint(Alignment alignment) {
	super(alignment);
    }

    /**
     * Creates a layout hint with the given alignment and margins.
     * 
     * @param alignment
     *            the element alignment.
     * @param marginLeft
     *            the left alignment.
     * @param marginRight
     *            the right alignment.
     * @param marginTop
     *            the top alignment.
     * @param marginBottom
     *            the bottom alignment.
     */
    public ColumnLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom) {
	super(alignment, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * Creates a layout hint with the given alignment and margins.
     * 
     * @param alignment
     *            the element alignment.
     * @param marginLeft
     *            the left alignment.
     * @param marginRight
     *            the right alignment.
     * @param marginTop
     *            the top alignment.
     * @param marginBottom
     *            the bottom alignment.
     * @param resetY
     *            if <code>true</code>, the y coordinate will be reset to the
     *            point before layouting the element.
     */
    public ColumnLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom,
	    boolean resetY) {
	super(alignment, marginLeft, marginRight, marginTop, marginBottom,
		resetY);
    }

}
