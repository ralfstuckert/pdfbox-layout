package rst.pdfbox.layout.text;

/**
 * In order to avoid dependencies to AWT classes (e.g. Point), we have our own
 * silly implemenation of a position.
 */
public class Position {

    private final float x;
    private final float y;

    /**
     * Creates a position at the given coordinates.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public Position(float x, float y) {
	this.x = x;
	this.y = y;
    }

    /**
     * @return the x coordinate of the position.
     */
    public float getX() {
	return x;
    }

    /**
     * @return the y coordinate of the position.
     */
    public float getY() {
	return y;
    }

    /**
     * Adds an offset to the current position and returns it as a new position.
     * 
     * @param x
     *            the x offset to add.
     * @param y
     *            the y offset to add.
     * @return the new position.
     */
    public Position add(final float x, final float y) {
	return new Position(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
	return "Position [x=" + x + ", y=" + y + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Float.floatToIntBits(x);
	result = prime * result + Float.floatToIntBits(y);
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
	Position other = (Position) obj;
	if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
	    return false;
	if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
	    return false;
	return true;
    }

}
