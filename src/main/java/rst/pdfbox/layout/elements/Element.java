package rst.pdfbox.layout.elements;

import rst.pdfbox.layout.Coords;

public interface Element extends Drawable {

	float getMaxWidth();

	void setMaxWidth(float maxWidth);

}
