import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Frame;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.shape.Elipsis;
import rst.pdfbox.layout.shape.Rect;
import rst.pdfbox.layout.shape.RoundedRect;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;

public class Frames {

    public static void main(String[] args) throws Exception {
	String text1 = "{color:#ffffff}Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\n Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ " At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

	String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum."
		+ "Stet clita kasd gubergren, no sea takimata\n\n"
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	Document document = new Document(Constants.A5);

	Paragraph paragraph = new Paragraph();
	paragraph.addMarkup("Am I living in a box?", 11, BaseFont.Times);
	Frame box = new Frame(paragraph);
	box.setShape(new Rect());
	box.setBorderColor(Color.black);
	box.setBorderStroke(new Stroke());
	box.setPadding(10, 10, 5, 5);
	box.setMargin(40, 40, 20, 10);
	document.add(box, VerticalLayoutHint.CENTER);

	paragraph = new Paragraph();
	paragraph.addMarkup(text1, 11, BaseFont.Times);
	box = new Frame(paragraph, 200f, null);
	box.setShape(new Rect());
	box.setBackgroundColor(Color.black);
	box.setPadding(10, 10, 5, 5);
	box.setMargin(40, 40, 20, 10);
	document.add(box);

	paragraph = new Paragraph();
	paragraph.addMarkup("{color:#aa00aa}*Ain't no rectangle*", 22, BaseFont.Helvetica);
	paragraph.setAlignment(Alignment.Center);
	box = new Frame(paragraph, 300f, 100f);
	box.setShape(new Elipsis());
	box.setBorderColor(Color.green);
	box.setBorderStroke(new Stroke(2));
	box.setBackgroundColor(Color.pink);
	box.setPadding(50, 0, 35, 0);
//	box.setMargin(30, 30, 20, 10);
	document.add(box);

	paragraph = new Paragraph();
	paragraph.addMarkup("Frames also paginate, see here:\n\n", 13, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	box = new Frame(paragraph, null, null);
	box.setShape(new RoundedRect(10));
	box.setBorderColor(Color.magenta);
	box.setBorderStroke(new Stroke(3));
	box.setBackgroundColor(new Color(255,240,180));
	box.setPadding(20, 15, 10, 15);
	box.setMargin(50, 50, 20, 10);
	document.add(box);

	final OutputStream outputStream = new FileOutputStream("frames.pdf");
	document.save(outputStream);

    }
}
