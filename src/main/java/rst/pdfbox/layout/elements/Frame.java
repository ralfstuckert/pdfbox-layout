package rst.pdfbox.layout.elements;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.shape.Rect;
import rst.pdfbox.layout.shape.Shape;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.DrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.WidthRespecting;

/**
 * The frame is a container for a {@link Drawable}, that allows to add margin,
 * padding, border and background to the contained drawable. The size (width and
 * height) is either given, or calculated based on the dimensions of the
 * contained item. The size available for the inner element is reduced by the
 * margin, padding and border width.
 */
public class Frame implements Element, Drawable, WidthRespecting, Dividable {

    private List<Drawable> innerList = new CopyOnWriteArrayList<Drawable>();

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

    private Float givenWidth;
    private Float givenHeight;

    private Position absolutePosition;

    /**
     * Creates an empty frame.
     */
    public Frame() {
	this(null, null);
    }

    /**
     * Creates a frame containing the inner element.
     * 
     * @param inner
     *            the item to contain.
     */
    public Frame(final Drawable inner) {
	this(inner, null, null);
    }

    /**
     * Creates a frame containing the inner element, optionally constraint by
     * the given dimensions. These contraints target the border-box of the
     * frame, means: the inner element plus padding plus border width, but not
     * the margin.
     * 
     * @param inner
     *            the item to contain.
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final Drawable inner, final Float width, final Float height) {
	this(width, height);
	add(inner);
    }

    /**
     * Creates a frame constraint by the given dimensions. These contraints
     * target the border-box of the frame, means: the inner element plus padding
     * plus border width, but not the margin.
     * 
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final Float width, final Float height) {
	this.givenWidth = width;
	this.givenHeight = height;
    }

    /**
     * Adds a drawable to the frame.
     * @param drawable
     */
    public void add(final Drawable drawable) {
	innerList.add(drawable);
    }

    protected void addAll(final Collection<Drawable> drawable) {
	innerList.addAll(drawable);
    }

    /**
     * @return the shape to use as border and/or background.
     */
    public Shape getShape() {
	return shape;
    }

    /**
     * Sets the shape to use as border and/or background.
     * 
     * @param shape
     *            the shape to use.
     */
    public void setShape(Shape shape) {
	this.shape = shape;
    }

    /**
     * The stroke to use to draw the border.
     * 
     * @return the stroke to use.
     */
    public Stroke getBorderStroke() {
	return borderStroke;
    }

    /**
     * Sets the stroke to use to draw the border.
     * 
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorderStroke(Stroke borderStroke) {
	this.borderStroke = borderStroke;
    }

    /**
     * @return the widht of the {@link #getBorderStroke()} or <code>0</code>.
     */
    protected float getBorderWidth() {
	return hasBorder() ? getBorderStroke().getLineWidth() : 0;
    }

    /**
     * @return if a {@link #getShape() shape}, a {@link #getBorderStroke()
     *         stroke} and {@link #getBorderColor() color} is set.
     */
    protected boolean hasBorder() {
	return getShape() != null && getBorderStroke() != null
		&& getBorderColor() != null;
    }

    /**
     * @return the color to use to draw the border.
     */
    public Color getBorderColor() {
	return borderColor;
    }

    /**
     * Sets the color to use to draw the border.
     * 
     * @param borderColor
     *            the border color.
     */
    public void setBorderColor(Color borderColor) {
	this.borderColor = borderColor;
    }

    /**
     * Convenience method for setting both border color and stroke.
     * 
     * @param borderColor
     *            the border color.
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorder(Color borderColor, Stroke borderStroke) {
	setBorderColor(borderColor);
	setBorderStroke(borderStroke);
    }

    /**
     * @return the color to use to draw the background.
     */
    public Color getBackgroundColor() {
	return backgroundColor;
    }

    /**
     * Sets the color to use to draw the background.
     * 
     * @param backgroundColor
     *            the background color.
     */
    public void setBackgroundColor(Color backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    /**
     * Copies all attributes but the inner drawable and size to the given frame.
     * 
     * @param other
     *            the frame to copy the attributes to.
     */
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

    /**
     * @return the left padding
     */
    public float getPaddingLeft() {
	return paddingLeft;
    }

    /**
     * Sets the left padding.
     * 
     * @param paddingLeft
     *            left padding.
     */
    public void setPaddingLeft(float paddingLeft) {
	this.paddingLeft = paddingLeft;
    }

    /**
     * @return the right padding
     */
    public float getPaddingRight() {
	return paddingRight;
    }

    /**
     * Sets the right padding.
     * 
     * @param paddingRight
     *            right padding.
     */
    public void setPaddingRight(float paddingRight) {
	this.paddingRight = paddingRight;
    }

    /**
     * @return the top padding
     */
    public float getPaddingTop() {
	return paddingTop;
    }

    /**
     * Sets the top padding.
     * 
     * @param paddingTop
     *            top padding.
     */
    public void setPaddingTop(float paddingTop) {
	this.paddingTop = paddingTop;
    }

    /**
     * @return the bottom padding
     */
    public float getPaddingBottom() {
	return paddingBottom;
    }

    /**
     * Sets the bottom padding.
     * 
     * @param paddingBottom
     *            bottom padding.
     */
    public void setPaddingBottom(float paddingBottom) {
	this.paddingBottom = paddingBottom;
    }

    /**
     * Sets the padding.
     * 
     * @param left
     *            left padding.
     * @param right
     *            right padding.
     * @param top
     *            top padding.
     * @param bottom
     *            bottom padding.
     */
    public void setPadding(float left, float right, float top, float bottom) {
	setPaddingLeft(left);
	setPaddingRight(right);
	setPaddingTop(top);
	setPaddingBottom(bottom);
    }

    /**
     * @return the left margin
     */
    public float getMarginLeft() {
	return marginLeft;
    }

    /**
     * Sets the left margin.
     * 
     * @param marginLeft
     *            left margin.
     */
    public void setMarginLeft(float marginLeft) {
	this.marginLeft = marginLeft;
    }

    /**
     * @return the right margin
     */
    public float getMarginRight() {
	return marginRight;
    }

    /**
     * Sets the right margin.
     * 
     * @param marginRight
     *            right margin.
     */
    public void setMarginRight(float marginRight) {
	this.marginRight = marginRight;
    }

    /**
     * @return the top margin
     */
    public float getMarginTop() {
	return marginTop;
    }

    /**
     * Sets the top margin.
     * 
     * @param marginTop
     *            top margin.
     */
    public void setMarginTop(float marginTop) {
	this.marginTop = marginTop;
    }

    /**
     * @return the bottom margin
     */
    public float getMarginBottom() {
	return marginBottom;
    }

    /**
     * Sets the bottom margin.
     * 
     * @param marginBottom
     *            bottom margin.
     */
    public void setMarginBottom(float marginBottom) {
	this.marginBottom = marginBottom;
    }

    /**
     * Sets the margin.
     * 
     * @param left
     *            left margin.
     * @param right
     *            right margin.
     * @param top
     *            top margin.
     * @param bottom
     *            bottom margin.
     */
    public void setMargin(float left, float right, float top, float bottom) {
	setMarginLeft(left);
	setMarginRight(right);
	setMarginTop(top);
	setMarginBottom(bottom);
    }

    /**
     * @return the sum of left/right padding and border width.
     */
    protected float getHorizontalShapeSpacing() {
	return 2 * getBorderWidth() + getPaddingLeft() + getPaddingRight();
    }

    /**
     * @return the sum of top/bottom padding and border width.
     */
    protected float getVerticalShapeSpacing() {
	return 2 * getBorderWidth() + getPaddingTop() + getPaddingBottom();
    }

    /**
     * @return the sum of left/right margin, padding and border width.
     */
    protected float getHorizontalSpacing() {
	return getMarginLeft() + getMarginRight() + getHorizontalShapeSpacing();
    }

    /**
     * @return the sum of top/bottom margin, padding and border width.
     */
    protected float getVerticalSpacing() {
	return getMarginTop() + getMarginBottom() + getVerticalShapeSpacing();
    }

    /**
     * @return the height given to constrain the size of the shape.
     */
    protected Float getGivenHeight() {
	return givenHeight;
    }

    /**
     * @return the width given to constrain the size of the shape.
     */
    protected Float getGivenWidth() {
	return givenWidth;
    }

    @Override
    public float getWidth() throws IOException {
	if (getGivenWidth() != null) {
	    return getGivenWidth() + getMarginLeft() + getMarginRight();
	}
	return getMaxWidth(innerList) + getHorizontalSpacing();
    }

    protected float getMaxWidth(List<Drawable> drawableList) throws IOException {
	float max = 0;
	if (drawableList != null) {
	    for (Drawable inner : drawableList) {
		max = Math.max(max, inner.getWidth());
	    }
	}
	return max;
    }

    @Override
    public float getHeight() throws IOException {
	if (getGivenHeight() != null) {
	    return getGivenHeight() + getMarginTop() + getMarginBottom();
	}
	return getHeight(innerList) + getVerticalSpacing();
    }

    protected float getHeight(List<Drawable> drawableList) throws IOException {
	float height = 0;
	if (drawableList != null) {
	    for (Drawable inner : drawableList) {
		height += inner.getHeight();
	    }
	}
	return height;
    }

    @Override
    public Position getAbsolutePosition() throws IOException {
	return absolutePosition;
    }

    /**
     * Sets th absolute position.
     * 
     * @param absolutePosition
     *            the absolute position to use, or <code>null</code>.
     */
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

	for (Drawable inner : innerList) {
	    setMaxWidth(inner, maxWidth);
	}
    }

    private void setMaxWidth(final Drawable inner, float maxWidth) {
	if (inner instanceof WidthRespecting) {
	    if (getGivenWidth() != null) {
		((WidthRespecting) inner).setMaxWidth(getGivenWidth()
			- getHorizontalShapeSpacing());
	    } else if (maxWidth >= 0) {
		((WidthRespecting) inner).setMaxWidth(maxWidth
			- getHorizontalSpacing());
	    }
	}
    }

    /**
     * Propagates the max width to the inner item if there is a given size, but
     * no absolute position.
     * 
     * @throws IOException
     *             by pdfbox.
     */
    protected void setInnerMaxWidthIfNecessary() throws IOException {
	if (getAbsolutePosition() == null && getGivenWidth() != null) {
	    setMaxWidth(getGivenWidth() - getHorizontalShapeSpacing());
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
	    float shapeWidth = getWidth() - getMarginLeft() - getMarginRight()
		    - getBorderWidth();
	    float shapeHeight = getHeight() - getMarginTop()
		    - getMarginBottom() - getBorderWidth();

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

	for (Drawable inner : innerList) {
	    inner.draw(pdDocument, contentStream, innerUpperLeft, drawListener);
	    innerUpperLeft = innerUpperLeft.add(0, -inner.getHeight());
	}
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() throws IOException {
	if (innerList.size() > 0) {
	    Drawable drawableWithoutLeadingVerticalSpace = innerList.get(0)
		    .removeLeadingEmptyVerticalSpace();
	    innerList.set(0, drawableWithoutLeadingVerticalSpace);
	}
	return this;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
	    throws IOException {
	setInnerMaxWidthIfNecessary();

	if (remainingHeight - getVerticalSpacing() <= 0) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}

	// find first inner that does not fit on page
	float spaceLeft = remainingHeight - getVerticalSpacing();

	DividedList dividedList = divideList(innerList, spaceLeft);

	float spaceLeftForDivided = spaceLeft
		- getHeight(dividedList.getHead());
	Divided divided = null;

	if (dividedList.getDrawableToDivide() != null) {
	    Dividable innerDividable = null;
	    if (dividedList.getDrawableToDivide() instanceof Dividable) {
		innerDividable = (Dividable) dividedList.getDrawableToDivide();
	    } else {
		innerDividable = new Cutter(dividedList.getDrawableToDivide());
	    }
	    // some space left on this page for the inner element
	    divided = innerDividable.divide(spaceLeftForDivided, nextPageHeight
		    - getVerticalSpacing());
	}

	Float firstHeight = getGivenHeight() == null ? null : remainingHeight;
	Float tailHeight = getGivenHeight() == null ? null : getGivenHeight()
		- spaceLeft;

	// create head sub frame
	Frame first = new Frame(getGivenWidth(), firstHeight);
	copyAllButInnerAndSizeTo(first);
	if (dividedList.getHead() != null) {
	    first.addAll(dividedList.getHead());
	}
	if (divided != null) {
	    first.add(divided.getFirst());
	}

	// create tail sub frame
	Frame tail = new Frame(getGivenWidth(), tailHeight);
	copyAllButInnerAndSizeTo(tail);
	if (divided != null) {
	    tail.add(divided.getTail());
	}
	if (dividedList.getTail() != null) {
	    tail.addAll(dividedList.getTail());
	}

	return new Divided(first, tail);
    }

    private DividedList divideList(List<Drawable> items, float spaceLeft)
	    throws IOException {
	List<Drawable> head = null;
	List<Drawable> tail = null;
	Drawable toDivide = null;

	float tmpHeight = 0;
	int index = 0;
	while (tmpHeight < spaceLeft) {
	    tmpHeight += items.get(index).getHeight();

	    if (tmpHeight == spaceLeft) {
		// we can split between two drawables
		head = items.subList(0, index + 1);
		if (index + 1 < items.size()) {
		    tail = items.subList(index + 1, items.size());
		}
	    }

	    if (tmpHeight > spaceLeft) {
		head = items.subList(0, index);
		toDivide = items.get(index);
		if (index + 1 < items.size()) {
		    tail = items.subList(index + 1, items.size());
		}
	    }

	    ++index;
	}

	return new DividedList(head, toDivide, tail);
    }

    public static class DividedList {
	private List<Drawable> head;
	private Drawable drawableToDivide;
	private List<Drawable> tail;

	public DividedList(List<Drawable> head, Drawable drawableToDivide,
		List<Drawable> tail) {
	    this.head = head;
	    this.drawableToDivide = drawableToDivide;
	    this.tail = tail;
	}

	public List<Drawable> getHead() {
	    return head;
	}

	public Drawable getDrawableToDivide() {
	    return drawableToDivide;
	}

	public List<Drawable> getTail() {
	    return tail;
	}

    }

}
