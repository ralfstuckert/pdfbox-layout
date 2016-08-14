package rst.pdfbox.layout.text.annotations;

/**
 * Container for all annotations.
 */
public class Annotations {

    /**
     * Represents a hyperlink annotation
     */
    public static class HyperlinkAnnotation implements Annotation {
	
	public static enum LinkStyle {
	    /**
	     * Underline.
	     */
	    ul,
	    /**
	     * None.
	     */
	    none;
	}
	
	private final String hyperlink;
	private final LinkStyle linkStyle;

	/**
	 * Creates a hyperlink annotation.
	 * 
	 * @param hyperlink
	 *            the hyperlink.
	 * @param linkStyle
	 *            the link style.
	 */
	public HyperlinkAnnotation(String hyperlink, LinkStyle linkStyle) {
	    this.hyperlink = hyperlink;
	    this.linkStyle = linkStyle;
	}

	/**
	 * @return the hyperlink (URL).
	 */
	public String getHyperlink() {
	    return hyperlink;
	}

	public LinkStyle getLinkStyle() {
	    return linkStyle;
	}

	@Override
	public String toString() {
	    return "HyperlinkAnnotation [hyperlink=" + hyperlink
		    + ", linkStyle=" + linkStyle + "]";
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
