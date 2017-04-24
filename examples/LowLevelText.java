import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.shape.RoundRect;
import rst.pdfbox.layout.shape.Shape;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextFlowUtil;
import rst.pdfbox.layout.text.TextSequenceUtil;

public class LowLevelText {

    public static void main(String[] args) throws Exception {

	final PDDocument test = new PDDocument();
	final PDPage page = new PDPage(Constants.A4);
	float pageWidth = page.getMediaBox().getWidth();
	float pageHeight = page.getMediaBox().getHeight();

	test.addPage(page);
	PDPageContentStream contentStream = new PDPageContentStream(test, page,
		true, true);

	TextFlow text = TextFlowUtil
		.createTextFlowFromMarkup(
			"Hello *bold _italic bold-end* italic-end_. Eirmod\ntempor invidunt ut \\*labore",
			11, BaseFont.Times);

	text.addText("Spongebob", 11, PDType1Font.COURIER);
	text.addText(" is ", 20, PDType1Font.HELVETICA_BOLD_OBLIQUE);
	text.addText("cool", 7, PDType1Font.HELVETICA);

	text.setMaxWidth(100);
	float xOffset = TextSequenceUtil.getOffset(text, pageWidth,
		Alignment.Right);
	text.drawText(contentStream, new Position(xOffset, pageHeight - 50),
		Alignment.Right, null);

	String textBlock = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	text = new TextFlow();
	text.addMarkup(textBlock, 8, BaseFont.Courier);
	text.setMaxWidth(200);
	xOffset = TextSequenceUtil.getOffset(text, pageWidth, Alignment.Center);
	text.drawText(contentStream, new Position(xOffset, pageHeight - 100),
		Alignment.Justify, null);

	// draw a round rect box with text
	text.setMaxWidth(350);
	float x = 50;
	float y = pageHeight - 300;
	float paddingX = 20;
	float paddingY = 15;
	float boxWidth = text.getWidth() + 2*paddingX;
	float boxHeight = text.getHeight() + 2*paddingY;

	Shape shape = new RoundRect(20);
	shape.fill(test, contentStream, new Position(x, y), 
		boxWidth, boxHeight, Color.pink, null);
	shape.draw(test, contentStream, new Position(x, y), 
		boxWidth, boxHeight, Color.blue, new Stroke(3), null);
	 // now the text
	text.drawText(contentStream, new Position(x + paddingX, y - paddingY),
		Alignment.Center, null);

	
	contentStream.close();

	final OutputStream outputStream = new FileOutputStream(
		"lowleveltext.pdf");
	test.save(outputStream);
	test.close();

    }
}
