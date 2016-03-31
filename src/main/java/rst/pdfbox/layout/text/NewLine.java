package rst.pdfbox.layout.text;

/**
 * Control fragment that represents a new line in text. It has a (font and)
 * height in order to specify the height of an empty line.
 */
public class NewLine extends ControlFragment {

	/**
	 * Creates a new line with the
	 * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}.
	 */
	public NewLine() {
		this(DEFAULT_FONT_DESCRIPTOR);
	}

	/**
	 * Creates a new line with the
	 * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the given
	 * height..
	 */
	public NewLine(final float fontSize) {
		this(new FontDescriptor(DEFAULT_FONT_DESCRIPTOR.getFont(), fontSize));
	}

	/**
	 * Creates a new line with the given font descriptor.
	 * 
	 * @param fontDescriptor
	 */
	public NewLine(final FontDescriptor fontDescriptor) {
		super("NEWLINE", "\n", fontDescriptor);
	}

}
