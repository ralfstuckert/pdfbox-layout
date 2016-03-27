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
 * Text with font.
 */
public class StyledText implements TextFragment {

	private final String text;
	private final FontDescriptor fontDescriptor;
	private Color color = Color.black;

	public StyledText(final String text, final PDFont font, final float size) {
		this(text, new FontDescriptor(font, size));
	}

	public StyledText(final String text, final FontDescriptor fontDescriptor) {
		if (text.contains("\n")) {
			throw new IllegalArgumentException("StyledText must not contain line breaks, use TextFragment.LINEBREAK for that");
		}
		this.text = text;
		this.fontDescriptor = fontDescriptor;
	}

	public String getText() {
		return text;
	}

	public FontDescriptor getFontDescriptor() {
		return fontDescriptor;
	}

	@Override
	public float getWidth() throws IOException {
		return getFontDescriptor().getSize() * getFontDescriptor().getFont().getStringWidth(getText()) / 1000;
	}

	@Override
	public float getHeight()throws IOException {
		return getFontDescriptor().getSize();
	}

	@Override
	public Color getColor() {
		return color;
	}

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
