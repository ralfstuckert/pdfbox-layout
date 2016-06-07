import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;
import rst.pdfbox.layout.text.Indent;
import rst.pdfbox.layout.text.SpaceUnit;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.Enumerators.RomanEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseRomanEnumerator;
import rst.pdfbox.layout.util.Enumerators.AlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseAlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.ArabicEnumerator;

public class Indention {

    public static void main(String[] args) throws Exception {
	String bulletOdd = CompatibilityHelper.getBulletCharacter(1) + " ";
	String bulletEven = CompatibilityHelper.getBulletCharacter(2) + " ";
	String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat\n";

	Document document = new Document(Constants.A4, 40, 60, 40, 60);
	Paragraph paragraph = new Paragraph();
	paragraph
		.addMarkup(
			"This is an example for the new indent feature. Let's do some empty space indention:\n",
			11, BaseFont.Times);
	paragraph.add(new Indent(50, SpaceUnit.pt));
	paragraph.addMarkup("Here we go indented.\n", 11, BaseFont.Times);
	paragraph.addMarkup(
		"The Indention holds for the rest of the paragraph, or... \n",
		11, BaseFont.Times);
	paragraph.add(new Indent(70, SpaceUnit.pt));
	paragraph.addMarkup("any new indent comes.\n", 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph
		.addMarkup(
			"New paragraph, now indention is gone. But we can indent with a label also:\n",
			11, BaseFont.Times);
	paragraph.add(new Indent("This is some label", 100, SpaceUnit.pt, 11,
		PDType1Font.TIMES_BOLD));
	paragraph.addMarkup("Here we go indented.\n", 11, BaseFont.Times);
	paragraph
		.addMarkup(
			"And again, the Indention holds for the rest of the paragraph, or any new indent comes.\nLabels can be aligned:\n",
			11, BaseFont.Times);
	paragraph.add(new Indent("Left", 100, SpaceUnit.pt, 11,
		PDType1Font.TIMES_BOLD, Alignment.Left));
	paragraph.addMarkup("Indent with label aligned to the left.\n", 11,
		BaseFont.Times);
	paragraph.add(new Indent("Center", 100, SpaceUnit.pt, 11,
		PDType1Font.TIMES_BOLD, Alignment.Center));
	paragraph.addMarkup("Indent with label aligned to the center.\n", 11,
		BaseFont.Times);
	paragraph.add(new Indent("Right", 100, SpaceUnit.pt, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Indent with label aligned to the right.\n", 11,
		BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph.addMarkup(
		"So, what can you do with that? How about lists:\n", 11,
		BaseFont.Times);
	paragraph.add(new Indent(bulletOdd, 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("This is a list item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(bulletOdd, 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Another list item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(bulletEven, 8, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Sub list item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(bulletOdd, 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("And yet another one\n", 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph.addMarkup("Also available with indents: Enumerators:\n", 11,
		BaseFont.Times);
	RomanEnumerator e1 = new RomanEnumerator();
	LowerCaseAlphabeticEnumerator e2 = new LowerCaseAlphabeticEnumerator();
	paragraph.add(new Indent(e1.next() + ". ", 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("First item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(e1.next() + ". ", 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Second item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(e2.next() + ") ", 8, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("A sub item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(e2.next() + ") ", 8, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Another sub item\n", 11, BaseFont.Times);
	paragraph.add(new Indent(e1.next() + ". ", 4, SpaceUnit.em, 11,
		PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("Third item\n", 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	paragraph.addMarkup("The following types are built in:\n", 11,
		BaseFont.Times);
	paragraph.add(new Indent(new RomanEnumerator().next() + " ", 4,
		SpaceUnit.em, 11, PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("RomanEnumerator\n", 11, BaseFont.Times);
	paragraph.add(new Indent(new LowerCaseRomanEnumerator().next() + " ",
		4, SpaceUnit.em, 11, PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("LowerCaseRomanEnumerator\n", 11, BaseFont.Times);
	paragraph.add(new Indent(new AlphabeticEnumerator().next() + " ", 4,
		SpaceUnit.em, 11, PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("AlphabeticEnumerator\n", 11, BaseFont.Times);
	paragraph.add(new Indent(new LowerCaseAlphabeticEnumerator().next()
		+ " ", 4, SpaceUnit.em, 11, PDType1Font.TIMES_BOLD,
		Alignment.Right));
	paragraph.addMarkup("LowerCaseAlphabeticEnumerator\n", 11,
		BaseFont.Times);
	paragraph.add(new Indent(new ArabicEnumerator().next() + " ", 4,
		SpaceUnit.em, 11, PDType1Font.TIMES_BOLD, Alignment.Right));
	paragraph.addMarkup("ArabicEnumerator\n", 11, BaseFont.Times);
	document.add(paragraph);

	paragraph = new Paragraph();
	text1 = "For your convenience, you can do all that much easier with markup, e.g. simple indention\n"
		+ "--At vero eos et accusam\n\n"
		+ "-!And end the indention. Now a list:\n"
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

	final OutputStream outputStream = new FileOutputStream("indention.pdf");
	document.save(outputStream);
    }

}
