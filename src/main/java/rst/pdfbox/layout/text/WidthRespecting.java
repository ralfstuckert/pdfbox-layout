package rst.pdfbox.layout.text;

/**
 * If a drawable is width-respecting, a max width may be set by layouts in order
 * fit layout constraints. It is the drawable job to do its best to match the
 * max width (e.g. word-wrap text).
 *
 */
public interface WidthRespecting {

    /**
     * @return the max width to respect.
     */
    float getMaxWidth();

    /**
     * Sets the max width to respect.
     * 
     * @param maxWidth the maximum width.
     */
    void setMaxWidth(float maxWidth);
}
