import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.Orientation;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Constants;

public class HelloDoc {

    public static void main(String[] args) throws Exception {
	PageFormat pageFormat = new PageFormat(Constants.A4, Orientation.Portrait, 40f, 60f, 40f, 60f);
	Document document = new Document(pageFormat);

	Paragraph paragraph = new Paragraph();
	paragraph.addText("Hello Document", 20,
		PDType1Font.HELVETICA);
	document.add(paragraph);

	final OutputStream outputStream = new FileOutputStream("hellodoc.pdf");
	document.save(outputStream);

    }
}
