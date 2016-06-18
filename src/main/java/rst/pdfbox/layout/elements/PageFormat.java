package rst.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.text.Constants;

/**
 * Defines the size and orientation of a page. The default is A4 portrait
 * without margins.
 */
public class PageFormat implements Element {

    private final float marginLeft;
    private final float marginRight;
    private final float marginTop;
    private final float marginBottom;
    private final PDRectangle mediaBox;
    private final Orientation orientation;

    /**
     * Creates a PageFormat with A4 portrait without margins.
     */
    public PageFormat() {
	this(Constants.A4);
    }

    /**
     * Creates a PageFormat with a given size and orientation portrait.
     * 
     * @param mediaBox
     *            the size.
     */
    public PageFormat(final PDRectangle mediaBox) {
	this(mediaBox, Orientation.Portrait);
    }

    /**
     * Creates a PageFormat with a given size and orientation.
     * 
     * @param mediaBox
     *            the size.
     * @param orientation
     *            the orientation.
     */
    public PageFormat(final PDRectangle mediaBox, final Orientation orientation) {
	this(mediaBox, orientation, 0, 0, 0, 0);
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
    public PageFormat(PDRectangle mediaBox, Orientation orientation,
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
     * @return the orientation to use.
     */
    public Orientation getOrientation() {
	return orientation;
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
     * @return a page format builder. The default of the builder is A4 portrait
     *         without margins.
     */
    public static PageFormatBuilder with() {
	return new PageFormatBuilder();
    }

    public static class PageFormatBuilder {
	private float marginLeft;
	private float marginRight;
	private float marginTop;
	private float marginBottom;
	private PDRectangle mediaBox = Constants.A4;
	private Orientation orientation = Orientation.Portrait;

	protected PageFormatBuilder() {
	}

	public PageFormat build() {
	    return new PageFormat(mediaBox, orientation, marginLeft,
		    marginRight, marginTop, marginBottom);
	}

	public PageFormatBuilder marginLeft(float marginLeft) {
	    this.marginLeft = marginLeft;
	    return this;
	}

	public PageFormatBuilder marginRight(float marginRight) {
	    this.marginRight = marginRight;
	    return this;
	}

	public PageFormatBuilder marginTop(float marginTop) {
	    this.marginTop = marginTop;
	    return this;
	}

	public PageFormatBuilder marginBottom(float marginBottom) {
	    this.marginBottom = marginBottom;
	    return this;
	}

	public PageFormatBuilder margins(float marginLeft, float marginRight,
		float marginTop, float marginBottom) {
	    this.marginLeft = marginLeft;
	    this.marginRight = marginRight;
	    this.marginTop = marginTop;
	    this.marginBottom = marginBottom;
	    return this;
	}

	public PageFormatBuilder mediaBox(PDRectangle mediaBox) {
	    this.mediaBox = mediaBox;
	    return this;
	}

	public PageFormatBuilder A0() {
	    this.mediaBox = Constants.A0;
	    return this;
	}

	public PageFormatBuilder A1() {
	    this.mediaBox = Constants.A1;
	    return this;
	}

	public PageFormatBuilder A2() {
	    this.mediaBox = Constants.A2;
	    return this;
	}

	public PageFormatBuilder A3() {
	    this.mediaBox = Constants.A3;
	    return this;
	}

	public PageFormatBuilder A4() {
	    this.mediaBox = Constants.A4;
	    return this;
	}

	public PageFormatBuilder A5() {
	    this.mediaBox = Constants.A5;
	    return this;
	}

	public PageFormatBuilder A6() {
	    this.mediaBox = Constants.A6;
	    return this;
	}

	public PageFormatBuilder orientation(Orientation orientation) {
	    this.orientation = orientation;
	    return this;
	}

	public PageFormatBuilder portrait() {
	    this.orientation = Orientation.Portrait;
	    return this;
	}

	public PageFormatBuilder landscape() {
	    this.orientation = Orientation.Landscape;
	    return this;
	}

    }

}
