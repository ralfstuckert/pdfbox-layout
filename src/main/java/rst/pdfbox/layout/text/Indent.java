package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Control fragment that represents a indent in text.
 */
public class Indent extends ControlFragment {

    /**
     * Constant for the indention of 0.
     */
    public final static Indent UNINDENT = new Indent(0);

    protected int level = 1;
    protected float indentWidth = 4;
    protected SpaceUnit indentUnit = SpaceUnit.em;
    protected StyledText styledText;

    /**
     * Creates a new line with the given font descriptor.
     * 
     * @param level
     *            the indention level.
     * @param indentWidth
     *            the indention.
     * @param indentUnit
     *            the indention unit.
     * @throws IOException
     */
    public Indent(final int level, final float indentWidth, final SpaceUnit indentUnit)
	    throws IOException {
	this("", level, indentWidth, indentUnit, DEFAULT_FONT_DESCRIPTOR, Color.black);
    }

    /**
     * Creates a new line with the
     * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     * 
     * @param label
     *            the label of the indention.
     * @param level
     *            the indention level.
     * @param indentWidth
     *            the indention.
     * @param indentUnit
     *            the indention unit.
     * @param fontSize
     *            the font size, resp. the height of the new line.
     * @param font
     *            the font to use.
     * @param color
     *            the color to use.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final int level, final float indentWidth, final SpaceUnit indentUnit,
	    final float fontSize, final PDFont font, final Color color) throws IOException {
	this(label, level, indentWidth, indentUnit, new FontDescriptor(font, fontSize), color);
    }

    /**
     * Creates a new line with the given font descriptor.
     * 
     * @param label
     *            the label of the indention.
     * @param level
     *            the indention level.
     * @param indentWidth
     *            the indention width.
     * @param indentUnit
     *            the indention unit.
     * @param fontDescriptor
     *            the font and size associated with this new line.
     * @param color
     *            the color to use.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final int level, final float indentWidth, final SpaceUnit indentUnit,
	    final FontDescriptor fontDescriptor, final Color color) throws IOException {
	super("INDENT", label, fontDescriptor, color);
	
	float indent = calculateIndent(level, indentWidth, indentUnit, fontDescriptor);
	float textWidth = 0;
	if (label != null && !label.isEmpty()) {
	    textWidth = fontDescriptor.getSize() * fontDescriptor.getFont().getStringWidth(label) / 1000f;
	}
	float marginLeft = textWidth < indent ? indent-textWidth : 0;
	styledText = new StyledText(label, getFontDescriptor(), getColor(), marginLeft, 0);
    }

    /**
     * Directly creates an indent of the given width in pt.
     * @param indentPt the indention in pt.
     */
    public Indent(final float indentPt) {
	super("", DEFAULT_FONT_DESCRIPTOR);
	styledText = new StyledText("", getFontDescriptor(), getColor(), indentPt, 0);
    }
    
    private float calculateIndent(final int level, final float indentWidth, final SpaceUnit indentUnit,
	    final FontDescriptor fontDescriptor) throws IOException {
	if (level < 0) {
	    return 0;
	}
	return level * indentUnit.toPt(indentWidth, fontDescriptor);
    }

    @Override
    public float getWidth() throws IOException {
	return styledText.getWidth();
    }

    /**
     * @return a styled text representation of the indent.
     */
    public StyledText toStyledText() {
	return styledText;
    }

    @Override
    public String toString() {
	return "ControlFragment [" + getName() + ", " + styledText + "]";
    }


}
