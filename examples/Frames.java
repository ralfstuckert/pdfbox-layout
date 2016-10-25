import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Frame;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.shape.Ellipse;
import rst.pdfbox.layout.shape.Rect;
import rst.pdfbox.layout.shape.RoundRect;
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

	String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum. "
		+ "Stet clita kasd gubergren, no sea takimata.\n\n"
		+ "Sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	Document document = new Document(new PageFormat(Constants.A5));

	Paragraph paragraph = new Paragraph();
	paragraph.addMarkup("Am I living in a box?", 11, BaseFont.Times);
	Frame frame = new Frame(paragraph);
	frame.setShape(new Rect());
	frame.setBorder(Color.black, new Stroke());
	frame.setPadding(10, 10, 5, 5);
	frame.setMargin(40, 40, 20, 10);
	document.add(frame, VerticalLayoutHint.CENTER);

	paragraph = new Paragraph();
	paragraph.addMarkup(text1, 11, BaseFont.Times);
	frame = new Frame(paragraph, 200f, null);
	frame.setShape(new Rect());
	frame.setBackgroundColor(Color.black);
	frame.setPadding(10, 10, 5, 5);
	frame.setMargin(40, 40, 20, 10);
	document.add(frame);

	paragraph = new Paragraph();
	paragraph.addMarkup("{color:#aa00aa}*Ain't no rectangle*", 22, BaseFont.Helvetica);
	paragraph.setAlignment(Alignment.Center);
	frame = new Frame(paragraph, 300f, 100f);
	frame.setShape(new Ellipse());
	frame.setBorder(Color.green, new Stroke(2));
	frame.setBackgroundColor(Color.pink);
	frame.setPadding(50, 0, 35, 0);
//	frame.setMargin(30, 30, 20, 10);
	document.add(frame);

	paragraph = new Paragraph();
	paragraph.addMarkup("Frames also paginate, see here:\n\n", 13, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	paragraph.addMarkup(text2, 11, BaseFont.Times);
	frame = new Frame(paragraph, null, null);
	frame.setShape(new RoundRect(10));
	frame.setBorder(Color.magenta, new Stroke(3));
	frame.setBackgroundColor(new Color(255,240,180));
	frame.setPadding(20, 15, 10, 15);
	frame.setMargin(50, 50, 20, 10);
	document.add(frame);

	final OutputStream outputStream = new FileOutputStream("frames.pdf");
	document.save(outputStream);

    }
    
}
