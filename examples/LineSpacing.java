import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.ColumnLayout;
import rst.pdfbox.layout.text.BaseFont;

public class LineSpacing {

    public static void main(String[] args) throws Exception {
	String text = "*Lorem ipsum* dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et _accusam et justo_ "
		+ "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est _Lorem ipsum dolor sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam.";

	// create document without margins
	Document document = new Document();
	document.add(new ColumnLayout(2, 5));

	Paragraph left = new Paragraph();
	// no line spacing for the first line
	left.setApplyLineSpacingToFirstLine(false);
	// use a bigger line spacing to visualize the effects of line spacing more drastically
	left.setLineSpacing(1.5f);
	left.setMaxWidth(document.getPageWidth()/2);
	left.addMarkup(text, 11, BaseFont.Times);
	document.add(left);

	document.add(left);
	document.add(left);
	
	document.add(ColumnLayout.NEWCOLUMN);
	
	Paragraph right = new Paragraph();
	right.setLineSpacing(1.5f);
	right.setMaxWidth(document.getPageWidth()/2);
	right.addMarkup(text, 11, BaseFont.Times);
	document.add(right);

	document.add(right);
	document.add(right);

	final OutputStream outputStream = new FileOutputStream(
		"linespacing.pdf");
	document.save(outputStream);

    }
}
