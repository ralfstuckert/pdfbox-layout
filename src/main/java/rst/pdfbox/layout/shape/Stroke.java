package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

public class Stroke {

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

    public static class DashPattern {

	private final float[] pattern;
	private final float phase;

	public DashPattern(float onOff) {
	    this(onOff, onOff, 0f);
	}

	public DashPattern(float on, float off) {
	    this(on, off, 0f);
	}

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

    public Stroke() {
	this(1f);
    }

    public Stroke(float lineWidth) {
	this(CapStyle.Cap, JoinStyle.Miter, null, lineWidth);
    }

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

    public static StrokeBuilder builder() {
	return new StrokeBuilder();
    }

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
