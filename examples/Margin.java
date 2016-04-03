import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Constants;

public class Margin {

    public static void main(String[] args) throws Exception {
	String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo "
		+ "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

	String text2 = "short text, right aligned with some margin";

	String text3 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo "
		+ "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

	Document document = new Document(Constants.A4, 20, 40, 20, 40);
	Paragraph paragraph = new Paragraph();
	paragraph.addText(text1, 11, PDType1Font.HELVETICA);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 0, 100,
		100, 100));

	paragraph = new Paragraph();
	paragraph.addText(text2, 11, PDType1Font.HELVETICA);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Right, 0, 50,
		0, 0));

	paragraph = new Paragraph();
	paragraph.addText(text3, 11, PDType1Font.HELVETICA);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Right, 150,
		150, 20, 0));

	final OutputStream outputStream = new FileOutputStream("margin.pdf");
	document.save(outputStream);

    }
}
