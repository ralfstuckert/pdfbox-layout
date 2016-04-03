import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.StyledText;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextFlowUtil;
import rst.pdfbox.layout.text.TextSequenceUtil;

public class LowLevelText {

    public static void main(String[] args) throws Exception {

	final PDDocument test = new PDDocument();
	final OutputStream outputStream = new FileOutputStream("lowleveltext.pdf");
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

	text.add(new StyledText("Spongebob", 11, PDType1Font.COURIER));
	text.add(new StyledText(" is ", 20, PDType1Font.HELVETICA_BOLD_OBLIQUE));
	text.add(new StyledText("cool", 7, PDType1Font.HELVETICA));

	text.setMaxWidth(100);
	float xOffset= TextSequenceUtil.getOffset(text, pageWidth,
		Alignment.Right);
	text.drawText(contentStream, new Position(xOffset, pageHeight-50),
		Alignment.Right);

	String textBlock = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\n Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ " At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

	text = TextFlowUtil
		.createTextFlowFromMarkup(
			textBlock,
			8, BaseFont.Courier);
	text.setMaxWidth(200);
	xOffset= TextSequenceUtil.getOffset(text, pageWidth,
		Alignment.Center);
	text.drawText(contentStream, new Position(xOffset, pageHeight-100),
		Alignment.Center);

	contentStream.close();
	test.save(outputStream);
	test.close();

    }
}
