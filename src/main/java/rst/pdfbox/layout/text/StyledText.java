/**
 * Copyright (c) 2014-2016 Deutsche Verrechnungsstelle GmbH
 * All rights reserved. The use of this program and the
 * accompanying materials are subject to license terms.
 */

package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * A drawable text styled with font, size, color etc.
 */
public class StyledText implements TextFragment {

    private final String text;
    private final FontDescriptor fontDescriptor;
    private Color color = Color.black;

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param size
     *            the size of the font.
     * @param font
     *            the font to use.
     */
    public StyledText(final String text, final float size, final PDFont font) {
	this(text, new FontDescriptor(font, size));
    }

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param fontDescriptor
     *            the font to use.
     */
    public StyledText(final String text, final FontDescriptor fontDescriptor) {
	if (text.contains("\n")) {
	    throw new IllegalArgumentException(
		    "StyledText must not contain line breaks, use TextFragment.LINEBREAK for that");
	}
	this.text = text;
	this.fontDescriptor = fontDescriptor;
    }

    /**
     * @return the text to draw.
     */
    public String getText() {
	return text;
    }

    /**
     * @return the font to use to draw the text.
     */
    public FontDescriptor getFontDescriptor() {
	return fontDescriptor;
    }

    @Override
    public float getWidth() throws IOException {
	return getFontDescriptor().getSize()
		* getFontDescriptor().getFont().getStringWidth(getText())
		/ 1000;
    }

    @Override
    public float getHeight() throws IOException {
	return getFontDescriptor().getSize();
    }

    @Override
    public Color getColor() {
	return color;
    }

    /**
     * Sets the color to use for drawing the text.
     * 
     * @param color
     */
    public void setColor(Color color) {
	this.color = color;
    }

    public TextSequence asSequence() {
	TextLine line = new TextLine();
	line.add(this);
	return line;
    }

    @Override
    public String toString() {
	return "StyledText [text=" + text + ", fontDescriptor="
		+ fontDescriptor + ", color=" + color + "]";
    }

}
