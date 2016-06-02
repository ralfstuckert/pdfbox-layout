package rst.pdfbox.layout.text;

import java.io.FileOutputStream;
import java.io.OutputStream;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;

/**
 * A NewLine introduced by wrapping. This interface is useful for detecting
 * new-lines not contained in the original text.
 */
public class WrappingNewLine extends NewLine {

    /**
     * See {@link NewLine#NewLine()}.
     */
    public WrappingNewLine() {
	super();
    }

    /**
     * See {@link NewLine#NewLine(FontDescriptor)}.
     * @param fontDescriptor the font and size associated with this new line.
     */
    public WrappingNewLine(FontDescriptor fontDescriptor) {
	super(fontDescriptor);
    }

    /**
     * See {@link NewLine#NewLine(float)}.
     * @param fontSize the font size, resp. the height of the new line.
     */
    public WrappingNewLine(float fontSize) {
	super(fontSize);
    }

    public static void main(String[] args) throws Exception {
	String text1 = 
		"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n--Stet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n"
		+ " -#{I:}At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ "At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n"
		+ " -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ " -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ "  -#{a -:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ "  -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ "   -#{i:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ " -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ " -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ " -#{I::}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ " -#{I :}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
		+ "-+{--:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n"
	+ " -#{I:}At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd "
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
	            "And now also {color:#ff0000}c{color:#00ff00}o{color:#0000ff}l{color:#00cccc}o{color:#cc00cc}r{color:#000000}.\n\n",
	            11, BaseFont.Times);
	paragraph.addMarkup(
		"Escape \\* with \\\\\\* and \\_ with \\\\\\_ in markup.\n",
		11, BaseFont.Times);
	document.add(paragraph, new VerticalLayoutHint(Alignment.Left, 0, 0,
		30, 0));

	final OutputStream outputStream = new FileOutputStream("test.pdf");
	document.save(outputStream);

    }

}
