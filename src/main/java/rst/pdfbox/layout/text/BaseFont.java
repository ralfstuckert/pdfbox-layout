package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * In order to easy handling with fonts, this enum bundles the
 * plain/italic/bold/bold-italic variants of the three standard font types
 * {@link PDType1Font#TIMES_ROMAN Times},{@link PDType1Font#COURIER Courier} and
 * {@link PDType1Font#HELVETICA Helveticy}.
 * 
 * @author Ralf
 *
 */
public enum BaseFont {

    Times(PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD,
	    PDType1Font.TIMES_ITALIC, PDType1Font.TIMES_BOLD_ITALIC), //
    Courier(PDType1Font.COURIER, PDType1Font.COURIER_BOLD,
	    PDType1Font.COURIER_OBLIQUE, PDType1Font.COURIER_BOLD_OBLIQUE), //
    Helvetica(PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD,
	    PDType1Font.HELVETICA_OBLIQUE, PDType1Font.HELVETICA_BOLD_OBLIQUE);

    private PDFont plainFont;
    private PDFont boldFont;
    private PDFont italicFont;
    private PDFont boldItalicFont;

    private BaseFont(PDFont plainFont, PDFont boldFont, PDFont italicFont,
	    PDFont boldItalicFont) {
	this.plainFont = plainFont;
	this.boldFont = boldFont;
	this.italicFont = italicFont;
	this.boldItalicFont = boldItalicFont;
    }

    public PDFont getPlainFont() {
	return plainFont;
    }

    public PDFont getBoldFont() {
	return boldFont;
    }

    public PDFont getItalicFont() {
	return italicFont;
    }

    public PDFont getBoldItalicFont() {
	return boldItalicFont;
    }

}
