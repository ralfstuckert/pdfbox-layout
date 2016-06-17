package rst.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.text.Constants;

/**
 * Defines the size and orientation of a page.
 */
public class PageFormat implements Element {
    
    public static final PageFormat A0_PORTRAIT = new PageFormat(Constants.A0, Orientation.Portrait);
    public static final PageFormat A0_LANDSCAPE = new PageFormat(Constants.A0, Orientation.Landscape);
    public static final PageFormat A1_PORTRAIT = new PageFormat(Constants.A1, Orientation.Portrait);
    public static final PageFormat A1_LANDSCAPE = new PageFormat(Constants.A1, Orientation.Landscape);
    public static final PageFormat A2_PORTRAIT = new PageFormat(Constants.A2, Orientation.Portrait);
    public static final PageFormat A2_LANDSCAPE = new PageFormat(Constants.A2, Orientation.Landscape);
    public static final PageFormat A3_PORTRAIT = new PageFormat(Constants.A3, Orientation.Portrait);
    public static final PageFormat A3_LANDSCAPE = new PageFormat(Constants.A3, Orientation.Landscape);
    public static final PageFormat A4_PORTRAIT = new PageFormat(Constants.A4, Orientation.Portrait);
    public static final PageFormat A4_LANDSCAPE = new PageFormat(Constants.A4, Orientation.Landscape);
    public static final PageFormat A5_PORTRAIT = new PageFormat(Constants.A5, Orientation.Portrait);
    public static final PageFormat A5_LANDSCAPE = new PageFormat(Constants.A5, Orientation.Landscape);
    public static final PageFormat A6_PORTRAIT = new PageFormat(Constants.A6, Orientation.Portrait);
    public static final PageFormat A6_LANDSCAPE = new PageFormat(Constants.A6, Orientation.Landscape);

    private final PDRectangle mediaBox;
    private final Orientation orientation;

    /**
     * Creates a PageFormat with a given size in orientation portrait.
     * @param mediaBox the size.
     */
    public PageFormat(final PDRectangle mediaBox) {
	this(mediaBox, Orientation.Portrait);
    }

    /**
     * Creates a PageFormat with a given orientation in size A4.
     * @param orientation the orientation to use.
     */
    public PageFormat(final Orientation orientation) {
	this(Constants.A4, orientation);
    }

    /**
     * Creates a PageFormat with a given size and orientation.
     * @param mediaBox the size.
     * @param orientation the orientation.
     */
    public PageFormat(final PDRectangle mediaBox, final Orientation orientation) {
	this.mediaBox = mediaBox;
	this.orientation = orientation;
    }

    public PDRectangle getMediaBox() {
	return mediaBox;
    }

    public Orientation getOrientation() {
	return orientation;
    }

    @Override
    public String toString() {
	return "PageFormat [mediaBox=" + mediaBox + ", orientation="
		+ orientation + "]";
    }
    
}
