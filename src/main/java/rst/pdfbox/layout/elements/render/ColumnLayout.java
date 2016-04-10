package rst.pdfbox.layout.elements.render;

import java.io.IOException;

/**
 * The column layout divides the page vertically into columns. You can specify
 * the number of columns and the inter-column spacing. The layouting inside a
 * column is similar to the {@link VerticalLayout}. See there for more details
 * on the possiblities.
 */
public class ColumnLayout extends VerticalLayout {

    private final int columnCount;
    private float columnSpacing;
    private int columnIndex = 0;

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

    @Override
    protected void nextPage(final RenderContext renderContext)
	    throws IOException {
	if (++columnIndex >= columnCount) {
	    renderContext.newPage();
	    columnIndex = 0;
	} else {
	    float nextColumnX = (getTargetWidth(renderContext) + columnSpacing)
		    * columnIndex;
	    renderContext.resetPositionToUpperLeft();
	    renderContext.movePositionBy(nextColumnX, 0);
	}
    }

}