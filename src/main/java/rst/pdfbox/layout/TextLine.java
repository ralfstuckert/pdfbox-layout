/**
 * Copyright (c) 2014-2016 Deutsche Verrechnungsstelle GmbH
 * All rights reserved. The use of this program and the
 * accompanying materials are subject to license terms.
 */

package rst.pdfbox.layout;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;

public class TextLine implements TextSequence {

	private final List<StyledText> fragments = new ArrayList<StyledText>();

	TextLine() {
		super();
	}

	public void add(final StyledText fragment) {
		fragments.add(fragment);
	}

	public void add(final TextLine textLine) {
		for (StyledText fragment : textLine.getStyledTexts()) {
			add(fragment);
		}
	}

	public List<StyledText> getStyledTexts() {
		return Collections.unmodifiableList(fragments);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<TextFragment> iterator() {
		return (Iterator) getStyledTexts().iterator();
	}

	public boolean isEmpty() {
		return fragments.isEmpty();
	}

	@Override
	public float getWidth() throws IOException {
		float sum = 0;
		for (TextFragment fragment : fragments) {
			sum += fragment.getWidth();
		}
		return sum;
	}

	@Override
	public float getHeight() throws IOException {
		float max = 0;
		for (TextFragment fragment : fragments) {
			max = Math.max(max, fragment.getHeight());
		}
		return max;
	}

	public float getLineHeightWithSpacing(float lineSpacing) throws IOException {
		float width = getWidth();
		float max = 0;
		float weightedHeight = 0;
		for (TextFragment fragment : fragments) {
			max = Math.max(max, fragment.getHeight());
			if (width > 0) {
				weightedHeight += (fragment.getWidth() / width)
						* fragment.getHeight();
			}
		}
		if (weightedHeight > 0) {
			return Math.max(max, weightedHeight * lineSpacing);
		}
		return max * lineSpacing;
	}

	@Override
	public void drawText(PDPageContentStream contentStream, Coords beginOfText,
			Alignment alignment) throws IOException {
		contentStream.saveGraphicsState();
		contentStream.beginText();
		contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, beginOfText.getX(),
				beginOfText.getY()));
		FontDescriptor lastFontDesc = null;
		Color lastColor = null;
		for (StyledText styledText : fragments) {
			if (!styledText.getFontDescriptor().equals(lastFontDesc)) {
				lastFontDesc = styledText.getFontDescriptor();
				contentStream.setFont(lastFontDesc.getFont(),
						lastFontDesc.getSize());
			}
			if (!styledText.getColor().equals(lastColor)) {
				lastColor = styledText.getColor();
				contentStream.setNonStrokingColor(lastColor);
			}
			contentStream.showText(styledText.getText());
		}
		contentStream.endText();
		contentStream.restoreGraphicsState();
	}

	@Override
	public String toString() {
		return "TextLine [line=" + fragments + "]";
	}

}
