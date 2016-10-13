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

    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;

    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private float marginBottom;

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
	
	other.setPaddingBottom(this.getPaddingBottom());
	other.setPaddingLeft(this.getPaddingLeft());
	other.setPaddingRight(this.getPaddingRight());
	other.setPaddingTop(this.getPaddingTop());
	
	other.setMarginBottom(this.getMarginBottom());
	other.setMarginLeft(this.getMarginLeft());
	other.setMarginRight(this.getMarginRight());
	other.setMarginTop(this.getMarginTop());
    }

    public float getPaddingLeft() {
	return paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
	this.paddingLeft = paddingLeft;
    }

    public float getPaddingRight() {
	return paddingRight;
    }

    public void setPaddingRight(float paddingRight) {
	this.paddingRight = paddingRight;
    }

    public float getPaddingTop() {
	return paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
	this.paddingTop = paddingTop;
    }

    public float getPaddingBottom() {
	return paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
	this.paddingBottom = paddingBottom;
    }

    public void setPadding(float left, float right, float top, float bottom) {
	setPaddingLeft(left);
	setPaddingRight(right);
	setPaddingTop(top);
	setPaddingBottom(bottom);
    }

    public float getMarginLeft() {
	return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
	this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
	return marginRight;
    }

    public void setMarginRight(float marginRight) {
	this.marginRight = marginRight;
    }

    public float getMarginTop() {
	return marginTop;
    }

    public void setMarginTop(float marginTop) {
	this.marginTop = marginTop;
    }

    public float getMarginBottom() {
	return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
	this.marginBottom = marginBottom;
    }

    public void setMargin(float left, float right, float top, float bottom) {
	setMarginLeft(left);
	setMarginRight(right);
	setMarginTop(top);
	setMarginBottom(bottom);
    }

    protected float getHorizontalSpacingWithBorder() {
	return getMarginLeft() + getMarginRight() + 2 * getBorderWidth()
		+ getPaddingLeft() + getPaddingRight();
    }

    protected float getVerticalSpacingWithBorder() {
	return getMarginTop() + getMarginBottom() + 2 * getBorderWidth()
		+ getPaddingTop() + getPaddingBottom();
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
	return inner.getWidth() + getHorizontalSpacingWithBorder();
    }

    @Override
    public float getHeight() throws IOException {
	if (getInternalHeight() != null) {
	    return getInternalHeight();
	}
	return inner.getHeight() + getVerticalSpacingWithBorder();
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
			- getHorizontalSpacingWithBorder());
	    } else if (maxWidth >= 0) {
		((WidthRespecting) inner).setMaxWidth(maxWidth
			- getHorizontalSpacingWithBorder());
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
	}
	upperLeft = upperLeft.add(getMarginLeft() + halfBorderWidth,
		-getMarginTop() - halfBorderWidth);

	if (getShape() != null) {
	    float shapeWidth = getWidth() - getBorderWidth() - getMarginLeft()
		    - getMarginRight();
	    float shapeHeight = getHeight() - getBorderWidth() - getMarginTop()
		    - getMarginBottom();

	    if (getBackgroundColor() != null) {
		getShape().fill(pdDocument, contentStream, upperLeft,
			shapeWidth, shapeHeight, getBackgroundColor(),
			drawListener);
	    }
	    if (hasBorder()) {
		getShape().draw(pdDocument, contentStream, upperLeft,
			shapeWidth, shapeHeight, getBorderColor(),
			getBorderStroke(), drawListener);
	    }
	}

	Position innerUpperLeft = upperLeft.add(getPaddingLeft()
		+ halfBorderWidth, -getPaddingTop() - halfBorderWidth);

	inner.draw(pdDocument, contentStream, innerUpperLeft, drawListener);
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() throws IOException {
	inner = inner.removeLeadingEmptyVerticalSpace();
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

	if (remainingHeight - getVerticalSpacingWithBorder() <= 0) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}

	// some space left on this page for the inner element
	float spaceLeft = remainingHeight - getVerticalSpacingWithBorder();
	Divided divided = innerDividable.divide(spaceLeft, nextPageHeight
		- getVerticalSpacingWithBorder());

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
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo\n\n\n\n* "
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

	Document document = new Document(Constants.A6);

	// document.add(PositionControl.createSetPosition(50f, 100f));

	Paragraph paragraph1 = new Paragraph();
	paragraph1.addMarkup(text1, 11, BaseFont.Times);

	Frame box = new Frame(paragraph1, null, null);
	box.setShape(new RoundedRect(20));
	box.setBorderColor(Color.blue);
	box.setBorderStroke(new Stroke(3));
	// box.setAbsolutePosition(new Position(50f, 200f));
	box.setMargin(20, 20, 30, 5);
	box.setPadding(10,5,10,5);
//	box.setBackgroundColor(Color.pink);
	document.add(box);

	final OutputStream outputStream = new FileOutputStream("box.pdf");
	document.save(outputStream);

    }

}
