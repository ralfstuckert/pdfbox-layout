package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * A control fragment has no drawable representation but is meant to control the
 * text rendering.
 *
 */
public class ControlFragment implements TextFragment {

    protected final static FontDescriptor DEFAULT_FONT_DESCRIPTOR = new FontDescriptor(
	    PDType1Font.HELVETICA, 11);

    private String name;
    private String text;
    private FontDescriptor fontDescriptor;
    private Color color;

    protected ControlFragment(final String text,
	    final FontDescriptor fontDescriptor) {
	this(null, text, fontDescriptor, Color.black);
    }

    protected ControlFragment(final String name, final String text,
	    final FontDescriptor fontDescriptor, final Color color) {
	this.name = name;
	if (this.name == null) {
		this.name = getClass().getSimpleName();
	}
	this.text = text;
	this.fontDescriptor = fontDescriptor;
	this.color = color;
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
    
    protected String getName() {
	return name;
    }

    @Override
    public String getText() {
	return text;
    }

    @Override
    public Color getColor() {
	return color;
    }

    @Override
    public String toString() {
	return "ControlFragment [" + name + "]";
    }

}
