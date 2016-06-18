import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.Orientation;
import rst.pdfbox.layout.elements.PageFormat;
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

	Document document = new Document(40, 60, 40, 60);
	Paragraph paragraph = new Paragraph();
	paragraph.addMarkup(text1, 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph
		.addMarkup(
			"Markup supports *bold*, _italic_, and *even _mixed* markup_.\n",
			11, BaseFont.Times);
	    paragraph.addMarkup(
	            "And now also {color:#ff0000}c{color:#00ff00}o{color:#0000ff}l{color:#00cccc}o{color:#cc00cc}r{color:#000000}.\n\n",
	            11, BaseFont.Times);
	paragraph.addMarkup(
		"Escape \\* with \\\\\\* and \\_ with \\\\\\_ in markup.\n\n",
		11, BaseFont.Times);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 0, 0,
		30, 0));

	paragraph = new Paragraph();
	text1 = "\nAlso, you can do all that indentation stuff much easier with markup, e.g. simple indentation\n"
		+ "--At vero eos et accusam\n\n"
		+ "-!And end the indentation. Now a list:\n"
		+ "-+This is a list item\n"
		+ "-+Another list item\n"
		+ " -+A sub list item\n"
		+ "-+And yet another one\n\n"
		+ "-!Even enumeration is supported:\n"
		+ "-#This is a list item\n"
		+ "-#Another list item\n"
		+ " -#{a:}A sub list item\n"
		+ "-#And yet another one\n\n"
		+ "-!And you can customize it:\n"
		+ "-#{I ->:5}This is a list item\n"
		+ "-#{I ->:5}Another list item\n"
		+ " -#{a ~:30pt}A sub list item\n"
		+ "-#{I ->:5}And yet another one\n\n";
	paragraph.addMarkup(text1, 11, BaseFont.Times);
	document.add(paragraph);


	final OutputStream outputStream = new FileOutputStream("markup.pdf");
	document.save(outputStream);

    }
}
