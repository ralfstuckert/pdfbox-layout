import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.Orientation;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.elements.render.ColumnLayout;
import rst.pdfbox.layout.elements.render.ColumnLayoutHint;
import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;

public class Rotation {

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

	String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum. "
		+ "Stet clita kasd gubergren, no sea takimata\n\n"
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	Paragraph paragraph1 = new Paragraph();
	paragraph1.addMarkup(text1, 11, BaseFont.Times);
	Paragraph paragraph2 = new Paragraph();
	paragraph2.addMarkup(text2, 12, BaseFont.Helvetica);
	Paragraph paragraph3 = new Paragraph();
	paragraph3.addMarkup(text1, 8, BaseFont.Courier);

	Paragraph titleA4 = new Paragraph();
	titleA4.addMarkup("*Format A4 Landscape*", 20, BaseFont.Times);
	Paragraph titleA5 = new Paragraph();
	titleA5.addMarkup("*Format A4 Landscape rotated by -90 degrees*", 20, BaseFont.Times);
	
	PageFormat a4_landscape = PageFormat.with().margins(40, 50, 40, 60).landscape().build();
	PageFormat a4_landscape_rotated = PageFormat.with().margins(40, 50, 40, 60).landscape().rotation(-90).build();

	Document document = new Document(a4_landscape);
	
	document.add(titleA4, VerticalLayoutHint.CENTER);
	document.add(new VerticalSpacer(5));
	document.add(new ColumnLayout(2, 10));

	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph1);
	document.add(paragraph3);
	document.add(paragraph2);
	document.add(paragraph2);
	document.add(paragraph3);

	document.add(a4_landscape_rotated);
	document.add(ControlElement.NEWPAGE);
	document.add(new VerticalLayout());
	document.add(titleA5, VerticalLayoutHint.CENTER);
	document.add(new VerticalSpacer(5));
	document.add(new ColumnLayout(2, 10));

	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph1);
	document.add(paragraph3);
	document.add(paragraph2);
	document.add(paragraph2);
	document.add(paragraph3);

	document.add(a4_landscape);
	document.add(ControlElement.NEWPAGE);
	document.add(new VerticalLayout());
	document.add(titleA4, VerticalLayoutHint.CENTER);
	document.add(new VerticalSpacer(5));
	document.add(new ColumnLayout(2, 10));

	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph1);
	document.add(paragraph3);
	document.add(paragraph2);
	document.add(paragraph2);
	document.add(paragraph3);

	document.add(a4_landscape_rotated);
	document.add(ControlElement.NEWPAGE);
	document.add(new VerticalLayout());
	document.add(titleA5, VerticalLayoutHint.CENTER);
	document.add(new VerticalSpacer(5));
	document.add(new ColumnLayout(2, 10));

	document.add(paragraph2);
	document.add(paragraph1);
	document.add(paragraph1);
	document.add(paragraph3);
	document.add(paragraph2);
	document.add(paragraph2);
	document.add(paragraph3);

	final OutputStream outputStream = new FileOutputStream(
		"rotation.pdf");
	document.save(outputStream);

    }
}
