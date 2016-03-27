package rst.pdfbox.layout.text;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public interface DrawableText extends Area {

	void drawText(PDPageContentStream contentStream, Coords beginOfText,
			Alignment alignment) throws IOException;
}
