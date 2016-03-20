package rst.pdfbox.layout;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ControlFragment implements TextFragment {
	
	private final static FontDescriptor DEFAULT_FONT_DESCRIPTOR = new FontDescriptor(PDType1Font.HELVETICA, 11);

	public final static ControlFragment NEWLINE = new ControlFragment("NEWLINE", "\n");
	
	private String name;
	private String text;
	
	public ControlFragment(final String name, final String text) {
		this.name = name;
		this.text = text;
	}

	@Override
	public float getWidth() throws IOException {
		return 0;
	}

	@Override
	public float getHeight() throws IOException {
		return 0;
	}

	@Override
	public FontDescriptor getFontDescriptor() {
		return DEFAULT_FONT_DESCRIPTOR;
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
