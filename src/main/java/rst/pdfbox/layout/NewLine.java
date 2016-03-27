package rst.pdfbox.layout;

public class NewLine extends ControlFragment {

	public NewLine() {
		this(DEFAULT_FONT_DESCRIPTOR);
	}

	public NewLine(final FontDescriptor fontDescriptor) {
		super("NEWLINE", "\n", fontDescriptor);
	}

	public NewLine(final float fontSize) {
		this(new FontDescriptor(DEFAULT_FONT_DESCRIPTOR.getFont(), fontSize));
	}
	

}
