package rst.pdfbox.layout;

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
		TextFlow text = PdfUtil
				.createTextFlowFromMarkup(
						"Hallo *bold _italic boldend* italicend_af\n hdsafkhkhds \\*dsjhfs",
						11, BaseFont.Times);
		TextLine line = new TextLine();
		line.add(new StyledText("Blubber", PDType1Font.COURIER, 11));
		line.add(new StyledText(" ist ", PDType1Font.HELVETICA_BOLD_OBLIQUE, 20));
		line.add(new StyledText("too", PDType1Font.HELVETICA, 7));
		text.add(line);
		System.out.println(line.getHeight());
		
		paragraph.add(text);
		paragraph.setPreferredMaxWidth(100);
		float x = 400 - paragraph.getWidth();
		paragraph.drawText(contentStream, new Coords(x, 600), Alignment.Right);
		contentStream.close();
		test.save(outputStream);
		test.close();
	}
}
