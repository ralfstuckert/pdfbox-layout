import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;

public class Markup {

    public static void main(String[] args) throws Exception {
	String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n"
		+ " At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

	Document document = new Document(Constants.A4, 40, 60, 40, 60);
	Paragraph paragraph = new Paragraph();
	paragraph.addMarkup(text1, 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph
		.addMarkup(
			"Markup supports *bold*, _italic_, and *even _mixed* markup_.\n",
			11, BaseFont.Times);
	paragraph.addMarkup(
		"Escape \\* with \\\\\\* and \\_ with \\\\\\_ in markup.\n",
		11, BaseFont.Times);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 0, 0,
		30, 0));

	final OutputStream outputStream = new FileOutputStream("markup.pdf");
	document.save(outputStream);

    }
}
