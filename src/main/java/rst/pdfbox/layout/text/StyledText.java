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
    private Float width = null;
    private final Color color;
    private final float leftMargin;
    private final float rightMargin;

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
	this(text, size, font, Color.black);
    }

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param size
     *            the size of the font.
     * @param font
     *            the font to use.
     * @param color
     *            the color to use.
     */
    public StyledText(final String text, final float size, final PDFont font,
	    final Color color) {
	this(text, new FontDescriptor(font, size), color);
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
	this(text, fontDescriptor, Color.black);
    }

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param fontDescriptor
     *            the font to use.
     * @param color
     *            the color to use.
     */
    public StyledText(final String text, final FontDescriptor fontDescriptor,
	    final Color color) {
	this(text, fontDescriptor, color, 0, 0);
    }

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param fontDescriptor
     *            the font to use.
     * @param color
     *            the color to use.
     * @param leftMargin
     *            the margin left to the text.
     * @param rightMargin
     *            the margin right to the text.
     */
    public StyledText(final String text, final FontDescriptor fontDescriptor,
	    final Color color, final float leftMargin, final float rightMargin) {
	if (text.contains("\n")) {
	    throw new IllegalArgumentException(
		    "StyledText must not contain line breaks, use TextFragment.LINEBREAK for that");
	}
	if (leftMargin < 0) {
	    throw new IllegalArgumentException(
		    "leftMargin must be >= 0");
	}
	if (rightMargin < 0) {
	    throw new IllegalArgumentException(
		    "rightMargin must be >= 0");
	}
	this.text = text;
	this.fontDescriptor = fontDescriptor;
	this.color = color;
	this.leftMargin = leftMargin;
	this.rightMargin = rightMargin;
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
	if (width == null) {
	    width = getFontDescriptor().getSize()
		    * getFontDescriptor().getFont().getStringWidth(getText())
		    / 1000;
	    width += leftMargin;
	    width += rightMargin;
	}
	return width;
    }

    @Override
    public float getHeight() throws IOException {
	return getFontDescriptor().getSize();
    }

    @Override
    public Color getColor() {
	return color;
    }

    public float getLeftMargin() {
	return leftMargin;
    }

    public float getRightMargin() {
	return rightMargin;
    }

    public TextSequence asSequence() {
	TextLine line = new TextLine();
	line.add(this);
	return line;
    }

    @Override
    public String toString() {
	return "StyledText [text=" + text + ", fontDescriptor="
		+ fontDescriptor + ", width=" + width + ", color=" + color
		+ ", leftMargin=" + leftMargin + ", rightMargin=" + rightMargin
		+ "]";
    }

}
