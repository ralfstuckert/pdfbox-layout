import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.text.BaseFont;

public class Links {

    public static void main(String[] args) throws Exception {
	String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\n Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ " At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

	String text2 = "At *vero {link[#hello]}eos{link} et accusam* et justo duo dolores et ea rebum."
		+ "Stet clita kasd gubergren, no sea takimata\n\n"
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	Document document = new Document(40, 60, 40, 60);

	
	Paragraph paragraph0 = new Paragraph();
	paragraph0.addMarkup("This is a link to {link[https://github.com/ralfstuckert/pdfbox-layout]}PDFBox-Layout{link}.\n\n", 11, BaseFont.Times);
	paragraph0.addMarkup("Now the same link with color instead of underline {color:#ff5000}{link:none[https://github.com/ralfstuckert/pdfbox-layout]}PDFBox-Layout{link}{color:#000000}.\n\n", 11, BaseFont.Times);
	paragraph0.addMarkup("And here comes a link to an internal anchor name {color:#ff5000}{link[#hello]}hello{link}{color:#000000}.\n\n", 11, BaseFont.Times);
	document.add(paragraph0);

	Paragraph paragraph1 = new Paragraph();
	paragraph1.addMarkup(text1, 11, BaseFont.Times);
	document.add(paragraph1);

	Paragraph paragraph2 = new Paragraph();
	paragraph2.addMarkup(text2, 12, BaseFont.Helvetica);
	document.add(paragraph2);

	Paragraph paragraph3 = new Paragraph();
	paragraph3.addMarkup(text1, 8, BaseFont.Courier);
	document.add(paragraph3);

	Paragraph paragraph4 = new Paragraph();
	paragraph4.addMarkup("\n\n{anchor:hello}Here{anchor} comes the internal anchor named *hello*\n\n", 15, BaseFont.Courier);

	document.add(paragraph1);
	document.add(paragraph3);
	document.add(paragraph1);
	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph3);

	document.add(paragraph4);

	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph1);

	final OutputStream outputStream = new FileOutputStream(
		"links.pdf");
	document.save(outputStream);

    }
}
