package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Drawable;
import rst.pdfbox.layout.elements.Element;

/**
 * The column layout divides the page vertically into columns. You can specify
 * the number of columns and the inter-column spacing. The layouting inside a
 * column is similar to the {@link VerticalLayout}. See there for more details
 * on the possiblities.
 */
public class ColumnLayout extends VerticalLayout {
    
    /**
     * Triggers flip to the next column.
     */
    public final static ControlElement NEWCOLUMN = new ControlElement("NEWCOLUMN");


    private final int columnCount;
    private float columnSpacing;
    private int columnIndex = 0;
    private Float offsetY = null;
    

    public ColumnLayout(int columnCount) {
	this(columnCount, 0);
    }

    public ColumnLayout(int columnCount, float columnSpacing) {
	this.columnCount = columnCount;
	this.columnSpacing = columnSpacing;
    }

    @Override
    protected float getTargetWidth(final RenderContext renderContext) {
	return (renderContext.getWidth() - ((columnCount - 1) * columnSpacing))
		/ columnCount;
    }

    /**
     * Flips to the next column
     */
    @Override
    protected void turnPage(final RenderContext renderContext)
	    throws IOException {
	if (++columnIndex >= columnCount) {
	    renderContext.newPage();
	    columnIndex = 0;
	    offsetY = 0f;
	} else {
	    float nextColumnX = (getTargetWidth(renderContext) + columnSpacing)
		    * columnIndex;
	    renderContext.resetPositionToUpperLeft();
	    renderContext.movePositionBy(nextColumnX, -offsetY);
	}
    }

    @Override
    public boolean render(RenderContext renderContext, Element element,
            LayoutHint layoutHint) throws IOException {
	if (element == ControlElement.NEWPAGE) {
	    renderContext.newPage();
	    return true;
	}
	if (element == NEWCOLUMN) {
	    turnPage(renderContext);
	    return true;
	}
        return super.render(renderContext, element, layoutHint);
    }
    
    @Override
    public void render(RenderContext renderContext, Drawable drawable,
            LayoutHint layoutHint) throws IOException {
	if (offsetY == null) {
	    offsetY = renderContext.getUpperLeft().getY() - renderContext.getCurrentPosition().getY();
	}
        super.render(renderContext, drawable, layoutHint);
    }
    
    @Override
    protected boolean isPositionTopOfPage(final RenderContext renderContext) {
	float topPosition = renderContext.getUpperLeft().getY();
	if (offsetY != null) { 
	    topPosition -= offsetY;
	}
	return renderContext.getCurrentPosition().getY() == topPosition;
    }

}