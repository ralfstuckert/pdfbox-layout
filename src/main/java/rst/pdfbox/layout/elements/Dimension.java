package rst.pdfbox.layout.elements;

/**
 * In order to avoid dependencies to AWT, we use our own Dimension class here.
 */
public class Dimension {

    private final float width;
    private final float height;

    public Dimension(float width, float height) {
	super();
	this.width = width;
	this.height = height;
    }

    public float getWidth() {
	return width;
    }

    public float getHeight() {
	return height;
    }

    @Override
    public String toString() {
	return "Dimension [width=" + width + ", height=" + height + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Float.floatToIntBits(height);
	result = prime * result + Float.floatToIntBits(width);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Dimension other = (Dimension) obj;
	if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
	    return false;
	if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
	    return false;
	return true;
    }

}
