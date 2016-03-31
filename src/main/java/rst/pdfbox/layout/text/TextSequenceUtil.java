package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Paragraph;

public class TextSequenceUtil {

	public static List<TextLine> getLines(
			final TextSequence text) throws IOException {
		final List<TextLine> result = new ArrayList<TextLine>();

		TextLine line = new TextLine();
		for (TextFragment fragment : text) {
			if (fragment instanceof NewLine) {
				line.setNewLine((NewLine)fragment);
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

	public static Divided divide(final TextSequence text, final float maxWidth,
			final float maxHeight) throws IOException {
		TextFlow wrapped = wordWrap(text, maxWidth);
		List<TextLine> lines = getLines(wrapped);

		Paragraph first = new Paragraph();
		Paragraph last = new Paragraph();
		if (text instanceof TextFlow) {
			TextFlow flow = (TextFlow) text;
			first.setMaxWidth(flow.getMaxWidth());
			first.setLineSpacing(flow.getLineSpacing());
			last.setMaxWidth(flow.getMaxWidth());
			last.setLineSpacing(flow.getLineSpacing());
		}

		int index = 0;
		do {
			TextLine line = lines.get(index);
			first.add(line);
			++index;
		} while (index < lines.size() && first.getHeight() < maxHeight);

		if (first.getHeight() < maxHeight) {
			first.removeLast();
			--index;
		}

		for (int i = index; i < lines.size(); ++i) {
			last.add(lines.get(i));
		}
		return new Divided(first, last);
	}

	public static TextFlow wordWrap(final TextSequence text,
			final float maxWidth) throws IOException {

		TextFlow result = new TextFlow();
		float lineLength = 0;
		for (TextFragment fragment : text) {
			if (fragment instanceof NewLine) {
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
						result.add(new WrappingNewLine(fontDescriptor));
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
		TextFlow wrapped = wordWrap(text, maxWidth);
		List<TextLine> lines = getLines(wrapped);
		return lines;
	}

	public static TextFlow splitWords(final TextFragment text) {
		TextFlow result = new TextFlow();
		if (text instanceof NewLine) {
			result.add(text);
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
				float nextLineHeight = lines.get(i + 1)
						.getLineHeightWithSpacing(lineSpacing);
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

	public static float getWidth(final TextSequence textSequence,
			final float preferredMaxWidth) throws IOException {
		List<TextLine> lines = wordWrapToLines(textSequence,
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
		List<TextLine> lines = wordWrapToLines(textSequence,
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


}
