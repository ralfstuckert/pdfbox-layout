package rst.pdfbox.layout.elements;

/**
 * Utility class to create elements that allow the manipulation of the current
 * layout position. Use carefully.
 */
public class PositionControl extends ControlElement {

    /**
     * Use this value in {@link #createSetPosition(Float, Float)} to reset
     * either one or both coordinates to the marked position.
     */
    public static final Float MARKED_POSITION = Float.NEGATIVE_INFINITY;

    /**
     * Add this element to a document to mark the current position.
     * 
     * @return the created element
     */
    public static MarkPosition createMarkPosition() {
	return new MarkPosition();
    }

    /**
     * Add this element to a document to manipulate the current layout position.
     * If <code>null</code>, the position won't be changed (useful if you want
     * to change only X or Y). If the value is {@link #MARKED_POSITION}, it wil
     * be (re-)set to the marked position.
     * 
     * @param newX
     *            the new X position.
     * @param newY
     *            new new Y position.
     * @return the created element
     */
    public static SetPosition createSetPosition(final Float newX,
	    final Float newY) {
	return new SetPosition(newX, newY);
    }

    /**
     * Add this element to a document to manipulate the current layout position
     * by a relative amount. If <code>null</code>, the position won't be changed
     * (useful if you want to change only X or Y).
     * 
     * @param relativeX
     *            the value to change position in X direction.
     * @param relativeX
     *            the value to change position in Y direction.
     * @return the created element
     */
    public static MovePosition createMovePosition(final float relativeX,
	    final float relativeY) {
	return new MovePosition(relativeX, relativeY);
    }

    public static class MarkPosition extends PositionControl {
	private MarkPosition() {
	    super("MARK_POSITION");
	}
    }

    public static class SetPosition extends PositionControl {
	private Float newX;
	private Float newY;

	private SetPosition(final Float newX, final Float newY) {
	    super(String.format("SET_POSITION x:%f, y%f", newX, newY));
	    this.newX = newX;
	    this.newY = newY;
	}

	public Float getX() {
	    return newX;
	}

	public Float getY() {
	    return newY;
	}

    }

    public static class MovePosition extends PositionControl {
	private float relativeX;
	private float relativeY;

	private MovePosition(final float relativeX, final float relativeY) {
	    super(String.format("SET_POSITION x:%f, y%f", relativeX, relativeY));
	    this.relativeX = relativeX;
	    this.relativeY = relativeY;
	}

	public float getX() {
	    return relativeX;
	}

	public float getY() {
	    return relativeY;
	}

    }

    private PositionControl(String name) {
	super(name);
    }

}
