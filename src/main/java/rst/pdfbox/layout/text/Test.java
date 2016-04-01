package rst.pdfbox.layout.text;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Test {

	public static void main(String[] args) throws Exception {

		final PDDocument test = new PDDocument();
		final OutputStream outputStream = new FileOutputStream("test.pdf");
		final PDPage page = new PDPage(PDRectangle.A4);
		test.addPage(page);
		PDPageContentStream contentStream = new PDPageContentStream(test, page,
				true, true);
		TextFlow paragraph = new TextFlow();
		TextFlow text = TextFlowUtil
				.createTextFlowFromMarkup(
						"Hallo *bold _italic boldend* italicend_af\n hdsafkhkhds \\*dsjhfs",
						11, BaseFont.Times);
		TextLine line = new TextLine();
		line.add(new StyledText("Blubber", 11, PDType1Font.COURIER));
		line.add(new StyledText(" ist ", 20, PDType1Font.HELVETICA_BOLD_OBLIQUE));
		line.add(new StyledText("too", 7, PDType1Font.HELVETICA));
		text.add(line);

		paragraph.add(text);
		paragraph.setMaxWidth(100);
		float x = 400 - paragraph.getWidth();
		paragraph.drawText(contentStream, new Position(x, 600), Alignment.Right);
		paragraph.drawText(contentStream,
				new Position(x, 600 - paragraph.getHeight()), Alignment.Right);
		contentStream.close();
		test.save(outputStream);
		test.close();
	}
}
