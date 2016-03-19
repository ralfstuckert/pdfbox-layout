package rst.pdfbox.layout;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
