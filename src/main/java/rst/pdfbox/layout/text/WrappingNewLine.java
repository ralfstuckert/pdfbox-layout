package rst.pdfbox.layout.text;


/**
 * A NewLine introduced by wrapping. This interface is useful for detecting
 * new-lines not contained in the original text.
 */
public class WrappingNewLine extends NewLine {

    /**
     * See {@link NewLine#NewLine()}.
     */
    public WrappingNewLine() {
	super();
    }

    /**
     * See {@link NewLine#NewLine(FontDescriptor)}.
     * @param fontDescriptor the font and size associated with this new line.
     */
    public WrappingNewLine(FontDescriptor fontDescriptor) {
	super(fontDescriptor);
    }

    /**
     * See {@link NewLine#NewLine(float)}.
     * @param fontSize the font size, resp. the height of the new line.
     */
    public WrappingNewLine(float fontSize) {
	super(fontSize);
    }


}
