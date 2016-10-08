package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.Position;

public class RoundedRect extends AbstractShape {

    private final static float BEZ = 0.551915024494f;

    private final float cornerRadianX;
    private final float cornerRadianY;

    public RoundedRect(float cornerRadian) {
	this(cornerRadian, cornerRadian);
    }

    public RoundedRect(float cornerRadianX, float cornerRadianY) {
	this.cornerRadianX = cornerRadianX;
	this.cornerRadianY = cornerRadianY;
    }

    @Override
    public void add(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height) throws IOException {
	
	addRoundRect(contentStream, upperLeft, width, height, cornerRadianX, cornerRadianY);
    }

    /**
     * create points clockwise starting in upper left corner
     * 
     * <pre>
     *     a          b
     *      ----------
     *     /          \
     *  h |            | c
     *    |            |
     *    |            |
     *   g \          / d
     *      ----------
     *     f          e
     * </pre>
     */
    protected void addRoundRect(PDPageContentStream contentStream,
	    Position upperLeft, float width, float height, float cornerRadianX,
	    float cornerRadianY) throws IOException {
	float nettoWidth = width - 2 * cornerRadianX;
	float nettoHeight = height - 2 * cornerRadianY;

	// top line
	Position a = new Position(upperLeft.getX() + cornerRadianX,
		upperLeft.getY());
	Position b = new Position(a.getX() + nettoWidth, a.getY());
	// right line
	Position c = new Position(upperLeft.getX() + width, upperLeft.getY()
		- cornerRadianY);
	Position d = new Position(c.getX(), c.getY() - nettoHeight);
	// bottom line
	Position e = new Position(
		upperLeft.getX() + width - cornerRadianX, upperLeft.getY()
			- height);
	Position f = new Position(e.getX() - nettoWidth, e.getY());
	// left line
	Position g = new Position(upperLeft.getX(), upperLeft.getY() - height
		+ cornerRadianY);
	Position h = new Position(g.getX(), upperLeft.getY()
		- cornerRadianY);

	float bezX = cornerRadianX * BEZ;
	float bezY = cornerRadianY * BEZ;

	contentStream.moveTo(a.getX(), a.getY());
	addLine(contentStream, a.getX(), a.getY(), b.getX(), b.getY());
	contentStream.addBezier312(b.getX() + bezX, b.getY(), c.getX(),
		c.getY() + bezY, c.getX(), c.getY());
	// contentStream.addLine(c.getX(), c.getY(), d.getX(), d.getY());
	addLine(contentStream, c.getX(), c.getY(), d.getX(), d.getY());
	contentStream.addBezier312(d.getX(), d.getY() - bezY, e.getX() + bezX,
		e.getY(), e.getX(), e.getY());
	// contentStream.addLine(e.getX(), e.getY(), f.getX(), f.getY());
	addLine(contentStream, e.getX(), e.getY(), f.getX(), f.getY());
	contentStream.addBezier312(f.getX() - bezX, f.getY(), g.getX(),
		g.getY() - bezY, g.getX(), g.getY());
	addLine(contentStream, g.getX(), g.getY(), h.getX(), h.getY());
	contentStream.addBezier312(h.getX(), h.getY() + bezY, a.getX() - bezX,
		a.getY(), a.getX(), a.getY());
    }

    /**
     * Using lines won't give us a continuing path, which looks silly on fill.
     * So we are approximating lines with bezier curves... is there no better
     * way?
     */
    private void addLine(final PDPageContentStream contentStream, float x1,
	    float y1, float x2, float y2) throws IOException {
	float xMid = (x1 + x2) / 2f;
	float yMid = (y1 + y2) / 2f;
	contentStream.addBezier31(xMid, yMid, x2, y2);
    }

}
