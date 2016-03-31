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
	 */
	public WrappingNewLine(FontDescriptor fontDescriptor) {
		super(fontDescriptor);
	}

	/**
	 * See {@link NewLine#NewLine(float)}.
	 */
	public WrappingNewLine(float fontSize) {
		super(fontSize);
	}

}
