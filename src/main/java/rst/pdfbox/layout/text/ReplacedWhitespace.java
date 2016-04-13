package rst.pdfbox.layout.text;

/**
 * Acts as a replacement for whitespace that has been removed by word wrapping.
 */
public class ReplacedWhitespace extends ControlFragment {

    private String replacedSpace;
    
    public ReplacedWhitespace(String replacedSpace, FontDescriptor fontDescriptor) {
	super("", fontDescriptor);

	this.replacedSpace = replacedSpace;
    }

    /**
     * @return the replaced space.
     */
    public String getReplacedSpace() {
	return replacedSpace;
    }
    
    /**
     * @return the replaced fragment.
     */
    public TextFragment toReplacedFragment() {
	return new StyledText(getReplacedSpace(), getFontDescriptor());
    }
}
