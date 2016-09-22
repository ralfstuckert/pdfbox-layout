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
    private final int rotation;

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
	this(mediaBox, orientation, 0, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * Creates a Document based on the given media box and margins. By default,
     * a {@link VerticalLayout} is used.
     * 
     * @param mediaBox
     *            the media box to use.
     * @param orientation
     *            the orientation to use.
     * @param rotation
     *            the rotation to apply to the page after rendering.
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
	    int rotation, float marginLeft, float marginRight,
	    float marginTop, float marginBottom) {
	this.mediaBox = mediaBox;
	this.orientation = orientation;
	this.rotation = rotation;
	this.marginLeft = marginLeft;
	this.marginRight = marginRight;
	this.marginTop = marginTop;
	this.marginBottom = marginBottom;
    }

    /**
     * @return the orientation to use.
     */
    public Orientation getOrientation() {
	if (orientation != null) {
	    return orientation;
	}
	if (getMediaBox().getWidth() > getMediaBox().getHeight()) {
	    return Orientation.Landscape;
	}
	return Orientation.Portrait;
    }

    /**
     * @return the rotation to apply to the page after rendering.
     */
    public int getRotation() {
        return rotation;
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
	private Orientation orientation;
	private int rotation;

	protected PageFormatBuilder() {
	}

	/**
	 * Actually builds the PageFormat.
	 * 
	 * @return the resulting PageFormat.
	 */
	public PageFormat build() {
	    return new PageFormat(mediaBox, orientation, rotation, marginLeft,
		    marginRight, marginTop, marginBottom);
	}

	/**
	 * Sets the left margin.
	 * 
	 * @param marginLeft
	 *            the left margin to use.
	 * @return the builder.
	 */
	public PageFormatBuilder marginLeft(float marginLeft) {
	    this.marginLeft = marginLeft;
	    return this;
	}

	/**
	 * Sets the right margin.
	 * 
	 * @param marginRight
	 *            the right margin to use.
	 * @return the builder.
	 */
	public PageFormatBuilder marginRight(float marginRight) {
	    this.marginRight = marginRight;
	    return this;
	}

	/**
	 * Sets the top margin.
	 * 
	 * @param marginTop
	 *            the top margin to use.
	 * @return the builder.
	 */
	public PageFormatBuilder marginTop(float marginTop) {
	    this.marginTop = marginTop;
	    return this;
	}

	/**
	 * Sets the bottom margin.
	 * 
	 * @param marginBottom
	 *            the bottom margin to use.
	 * @return the builder.
	 */
	public PageFormatBuilder marginBottom(float marginBottom) {
	    this.marginBottom = marginBottom;
	    return this;
	}

	/**
	 * Sets the margins.
	 * 
	 * @param marginLeft
	 *            the left margin to use.
	 * @param marginRight
	 *            the right margin to use.
	 * @param marginTop
	 *            the top margin to use.
	 * @param marginBottom
	 *            the bottom margin to use.
	 * @return the builder.
	 */
	public PageFormatBuilder margins(float marginLeft, float marginRight,
		float marginTop, float marginBottom) {
	    this.marginLeft = marginLeft;
	    this.marginRight = marginRight;
	    this.marginTop = marginTop;
	    this.marginBottom = marginBottom;
	    return this;
	}

	/**
	 * Sets the media box to the given size.
	 * 
	 * @param mediaBox
	 *            the media box to use.
	 * @return the builder.
	 */
	public PageFormatBuilder mediaBox(PDRectangle mediaBox) {
	    this.mediaBox = mediaBox;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A0}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A0() {
	    this.mediaBox = Constants.A0;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A1}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A1() {
	    this.mediaBox = Constants.A1;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A2}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A2() {
	    this.mediaBox = Constants.A2;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A3}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A3() {
	    this.mediaBox = Constants.A3;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A4}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A4() {
	    this.mediaBox = Constants.A4;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A5}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A5() {
	    this.mediaBox = Constants.A5;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#A6}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder A6() {
	    this.mediaBox = Constants.A6;
	    return this;
	}

	/**
	 * Sets the media box to size {@link Constants#Letter}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder letter() {
	    this.mediaBox = Constants.Letter;
	    return this;
	}

	/**
	 * Sets the orientation to the given one.
	 * 
	 * @param orientation
	 *            the orientation to use.
	 * @return the builder.
	 */
	public PageFormatBuilder orientation(Orientation orientation) {
	    this.orientation = orientation;
	    return this;
	}

	/**
	 * Sets the orientation to {@link Orientation#Portrait}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder portrait() {
	    this.orientation = Orientation.Portrait;
	    return this;
	}

	/**
	 * Sets the orientation to {@link Orientation#Landscape}.
	 * 
	 * @return the builder.
	 */
	public PageFormatBuilder landscape() {
	    this.orientation = Orientation.Landscape;
	    return this;
	}

	/**
	 * Sets the rotation to apply to the page after rendering.
	 * @param angle the angle to rotate.
	 * @return the builder.
	 */
	public PageFormatBuilder rotation(int angle) {
	    this.rotation = angle;
	    return this;
	}
    }

}
