package rst.pdfbox.layout.elements.render;

import rst.pdfbox.layout.text.Alignment;

/**
 * Layout hint for the {@link VerticalLayout}. You may specify margins to define
 * some extra space around the drawable. If there is still some extra space
 * available vertically, the alignment decides where to position the drawable.
 * The {@link #isResetY() reset Y} indicates if the Y postion should be reset
 * to the value before drawing. Be aware that this only applies to the current page
 * where the remainder of the element has been drawn to. Means, if the elemenent spawns
 * multiple pages, the position is reset to the begin of the last page.
 */
public class VerticalLayoutHint implements LayoutHint {

    public final static VerticalLayoutHint LEFT = new VerticalLayoutHint(
	    Alignment.Left);
    public final static VerticalLayoutHint CENTER = new VerticalLayoutHint(
	    Alignment.Center);
    public final static VerticalLayoutHint RIGHT = new VerticalLayoutHint(
	    Alignment.Right);

    private final Alignment alignment;
    private final float marginLeft;
    private final float marginRight;
    private final float marginTop;
    private final float marginBottom;
    private final boolean resetY;

    public VerticalLayoutHint() {
	this(Alignment.Left);
    }

    public VerticalLayoutHint(Alignment alignment) {
	this(alignment, 0, 0, 0, 0);
    }

    public VerticalLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom) {
	this(alignment, marginLeft, marginRight, marginTop, marginBottom, false);
    }

    public VerticalLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom,
	    boolean resetY) {
	this.alignment = alignment;
	this.marginLeft = marginLeft;
	this.marginRight = marginRight;
	this.marginTop = marginTop;
	this.marginBottom = marginBottom;
	this.resetY = resetY;
    }

    public Alignment getAlignment() {
	return alignment;
    }

    public float getMarginLeft() {
	return marginLeft;
    }

    public float getMarginRight() {
	return marginRight;
    }

    public float getMarginTop() {
	return marginTop;
    }

    public float getMarginBottom() {
	return marginBottom;
    }

    public boolean isResetY() {
	return resetY;
    }

    @Override
    public String toString() {
	return "VerticalLayoutHint [alignment=" + alignment + ", marginLeft="
		+ marginLeft + ", marginRight=" + marginRight + ", marginTop="
		+ marginTop + ", marginBottom=" + marginBottom + ", resetY="
		+ resetY + "]";
    }

}
