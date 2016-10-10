package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

/**
 * This is a container for all information needed to perform a stroke.
 */
public class Stroke {

    /**
     * Enum for the PDF cap styles.
     */
    public static enum CapStyle {

	Cap(0), RoundCap(1), Square(2);

	private final int value;

	private CapStyle(final int value) {
	    this.value = value;
	}

	public int value() {
	    return value;
	}
    }

    /**
     * Enum for the PDF join styles.
     */
    public static enum JoinStyle {

	Miter(0), Round(1), Bevel(2);

	private final int value;

	private JoinStyle(final int value) {
	    this.value = value;
	}

	public int value() {
	    return value;
	}
    }

    /**
     * Describes a PDF dash pattern. See the PDF documentation for more
     * information on that.
     */
    public static class DashPattern {

	private final float[] pattern;
	private final float phase;

	/**
	 * Creates a pattern with equal on and off length, starting with phase
	 * 0.
	 * 
	 * @param onOff
	 *            the length of the on/off part.
	 */
	public DashPattern(float onOff) {
	    this(onOff, onOff, 0f);
	}

	/**
	 * Creates a pattern with different on and off length, starting with
	 * phase 0.
	 * 
	 * @param on
	 *            the length of the off part.
	 * @param off
	 *            the length of the off part.
	 */
	public DashPattern(float on, float off) {
	    this(on, off, 0f);
	}

	/**
	 * Creates a pattern with different on and off length, starting with the
	 * given phase .
	 * 
	 * @param on
	 *            the length of the off part.
	 * @param off
	 *            the length of the off part.
	 * @param phase
	 *            the phase to start the pattern with.
	 */
	public DashPattern(float on, float off, float phase) {
	    this.pattern = new float[] { on, off };
	    this.phase = phase;
	}

	public float getOn() {
	    return pattern[0];
	}

	public float getOff() {
	    return pattern[1];
	}

	public float[] getPattern() {
	    return pattern;
	}

	public float getPhase() {
	    return phase;
	}

    }

    private final CapStyle capStyle;
    private final JoinStyle joinStyle;
    private final DashPattern dashPattern;
    private final float lineWidth;

    /**
     * Creates a Stroke with line width 1, cap style
     * {@link CapStyle#Cap}, join style {@link JoinStyle#Miter}, and no dash
     * pattern.
     */
    public Stroke() {
	this(1f);
    }

    /**
     * Creates a Stroke with the given line width, cap style
     * {@link CapStyle#Cap}, join style {@link JoinStyle#Miter}, and no dash
     * pattern.
     * 
     * @param lineWidth the line width.
     */
    public Stroke(float lineWidth) {
	this(CapStyle.Cap, JoinStyle.Miter, null, lineWidth);
    }

    /**
     * Creates a stroke with the given attributes.
     * @param capStyle the cap style.
     * @param joinStyle the join style.
     * @param dashPattern the dash pattern.
     * @param lineWidth the line width.
     */
    public Stroke(CapStyle capStyle, JoinStyle joinStyle,
	    DashPattern dashPattern, float lineWidth) {
	this.capStyle = capStyle;
	this.joinStyle = joinStyle;
	this.dashPattern = dashPattern;
	this.lineWidth = lineWidth;
    }

    public CapStyle getCapStyle() {
	return capStyle;
    }

    public JoinStyle getJoinStyle() {
	return joinStyle;
    }

    public DashPattern getDashPattern() {
	return dashPattern;
    }

    public float getLineWidth() {
	return lineWidth;
    }

    /**
     * Applies this stroke to the given content stream.
     * @param contentStream the content stream to apply this stroke to.
     * @throws IOException by PDFBox.
     */
    public void applyTo(final PDPageContentStream contentStream)
	    throws IOException {
	if (getCapStyle() != null) {
	    contentStream.setLineCapStyle(getCapStyle().value());
	}
	if (getJoinStyle() != null) {
	    contentStream.setLineJoinStyle(getJoinStyle().value());
	}
	if (getDashPattern() != null) {
	    contentStream.setLineDashPattern(getDashPattern().getPattern(),
		    getDashPattern().getPhase());
	}
	contentStream.setLineWidth(getLineWidth());
    }

    /**
     * Creates a stroke builder providing a fluent interface for creating a stroke.
     * @return a stroke builder.
     */
    public static StrokeBuilder builder() {
	return new StrokeBuilder();
    }

    /**
     * A builder providing a fluent interface for creating a stroke.
     */
    public static class StrokeBuilder {
	private CapStyle capStyle = CapStyle.Cap;
	private JoinStyle joinStyle = JoinStyle.Miter;
	private DashPattern dashPattern;
	float lineWidth = 1f;

	public StrokeBuilder capStyle(CapStyle capStyle) {
	    this.capStyle = capStyle;
	    return this;
	}

	public StrokeBuilder joinStyle(JoinStyle joinStyle) {
	    this.joinStyle = joinStyle;
	    return this;
	}

	public StrokeBuilder dashPattern(DashPattern dashPattern) {
	    this.dashPattern = dashPattern;
	    return this;
	}

	public StrokeBuilder lineWidth(float lineWidth) {
	    this.lineWidth = lineWidth;
	    return this;
	}

	public Stroke build() {
	    return new Stroke(capStyle, joinStyle, dashPattern, lineWidth);
	}
    }
}
