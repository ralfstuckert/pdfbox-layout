package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.Position;

public class Elipsis extends RoundedRect {

    public Elipsis() {
	super(0);
    }


    @Override
    protected void addRoundRect(PDPageContentStream contentStream,
            Position upperLeft, float width, float height, float cornerRadianX,
            float cornerRadianY) throws IOException {
        super.addRoundRect(contentStream, upperLeft, width, height, width/2f,
        	height/2);
    }
}
