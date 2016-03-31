/**
 * Copyright (c) 2014-2016 Deutsche Verrechnungsstelle GmbH
 * All rights reserved. The use of this program and the
 * accompanying materials are subject to license terms.
 */

package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;

/**
 * A text of line containing only {@link StyledText}s. It may be terminated by a {@link #getNewLine() new line}.
 */
public class TextLine implements TextSequence {

	private final List<StyledText> styledTextList = new ArrayList<StyledText>();
	private NewLine newLine;

	/**
	 * Adds a styled text.
	 * @param fragment
	 */
	public void add(final StyledText fragment) {
		styledTextList.add(fragment);
	}

	/**
	 * Adds all styled texts of the given text line.
	 * @param textLine
	 */
	public void add(final TextLine textLine) {
		for (StyledText fragment : textLine.getStyledTexts()) {
			add(fragment);
		}
	}

	/** 
	 * @return the terminating new line, may be <code>null</code>.
	 */
	public NewLine getNewLine() {
		return newLine;
	}

	/**
	 * Sets the new line.
	 * @param newLine
	 */
	public void setNewLine(NewLine newLine) {
		this.newLine = newLine;
	}

	/**
	 * @return the styled texts building up this line.
	 */
	public List<StyledText> getStyledTexts() {
		return Collections.unmodifiableList(styledTextList);
	}

	@Override
	public Iterator<TextFragment> iterator() {
		return new TextLineIterator(styledTextList.iterator(), newLine);
	}

	/**
	 * @return <code>true</code> if the line contains neither styled text nor a new line.
	 */
	public boolean isEmpty() {
		return styledTextList.isEmpty() && newLine == null;
	}

	@Override
	public float getWidth() throws IOException {
		float sum = 0;
		for (TextFragment fragment : this) {
			sum += fragment.getWidth();
		}
		return sum;
	}

	@Override
	public float getHeight() throws IOException {
		float max = 0;
		for (TextFragment fragment : this) {
			max = Math.max(max, fragment.getHeight());
		}
		return max;
	}

	/**
	 * @return the (max) ascent of this line.
	 * @throws IOException
	 */
	protected float getAscent() throws IOException {
		float max = 0;
		for (TextFragment fragment : this) {
			float ascent = fragment.getFontDescriptor().getSize()
					* fragment.getFontDescriptor().getFont()
							.getFontDescriptor().getAscent() / 1000;
			max = Math.max(max, ascent);
		}
		return max;
	}

	/**
	 * @Deprecated
	 */
	public float getLineHeightWithSpacing(float lineSpacing) throws IOException {
		float width = getWidth();
		float max = 0;
		float weightedHeight = 0;
		for (TextFragment fragment : this) {
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
	public void drawText(PDPageContentStream contentStream,
			Position originUpperLeft, Alignment alignment) throws IOException {
		contentStream.saveGraphicsState();
		contentStream.beginText();
		contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, originUpperLeft
				.getX(), originUpperLeft.getY() - getAscent()));
		FontDescriptor lastFontDesc = null;
		Color lastColor = null;
		for (StyledText styledText : styledTextList) {
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
		return "TextLine [styledText=" + styledTextList + ", newLine=" + newLine
				+ "]";
	}


	private static class TextLineIterator implements Iterator<TextFragment> {

		private Iterator<StyledText> styledText;
		private NewLine newLine;
		
		public TextLineIterator(Iterator<StyledText> styledText, NewLine newLine) {
			super();
			this.styledText = styledText;
			this.newLine = newLine;
		}

		@Override
		public boolean hasNext() {
			return styledText.hasNext() || newLine != null;
		}

		@Override
		public TextFragment next() {
			TextFragment next = null;
			if (styledText.hasNext()) {
				next = styledText.next();
			} else if (newLine != null) {
				next = newLine;
				newLine = null;
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	

}
