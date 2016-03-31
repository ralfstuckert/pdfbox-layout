package rst.pdfbox.layout.text;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextFlowUtil {

	public static TextFlow createTextFlow(final String text,
			final float fontSize, final PDFont font) {
		final Iterable<CharSequence> parts = fromPlainText(text);
		return createTextFlow(parts, fontSize, font, font, font,
				font);
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
					result.add(new NewLine(fontSize));
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

	public static Iterable<CharSequence> fromPlainText(final CharSequence text) {
		return fromPlainText(Collections.singleton(text));
	}

	public static Iterable<CharSequence> fromPlainText(
			final Iterable<CharSequence> text) {
		return splitByControlCharacter(ControlCharacter.NEWLINE, text, true);
	}

	public static Iterable<CharSequence> fromMarkup(final CharSequence markup) {
		return fromMarkup(Collections.singleton(markup));
	}

	public static Iterable<CharSequence> fromMarkup(
			final Iterable<CharSequence> markup) {
		Iterable<CharSequence> text = markup;
		text = splitByControlCharacter(ControlCharacter.BOLD, text, false);
		text = splitByControlCharacter(ControlCharacter.ITALIC, text, false);
		text = splitByControlCharacter(ControlCharacter.NEWLINE, text, true);
		return text;
	}

	protected static Iterable<CharSequence> lineBreak(
			final Iterable<CharSequence> text) {
		return splitByControlCharacter(ControlCharacter.NEWLINE, text, true);
	}

	protected static Iterable<CharSequence> splitByControlCharacter(
			ControlCharacter ctrl, final Iterable<CharSequence> markup, final boolean unescapeBackslash) {
		List<CharSequence> result = new ArrayList<CharSequence>();
		for (CharSequence current : markup) {
			if (current instanceof String) {
				String string = (String) current;
				String[] parts = ctrl.getPattern().split(string, -1);
				for (int i = 0; i < parts.length; i++) {
					if (i > 0) {
						result.add(ctrl);
					}
					if (!parts[i].isEmpty()) {
						String unescaped = ctrl.unescape(parts[i]);
						if (unescapeBackslash) {
							unescaped = ControlCharacter.unescapeBackslash(unescaped);
						}
						result.add(unescaped);
					}
				}
			} else {
				result.add(current);
			}
		}
		return result;
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
		TextFlow paragraph = TextFlowUtil.createTextFlowFromMarkup(text, 11,
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
