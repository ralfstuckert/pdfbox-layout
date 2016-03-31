package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * A control fragment has no drawable representation but is meant to control the text rendering.
 *
 */
public class ControlFragment implements TextFragment {

	protected final static FontDescriptor DEFAULT_FONT_DESCRIPTOR = new FontDescriptor(
			PDType1Font.HELVETICA, 11);

	private String name;
	private String text;
	private FontDescriptor fontDescriptor;

	protected ControlFragment(final String name, final String text,
			final FontDescriptor fontDescriptor) {
		this.name = name;
		this.text = text;
		this.fontDescriptor = fontDescriptor;
	}

	@Override
	public float getWidth() throws IOException {
		return 0;
	}

	@Override
	public float getHeight() throws IOException {
		return getFontDescriptor() == null ? 0 : getFontDescriptor().getSize();
	}

	@Override
	public FontDescriptor getFontDescriptor() {
		return fontDescriptor;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Color getColor() {
		return Color.black;
	}

	@Override
	public String toString() {
		return "ControlFragment [" + name + "]";
	}

}
