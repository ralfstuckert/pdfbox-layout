package rst.pdfbox.layout.elements;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.text.Constants;

/**
 * Defines the size and orientation of a page.
 */
public class PageFormat implements Element {

    protected static final String MARGIN_BOTTOM = "marginBottom";
    protected static final String MARGIN_TOP = "marginTop";
    protected static final String MARGIN_RIGHT = "marginRight";
    protected static final String MARGIN_LEFT = "marginLeft";
    protected static final String ORIENTATION = "orientation";
    protected static final String MEDIA_BOX = "mediaBox";

    private PageFormat defaults;

    private Map<String, Object> properties = new HashMap<String, Object>();

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
	this(mediaBox, orientation, null, null, null, null);
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
	    Float marginLeft, Float marginRight, Float marginTop,
	    Float marginBottom) {
	setValue(MEDIA_BOX, mediaBox);
	setValue(ORIENTATION, orientation);
	setValue(MARGIN_LEFT, marginLeft);
	setValue(MARGIN_RIGHT, marginRight);
	setValue(MARGIN_TOP, marginTop);
	setValue(MARGIN_BOTTOM, marginBottom);
    }

    protected PageFormat(final Map<String, Object> properties) {
	this(properties, null);
    }

    protected PageFormat(final Map<String, Object> properties,
	    final PageFormat defaults) {
	this.properties = properties;
	this.defaults = defaults;
    }

    protected <T> T getValue(final String property, final Class<T> type) {
	@SuppressWarnings("unchecked")
	T value = (T) properties.get(property);
	if (value == null && defaults != null) {
	    value = defaults.getValue(property, type);
	}
	return value;
    }

    protected void setValue(final String property, final Object value) {
	properties.put(property, value);
    }

    /**
     * @return the media box to use.
     */
    public PDRectangle getMediaBox() {
	return getValue(MEDIA_BOX, PDRectangle.class);
    }

    /**
     * @return the orientation to use.
     */
    public Orientation getOrientation() {
	return getValue(ORIENTATION, Orientation.class);
    }

    /**
     * @return the left document margin.
     */
    public Float getMarginLeft() {
	return getValue(MARGIN_LEFT, Float.class);
    }

    /**
     * @return the right document margin.
     */
    public Float getMarginRight() {
	return getValue(MARGIN_RIGHT, Float.class);
    }

    /**
     * @return the top document margin.
     */
    public Float getMarginTop() {
	return getValue(MARGIN_TOP, Float.class);
    }

    /**
     * @return the bottom document margin.
     */
    public Float getMarginBottom() {
	return getValue(MARGIN_BOTTOM, Float.class);
    }

    @Override
    protected PageFormat clone() {
	return new PageFormat(this.properties);
    }

    /**
     * Returns a copy of this PageFormat that uses the given PageFormat as
     * defaults. Means if a property is not found in this one, it will be looked
     * up in the other and so on.
     * 
     * @param defaults
     *            the defaults to use.
     * @return a new PageFormat.
     */
    public PageFormat useDefaults(final PageFormat defaults) {
	return new PageFormat(this.properties, defaults);
    }

    /**
     * @return a page format builder.
     */
    public static PageFormatBuilder builder() {
	return new PageFormatBuilder();
    }


    public static class PageFormatBuilder {
	private Float marginLeft;
	private Float marginRight;
	private Float marginTop;
	private Float marginBottom;
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

	public PageFormatBuilder orientation(Orientation orientation) {
	    this.orientation = orientation;
	    return this;
	}

    }

}
