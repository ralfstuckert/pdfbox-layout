package rst.pdfbox.layout.text;

/**
 * Container for all annotations.
 */
public class Annotations {

    /**
     * Represents a hyperlink annotation
     */
    public static class HyperlinkAnnotation implements Annotation {
	private final String hyperlink;

	/**
	 * Creates a hyperlink annotation.
	 * 
	 * @param hyperlink
	 *            the hyperlink.
	 */
	public HyperlinkAnnotation(String hyperlink) {
	    this.hyperlink = hyperlink;
	}

	/**
	 * @return the hyperlink (URL).
	 */
	public String getHyperlink() {
	    return hyperlink;
	}

	@Override
	public String toString() {
	    return "HyperlinkAnnotation [hyperlink=" + hyperlink + "]";
	}

    }

    /**
     * Represents a anchor annotation
     */
    public static class AnchorAnnotation implements Annotation {
	private final String anchor;

	/**
	 * Creates a anchor annotation.
	 * 
	 * @param anchor
	 *            the anchor name.
	 */
	public AnchorAnnotation(String anchor) {
	    this.anchor = anchor;
	}

	/**
	 * @return the anchor name.
	 */
	public String getAnchor() {
	    return anchor;
	}

	@Override
	public String toString() {
	    return "AnchorAnnotation [anchor=" + anchor + "]";
	}

    }
}
