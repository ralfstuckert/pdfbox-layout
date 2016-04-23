package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextSequenceUtil;
import rst.pdfbox.layout.text.WidthRespecting;

/**
 * A paragraph is used as a container for {@link TextFlow text} that is drawn as
 * one element. A paragraph has a {@link #setAlignment(Alignment) (text-)
 * alignment}, and {@link WidthRespecting respects a given width} by applying
 * word-wrap.
 */
public class Paragraph extends TextFlow implements Drawable, Element,
	WidthRespecting, Dividable {

    private Position absolutePosition;
    private Alignment alignment = Alignment.Left;

    @Override
    public Position getAbsolutePosition() {
	return absolutePosition;
    }

    /**
     * Sets the absolute position to render at.
     * 
     * @param absolutePosition
     *            the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
	this.absolutePosition = absolutePosition;
    }

    /**
     * @return the text alignment to apply. Default is left.
     */
    public Alignment getAlignment() {
	return alignment;
    }

    /**
     * Sets the alignment to apply.
     * 
     * @param alignment
     *            the text alignment.
     */
    public void setAlignment(Alignment alignment) {
	this.alignment = alignment;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft) throws IOException {
	drawText(contentStream, upperLeft, getAlignment());
    }

    @Override
    public Divided divide(float remainingHeight, final float pageHeight)
	    throws IOException {
	return TextSequenceUtil.divide(this, getMaxWidth(), remainingHeight);
    }

    @Override
    public Paragraph removeLeadingEmptyVerticalSpace() throws IOException {
	return removeLeadingEmptyLines();
    }

    @Override
    public Paragraph removeLeadingEmptyLines() throws IOException {
	Paragraph result = (Paragraph) super.removeLeadingEmptyLines();
	result.setAbsolutePosition(this.getAbsolutePosition());
	result.setAlignment(this.getAlignment());
	return result;
    }

    @Override
    protected Paragraph createInstance() {
	return new Paragraph();
    }

}
