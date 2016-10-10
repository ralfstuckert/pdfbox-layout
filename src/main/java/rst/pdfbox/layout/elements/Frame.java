package rst.pdfbox.layout.elements;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.shape.Rect;
import rst.pdfbox.layout.shape.RoundedRect;
import rst.pdfbox.layout.shape.Shape;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Constants;
import rst.pdfbox.layout.text.DrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.WidthRespecting;

public class Frame implements Element, Drawable, WidthRespecting, Dividable {

    private Drawable inner;

    private float leftMargin;
    private float rightMargin;
    private float topMargin;
    private float bottomMargin;

    private Shape shape = new Rect();
    private Stroke borderStroke = new Stroke();
    private Color borderColor;
    private Color backgroundColor;

    private float maxWidth = -1;

    private Float width;
    private Float height;

    private Position absolutePosition;

    public Frame(final Drawable inner) {
	this(inner, null, null);
    }

    public Frame(final Drawable inner, final Float width, final Float height) {
	this.inner = inner;
	this.width = width;
	this.height = height;
    }

    public Shape getShape() {
	return shape;
    }

    public void setShape(Shape shape) {
	this.shape = shape;
    }

    public Stroke getBorderStroke() {
	return borderStroke;
    }

    public void setBorderStroke(Stroke borderStroke) {
	this.borderStroke = borderStroke;
    }

    protected float getBorderWidth() {
	return hasBorder() ? getBorderStroke().getLineWidth() : 0;
    }

    protected boolean hasBorder() {
	return getShape() != null && getBorderStroke() != null
		&& getBorderColor() != null;
    }

    public Color getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(Color borderColor) {
	this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    protected void copyAllButInnerAndSizeTo(final Frame other) {
	other.setShape(this.getShape());
	other.setBorderStroke(this.getBorderStroke());
	other.setBorderColor(this.getBorderColor());
	other.setBackgroundColor(this.getBackgroundColor());
	other.setBottomMargin(this.getBottomMargin());
	other.setLeftMargin(this.getLeftMargin());
	other.setRightMargin(this.getRightMargin());
	other.setTopMargin(this.getTopMargin());
    }

    public float getLeftMargin() {
	return leftMargin;
    }

    public void setLeftMargin(float leftMargin) {
	this.leftMargin = leftMargin;
    }

    public float getRightMargin() {
	return rightMargin;
    }

    public void setRightMargin(float rightMargin) {
	this.rightMargin = rightMargin;
    }

    public float getTopMargin() {
	return topMargin;
    }

    public void setTopMargin(float topMargin) {
	this.topMargin = topMargin;
    }

    public float getBottomMargin() {
	return bottomMargin;
    }

    public void setBottomMargin(float bottomMargin) {
	this.bottomMargin = bottomMargin;
    }

    protected float getHorizontalMarginsWithBorder() {
	return getLeftMargin() + getRightMargin() + getBorderWidth();
    }

    protected float getVerticalMarginsWithBorder() {
	return getTopMargin() + getBottomMargin() + getBorderWidth();
    }

    public void setMargins(float left, float right, float top, float bottom) {
	setLeftMargin(left);
	setRightMargin(right);
	setTopMargin(top);
	setBottomMargin(bottom);
    }

    protected Float getInternalHeight() {
	return height;
    }

    protected Float getInternalWidth() {
	return width;
    }

    @Override
    public float getWidth() throws IOException {
	if (getInternalWidth() != null) {
	    return getInternalWidth();
	}
	return inner.getWidth() + getHorizontalMarginsWithBorder();
    }

    @Override
    public float getHeight() throws IOException {
	if (getInternalHeight() != null) {
	    return getInternalHeight();
	}
	return inner.getHeight() + getVerticalMarginsWithBorder();
    }

    @Override
    public Position getAbsolutePosition() throws IOException {
	return absolutePosition;
    }

    public void setAbsolutePosition(Position absolutePosition) {
	this.absolutePosition = absolutePosition;
    }

    @Override
    public float getMaxWidth() {
	return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
	this.maxWidth = maxWidth;

	if (inner instanceof WidthRespecting) {
	    if (getInternalWidth() != null) {
		((WidthRespecting) inner).setMaxWidth(getInternalWidth()
			- getHorizontalMarginsWithBorder());
	    } else if (maxWidth >= 0) {
		((WidthRespecting) inner).setMaxWidth(maxWidth
			- getHorizontalMarginsWithBorder());
	    }
	}
    }
    
    protected void setInnerMaxWidthIfNecessary() throws IOException {
	if (getAbsolutePosition() != null && getInternalWidth() != null) {
	    setMaxWidth(getInternalWidth());
	}
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, DrawListener drawListener) throws IOException {
	
	setInnerMaxWidthIfNecessary();

	float halfBorderWidth = 0;
	if (getBorderWidth() > 0) {
	    halfBorderWidth = getBorderWidth() / 2f;
	    upperLeft = upperLeft.add(halfBorderWidth, -halfBorderWidth);
	}

	if (getShape() != null) {
	    if (getBackgroundColor() != null) {
		getShape().fill(pdDocument, contentStream, upperLeft,
			getWidth(), getHeight(), getBackgroundColor(),
			drawListener);
	    }
	    if (hasBorder()) {
		getShape().draw(pdDocument, contentStream, upperLeft,
			getWidth(), getHeight(), getBorderColor(),
			getBorderStroke(), drawListener);
	    }
	}

	Position innerUpperLeft = upperLeft.add(getLeftMargin(),
		-getTopMargin());

	inner.draw(pdDocument, contentStream, innerUpperLeft, drawListener);
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() throws IOException {
	return this;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
	    throws IOException {
	setInnerMaxWidthIfNecessary();

	Dividable innerDividable = null;
	if (inner instanceof Dividable) {
	    innerDividable = (Dividable) inner;
	} else {
	    innerDividable = new Cutter(inner);
	}

	if (remainingHeight - getVerticalMarginsWithBorder() <= 0) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}

	// some space left on this page for the inner element
	float spaceLeft = remainingHeight - getVerticalMarginsWithBorder();
	Divided divided = innerDividable.divide(spaceLeft, nextPageHeight
		- getVerticalMarginsWithBorder());

	Float firstHeight = getInternalHeight() == null ? null
		: remainingHeight;
	Float tailHeight = getInternalHeight() == null ? null
		: getInternalHeight() - spaceLeft;

	Frame first = new Frame(divided.getFirst(), getInternalWidth(),
		firstHeight);
	copyAllButInnerAndSizeTo(first);
	Frame tail = new Frame(divided.getTail(), getInternalWidth(),
		tailHeight);
	copyAllButInnerAndSizeTo(tail);

	return new Divided(first, tail);
    }

    public static void main(String[] args) throws Exception {
	String text1 = "{color:#0000ff}Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.";

	Document document = new Document(Constants.A6, 50, 50, 50, 50);

	// document.add(PositionControl.createSetPosition(50f, 100f));

	Paragraph paragraph1 = new Paragraph();
	paragraph1.addMarkup(text1, 11, BaseFont.Times);

	Frame box = new Frame(paragraph1, null, null);
	box.setShape(new RoundedRect(20));
	box.setBorderColor(Color.blue);
	box.setBorderStroke(new Stroke(3));
//	 box.setAbsolutePosition(new Position(50f, 200f));
	box.setMargins(20, 20, 30, 5);
	box.setBackgroundColor(Color.pink);
	document.add(box);

	final OutputStream outputStream = new FileOutputStream("box.pdf");
	document.save(outputStream);

    }

}
