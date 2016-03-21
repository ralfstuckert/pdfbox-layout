/**
 * Copyright (c) 2014-2016 Deutsche Verrechnungsstelle GmbH
 * All rights reserved. The use of this program and the
 * accompanying materials are subject to license terms.
 */

package rst.pdfbox.layout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * 
 */
public class PdfUtil {

	public static TextFlow createTextFlow(final String text,
			final float fontSize, final BaseFont baseFont) {
		return createTextFlow(text, fontSize, baseFont.getPlainFont(),
				baseFont.getBoldFont(), baseFont.getItalicFont(),
				baseFont.getBoldItalicFont());
	}

	public static TextFlow createTextFlow(final String text,
			final float fontSize, final PDFont plainFont,
			final PDFont boldFont, final PDFont italicFont,
			final PDFont boldItalicFont) {
		final Iterable<CharSequence> parts = fromPlainText(text);
		return createTextFlow(parts, fontSize, plainFont, boldFont, italicFont,
				boldItalicFont);
	}

	public static TextFlow createTextFlowFromMarkup(final String markup,
			final float fontSize, final BaseFont baseFont) {
		return createTextFlowFromMarkup(markup, fontSize,
				baseFont.getPlainFont(), baseFont.getBoldFont(),
				baseFont.getItalicFont(), baseFont.getBoldItalicFont());
	}

	public static TextFlow createTextFlowFromMarkup(final String markup,
			final float fontSize, final PDFont plainFont,
			final PDFont boldFont, final PDFont italicFont,
			final PDFont boldItalicFont) {
		final Iterable<CharSequence> parts = fromMarkup(markup);
		return createTextFlow(parts, fontSize, plainFont, boldFont, italicFont,
				boldItalicFont);
	}

	protected static TextFlow createTextFlow(
			final Iterable<CharSequence> parts, final float fontSize,
			final PDFont plainFont, final PDFont boldFont,
			final PDFont italicFont, final PDFont boldItalicFont) {
		final TextFlow result = new TextFlow();
		boolean bold = false;
		boolean italic = false;
		for (final CharSequence fragment : parts) {
			if (fragment instanceof ControlCharacter) {
				if (fragment == ControlCharacter.NEWLINE) {
					result.add(ControlFragment.NEWLINE);
				}
				if (fragment == ControlCharacter.BOLD) {
					bold = !bold;
				}
				if (fragment == ControlCharacter.ITALIC) {
					italic = !italic;
				}
			} else {
				PDFont font = getFont(bold, italic, plainFont, boldFont,
						italicFont, boldItalicFont);
				StyledText styledText = new StyledText(fragment.toString(),
						font, fontSize);
				result.add(styledText);
			}
		}
		return result;
	}

	protected static PDFont getFont(boolean bold, boolean italic,
			final PDFont plainFont, final PDFont boldFont,
			final PDFont italicFont, final PDFont boldItalicFont) {
		PDFont font = plainFont;
		if (bold && !italic) {
			font = boldFont;
		} else if (!bold && italic) {
			font = italicFont;
		} else if (bold && italic) {
			font = boldItalicFont;
		}
		return font;
	}

	public static List<TextLine> getLinesWithoutLineBreak(
			final TextSequence text) throws IOException {
		final List<TextLine> result = new ArrayList<TextLine>();

		TextLine line = new TextLine();
		for (TextFragment fragment : text) {
			if (fragment == ControlFragment.NEWLINE) {
				result.add(line);
				line = new TextLine();
			} else {
				line.add((StyledText) fragment);
			}
		}
		if (!line.isEmpty()) {
			result.add(line);
		}
		return result;
	}

	public static TextFlow wordWrap(final TextSequence text,
			final float maxWidth) throws IOException {

		TextFlow result = new TextFlow();
		float lineLength = 0;
		for (TextFragment fragment : text) {
			if (fragment == ControlFragment.NEWLINE) {
				result.add(fragment);
				lineLength = 0;
			} else {
				TextFlow words = splitWords(fragment);
				for (TextFragment word : words) {

					FontDescriptor fontDescriptor = word.getFontDescriptor();
					float length = fontDescriptor.getSize()
							* fontDescriptor.getFont().getStringWidth(
									word.getText()) / 1000;

					float extraSpace = 0;
					if (lineLength > 0 && word.getText().length() > 0) {
						// not the first word in line, so separate by blank
						extraSpace = fontDescriptor.getSize()
								* fontDescriptor.getFont().getSpaceWidth()
								/ 1000;
					}
					if (maxWidth > 0 && lineLength > 0
							&& lineLength + length + extraSpace > maxWidth) {
						// word exceeds max width, so create new line
						result.add(ControlFragment.NEWLINE);
						lineLength = 0;
					}
					if (lineLength > 0 && word.getText().length() > 0) {
						// not the first word in line, so separate by blank
						word = new StyledText(" " + word.getText(),
								word.getFontDescriptor());
					}
					result.add(word);
					lineLength += length + extraSpace;
				}
			}
		}

		return result;
	}

	public static List<TextLine> wordWrapToLines(final TextSequence text,
			final float maxWidth) throws IOException {
		TextFlow wrapped = PdfUtil.wordWrap(text, maxWidth);
		List<TextLine> lines = PdfUtil.getLinesWithoutLineBreak(wrapped);
		return lines;
	}

	public static TextFlow splitWords(final TextFragment text) {
		TextFlow result = new TextFlow();
		if (text == ControlFragment.NEWLINE) {
			result.add(ControlFragment.NEWLINE);
		} else {
			String[] words = text.getText().split(" ");
			for (String word : words) {
				result.add(new StyledText(word, text.getFontDescriptor()));
			}
		}
		return result;
	}

	public static void drawText(TextSequence text,
			PDPageContentStream contentStream, Coords originUpperLeft,
			Alignment alignment, float maxWidth, final float lineSpacing)
			throws IOException {
		List<TextLine> lines = wordWrapToLines(text, maxWidth);
		float targetWidth = getMaxWidth(lines);
		Coords coords = originUpperLeft;
		for (int i = 0; i < lines.size(); i++) {
			TextLine textLine = lines.get(i);
			float offset = getOffset(textLine, targetWidth, alignment);
			coords = new Coords(originUpperLeft.getX() + offset, coords.getY());
			textLine.drawText(contentStream, coords, alignment);

			if (i < lines.size() - 1) {
				float nextLineHeight = lines.get(i + 1).getLineHeightWithSpacing(
						lineSpacing);
				coords = coords.add(0, -nextLineHeight);
			}
		}
	}

	public static float getOffset(final TextLine textLine,
			final float targetWidth, final Alignment alignment)
			throws IOException {
		switch (alignment) {
		case Right:
			return targetWidth - textLine.getWidth();
		case Center:
			return (targetWidth - textLine.getWidth()) / 2f;
		default:
			return 0;
		}
	}

	public static float getMaxWidth(final Iterable<TextLine> lines)
			throws IOException {
		float max = 0;
		for (TextLine line : lines) {
			max = Math.max(max, line.getWidth());
		}
		return max;
	}

	public static Iterable<CharSequence> fromPlainText(final CharSequence text) {
		return fromPlainText(Collections.singleton(text));
	}

	public static Iterable<CharSequence> fromPlainText(
			final Iterable<CharSequence> text) {
		return splitByControlCharacter(ControlCharacter.NEWLINE, text);
	}

	public static Iterable<CharSequence> fromMarkup(final CharSequence markup) {
		return fromMarkup(Collections.singleton(markup));
	}

	public static Iterable<CharSequence> fromMarkup(
			final Iterable<CharSequence> markup) {
		Iterable<CharSequence> text = markup;
		text = splitByControlCharacter(ControlCharacter.BOLD, text);
		text = splitByControlCharacter(ControlCharacter.ITALIC, text);
		text = splitByControlCharacter(ControlCharacter.NEWLINE, text);
		return text;
	}

	protected static Iterable<CharSequence> lineBreak(
			final Iterable<CharSequence> text) {
		return splitByControlCharacter(ControlCharacter.NEWLINE, text);
	}

	protected static Iterable<CharSequence> splitByControlCharacter(
			ControlCharacter ctrl, final Iterable<CharSequence> markup) {
		List<CharSequence> result = new ArrayList<CharSequence>();
		for (CharSequence current : markup) {
			if (current instanceof String) {
				String string = (String) current;
				String[] parts = string.split(ctrl.getRegex(), -1);
				for (int i = 0; i < parts.length; i++) {
					if (i > 0) {
						result.add(ctrl);
					}
					String unescaped = ctrl.unescape(parts[i]);
					result.add(unescaped);
				}
			} else {
				result.add(current);
			}
		}
		return result;
	}

	public static float getWidth(final TextSequence textSequence,
			final float preferredMaxWidth) throws IOException {
		List<TextLine> lines = PdfUtil.wordWrapToLines(textSequence,
				preferredMaxWidth);
		float max = 0;
		for (TextLine line : lines) {
			max = Math.max(max, line.getWidth());
		}
		return max;
	}

	public static float getHeight(final TextSequence textSequence,
			final float preferredMaxWidth, final float lineSpacing)
			throws IOException {
		List<TextLine> lines = PdfUtil.wordWrapToLines(textSequence,
				preferredMaxWidth);
		float sum = 0;
		for (int i = 0; i < lines.size(); i++) {
			TextLine line = lines.get(i);
			float lineHeight = line.getHeight();
			if (i > 0) {
				lineHeight = line.getLineHeightWithSpacing(lineSpacing);
			}
			sum += lineHeight;
		}
		return sum;
	}

	public static void main(final String[] args) throws Exception {
		String text = "The MIT License (MIT)\n\nCopyright (c) 2016 Ralf Stuckert\n\n"
				+ "Permission is hereby granted, free of charge, to any person obtaining a "
				+ "copy of this software and associated documentation files (the _Software_), "
				+ "to deal in the Software without restriction, including without limitation "
				+ "the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or "
				+ "sell copies of the Software, and to permit persons to whom the Software is "
				+ "furnished to do so, subject to the following conditions:"
				+ "\n\n"
				+ "The above copyright notice and this permission notice shall be included "
				+ "in all copies or substantial portions of the Software."
				+ "\n\n"
				+ "*THE SOFTWARE IS PROVIDED _AS IS_, WITHOUT WARRANTY OF ANY KIND, EXPRESS "
				+ "OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, "
				+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE "
				+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, "
				+ "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN "
				+ "CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*\n";

		final PDDocument test = new PDDocument();
		final OutputStream outputStream = new FileOutputStream("test.pdf");
		final PDPage page = new PDPage(PDRectangle.A4);
		test.addPage(page);
		PDPageContentStream contentStream = new PDPageContentStream(test, page,
				true, true);
		TextFlow paragraph = PdfUtil.createTextFlowFromMarkup(text, 11,
				BaseFont.Times);
		for (TextFragment fragment : paragraph) {
			System.out.println(fragment);
		}

		paragraph.setMaxWidth(300);
		System.out.println(paragraph.getHeight());
		float x = 20;
		paragraph.drawText(contentStream, new Coords(x, 700), Alignment.Right);
		contentStream.close();
		test.save(outputStream);
		test.close();
	}
}
