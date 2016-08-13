package rst.pdfbox.layout.text;


/**
 * Called if an object has been drawn.
 */
public interface DrawListener {

    /**
     * Indicates that an object has been drawn.
     * @param drawnObject the drawn object. 
     * @param upperLeft the upper left position.
     * @param width  the width of the drawn object.
     * @param height the height of the drawn object.
     */
    void drawn(Object drawnObject, Position upperLeft, float width, float height);
}
