package rst.pdfbox.layout.text.annotations;

/**
 * Container for all annotations.
 */
public class Annotations {

    /**
     * Represents a underline annotation
     */
    public static class UnderlineAnnotation implements Annotation {
	@Override
	public String toString() {
	    return "UnderlineAnnotation";
	}
    }
    
    
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
	
	private final String hyperlinkUri;
	private final LinkStyle linkStyle;

	/**
	 * Creates a hyperlink annotation.
	 * 
	 * @param hyperlinkUri
	 *            the hyperlinkUri.
	 * @param linkStyle
	 *            the link style.
	 */
	public HyperlinkAnnotation(String hyperlinkUri, LinkStyle linkStyle) {
	    this.hyperlinkUri = hyperlinkUri;
	    this.linkStyle = linkStyle;
	}

	/**
	 * @return the hyperlink URI.
	 */
	public String getHyperlinkURI() {
	    return hyperlinkUri;
	}

	public LinkStyle getLinkStyle() {
	    return linkStyle;
	}

	@Override
	public String toString() {
	    return "HyperlinkAnnotation [hyperlinkUri=" + hyperlinkUri
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
