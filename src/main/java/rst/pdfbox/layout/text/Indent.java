package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Control fragment that represents a indent in text.
 */
public class Indent extends ControlFragment {

    /**
     * Constant for the indentation of 0.
     */
    public final static Indent UNINDENT = new Indent(0);

    protected float indentWidth = 4;
    protected SpaceUnit indentUnit = SpaceUnit.em;
    protected Alignment alignment = Alignment.Left;
    protected StyledText styledText;

    /**
     * Creates a new line with the given font descriptor.
     * 
     * @param indentWidth
     *            the indentation.
     * @param indentUnit
     *            the indentation unit.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final float indentWidth, final SpaceUnit indentUnit)
	    throws IOException {
	this("", indentWidth, indentUnit, DEFAULT_FONT_DESCRIPTOR,
		Alignment.Left, Color.black);
    }

    /**
     * Creates a new line with the
     * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     * 
     * @param label
     *            the label of the indentation.
     * @param indentWidth
     *            the indentation.
     * @param indentUnit
     *            the indentation unit.
     * @param fontSize
     *            the font size, resp. the height of the new line.
     * @param font
     *            the font to use.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final float indentWidth,
	    final SpaceUnit indentUnit, final float fontSize, final PDFont font)
	    throws IOException {

	this(label, indentWidth, indentUnit, fontSize, font, Alignment.Left,
		Color.black);
    }

    /**
     * Creates a new line with the
     * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     * 
     * @param label
     *            the label of the indentation.
     * @param indentWidth
     *            the indentation.
     * @param indentUnit
     *            the indentation unit.
     * @param fontSize
     *            the font size, resp. the height of the new line.
     * @param font
     *            the font to use.
     * @param alignment
     *            the alignment of the label.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final float indentWidth,
	    final SpaceUnit indentUnit, final float fontSize,
	    final PDFont font, final Alignment alignment) throws IOException {
	this(label, indentWidth, indentUnit, fontSize, font, alignment,
		Color.black);
    }

    /**
     * Creates a new line with the
     * {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the given
     * height.
     * 
     * @param label
     *            the label of the indentation.
     * @param indentWidth
     *            the indentation.
     * @param indentUnit
     *            the indentation unit.
     * @param fontSize
     *            the font size, resp. the height of the new line.
     * @param font
     *            the font to use.
     * @param alignment
     *            the alignment of the label.
     * @param color
     *            the color to use.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final float indentWidth,
	    final SpaceUnit indentUnit, final float fontSize,
	    final PDFont font, final Alignment alignment, final Color color)
	    throws IOException {
	this(label, indentWidth, indentUnit,
		new FontDescriptor(font, fontSize), alignment, color);
    }

    /**
     * Creates a new line with the given font descriptor.
     * 
     * @param label
     *            the label of the indentation.
     * @param indentWidth
     *            the indentation width.
     * @param indentUnit
     *            the indentation unit.
     * @param fontDescriptor
     *            the font and size associated with this new line.
     * @param alignment
     *            the alignment of the label.
     * @param color
     *            the color to use.
     * @throws IOException
     *             by pdfbox
     */
    public Indent(final String label, final float indentWidth,
	    final SpaceUnit indentUnit, final FontDescriptor fontDescriptor,
	    final Alignment alignment, final Color color) throws IOException {
	super("INDENT", label, fontDescriptor, color);

	float indent = calculateIndent(indentWidth, indentUnit, fontDescriptor);
	float textWidth = 0;
	if (label != null && !label.isEmpty()) {
	    textWidth = fontDescriptor.getSize()
		    * fontDescriptor.getFont().getStringWidth(label) / 1000f;
	}
	float marginLeft = 0;
	float marginRight = 0;
	if (textWidth < indent) {
	    switch (alignment) {
	    case Left:
		marginRight = indent - textWidth;
		break;
	    case Right:
		marginLeft = indent - textWidth;
		break;
	    default:
		marginLeft = (indent - textWidth) / 2f;
		marginRight = marginLeft;
		break;
	    }
	}
	styledText = new StyledText(label, getFontDescriptor(), getColor(),
		marginLeft, marginRight);
    }

    /**
     * Directly creates an indent of the given width in pt.
     * 
     * @param indentPt
     *            the indentation in pt.
     */
    public Indent(final float indentPt) {
	super("", DEFAULT_FONT_DESCRIPTOR);
	styledText = new StyledText("", getFontDescriptor(), getColor(),
		indentPt, 0);
    }

    private float calculateIndent(final float indentWidth,
	    final SpaceUnit indentUnit, final FontDescriptor fontDescriptor)
	    throws IOException {
	if (indentWidth < 0) {
	    return 0;
	}
	return indentUnit.toPt(indentWidth, fontDescriptor);
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
