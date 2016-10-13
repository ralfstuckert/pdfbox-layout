package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.Position;

/**
 * Shapes an elipsis, or a circle if width==height.
 */
public class Elipsis extends RoundedRect {

    /**
     * Default constructor.
     */
    public Elipsis() {
	super(0);
    }


    @Override
    protected void addRoundRect(PDPageContentStream contentStream,
            Position upperLeft, float width, float height, float cornerRadiusX,
            float cornerRadiusY) throws IOException {
        super.addRoundRect(contentStream, upperLeft, width, height, width/2f,
        	height/2);
    }
}
