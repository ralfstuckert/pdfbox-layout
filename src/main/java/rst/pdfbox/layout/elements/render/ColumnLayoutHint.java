package rst.pdfbox.layout.elements.render;

import rst.pdfbox.layout.text.Alignment;

public class ColumnLayoutHint extends VerticalLayoutHint {

    public final static ColumnLayoutHint LEFT = new ColumnLayoutHint(
	    Alignment.Left);
    public final static ColumnLayoutHint CENTER = new ColumnLayoutHint(
	    Alignment.Center);
    public final static ColumnLayoutHint RIGHT = new ColumnLayoutHint(
	    Alignment.Right);

    public ColumnLayoutHint() {
	super();
    }

    public ColumnLayoutHint(Alignment alignment) {
	super(alignment);
    }

    public ColumnLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom) {
	super(alignment, marginLeft, marginRight, marginTop, marginBottom);
    }

    public ColumnLayoutHint(Alignment alignment, float marginLeft,
	    float marginRight, float marginTop, float marginBottom,
	    boolean resetY) {
	super(alignment, marginLeft, marginRight, marginTop, marginBottom,
		resetY);
	// TODO Auto-generated constructor stub
    }

}
