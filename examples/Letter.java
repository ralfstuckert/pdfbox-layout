import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;
import rst.pdfbox.layout.text.Position;

public class Letter {

    public static void main(String[] args) throws Exception {
	float hMargin = 40;
	float vMargin = 50;
	Document document = new Document(Constants.A4, hMargin, hMargin,
		vMargin, vMargin);

	Paragraph paragraph = new Paragraph();
	paragraph.addText("Blubberhausen, 01.04.2016", 11,
		PDType1Font.HELVETICA);
	document.add(paragraph, VerticalLayoutHint.RIGHT);

	document.add(new VerticalSpacer(100));

	paragraph = new Paragraph();
	String address = "Ralf Stuckert\nAm Hollergraben 24\n67346 Blubberhausen";
	paragraph.addText(address, 11, PDType1Font.HELVETICA);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph.addMarkup("*Labore et dolore magna aliquyam erat*", 11,
		BaseFont.Helvetica);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 0, 0,
		40, 20));

	String text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\n Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ " At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";
	paragraph = new Paragraph();
	paragraph.addMarkup(text, 11, BaseFont.Helvetica);
	document.add(paragraph);

	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph.addMarkup("Dolore magna aliquyam erat\nRalf Stuckert", 11,
		BaseFont.Helvetica);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 60, 0,
		40, 0));

	paragraph = new Paragraph();
	paragraph.addMarkup("*Sanctus est:* Lorem ipsum dolor consetetur "
		+ "sadipscing sed diam nonumy eirmod tempor invidunt", 6,
		BaseFont.Times);
	paragraph.setAbsolutePosition(new Position(hMargin, vMargin));
	document.add(paragraph);

	final OutputStream outputStream = new FileOutputStream("letter.pdf");
	document.save(outputStream);

    }
}
