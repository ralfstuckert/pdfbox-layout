import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.DrawContext;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.StyledText;
import rst.pdfbox.layout.text.annotations.Annotated;
import rst.pdfbox.layout.text.annotations.AnnotatedStyledText;
import rst.pdfbox.layout.text.annotations.Annotation;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacter;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.AnnotationControlCharacterFactory;
import rst.pdfbox.layout.text.annotations.AnnotationProcessor;
import rst.pdfbox.layout.text.annotations.AnnotationProcessorFactory;
import rst.pdfbox.layout.util.CompatibilityHelper;

public class CustomAnnotation {

    /**
     * Represents a highlight annotation that might be added to a
     * {@link AnnotatedStyledText}.
     */
    public static class HighlightAnnotation implements Annotation {

	private Color color;

	public HighlightAnnotation(Color color) {
	    this.color = color;
	}

	public Color getColor() {
	    return color;
	}
    }

    /**
     * Processes {@link HighlightAnnotation}s by adding a colored highlight to
     * the pdf.
     */
    public static class HighlightAnnotationProcessor implements
	    AnnotationProcessor {

	@Override
	public void annotatedObjectDrawn(Annotated drawnObject,
		DrawContext drawContext, Position upperLeft, float width,
		float height) throws IOException {

	    Iterable<HighlightAnnotation> HighlightAnnotations = drawnObject
		    .getAnnotationsOfType(HighlightAnnotation.class);

	    for (HighlightAnnotation highlightAnnotation : HighlightAnnotations) {

		// use PDF text markup to implement the highlight
		PDAnnotationTextMarkup markup = new PDAnnotationTextMarkup(
			PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);

		// use the bounding box of the drawn object to position the
		// highlight
		PDRectangle bounds = new PDRectangle();
		bounds.setLowerLeftX(upperLeft.getX());
		bounds.setLowerLeftY(upperLeft.getY() - height);
		bounds.setUpperRightX(upperLeft.getX() + width);
		bounds.setUpperRightY(upperLeft.getY() + 1);
		markup.setRectangle(bounds);
		float[] quadPoints = CompatibilityHelper.toQuadPoints(bounds);
		quadPoints = CompatibilityHelper.transformToPageRotation(
			quadPoints, drawContext.getCurrentPage());
		markup.setQuadPoints(quadPoints);

		// set the highlight color if given
		if (highlightAnnotation.getColor() != null) {
		    CompatibilityHelper.setAnnotationColor(markup, highlightAnnotation.getColor());
		}

		// finally add the markup to the PDF
		drawContext.getCurrentPage().getAnnotations().add(markup);
	    }
	}

	@Override
	public void beforePage(DrawContext drawContext) throws IOException {
	    // nothing to do here for us
	}

	@Override
	public void afterPage(DrawContext drawContext) throws IOException {
	    // nothing to do here for us
	}

	@Override
	public void afterRender(PDDocument document) throws IOException {
	    // nothing to do here for us
	}

    }

    /**
     * The control character is a representation of the parsed markup. It
     * contains any information passed by the markup necessary for rendering, in
     * our case here it is just the color for the highlight.
     */
    public static class HighlightControlCharacter extends
	    AnnotationControlCharacter<HighlightAnnotation> {

	private HighlightAnnotation annotation;

	protected HighlightControlCharacter(final Color color) {
	    super("HIGHLIGHT", HighlightControlCharacterFactory.TO_ESCAPE);
	    annotation = new HighlightAnnotation(color);
	}

	@Override
	public HighlightAnnotation getAnnotation() {
	    return annotation;
	}

	@Override
	public Class<HighlightAnnotation> getAnnotationType() {
	    return HighlightAnnotation.class;
	}
    }

    /**
     * Provides a regex pattern to match the highlight markup, and creates an
     * appropriate control character. In our case here the markup syntax is
     * either <code>{hl}</code> or with optional color information
     * <code>{hl:#ee22aa}</code>, where the color is given as hex RGB code
     * (ee22aa in this case). It can be escaped with a backslash ('\').
     */
    private static class HighlightControlCharacterFactory implements
	    AnnotationControlCharacterFactory<HighlightControlCharacter> {

	private final static Pattern PATTERN = Pattern
		.compile("(?<!\\\\)(\\\\\\\\)*\\{hl(:#(\\p{XDigit}{6}))?\\}");

	private final static String TO_ESCAPE = "{";

	@Override
	public HighlightControlCharacter createControlCharacter(String text,
		Matcher matcher, final List<CharSequence> charactersSoFar) {
	    Color color = null;
	    String hex = matcher.group(3);
	    if (hex != null) {
		int r = Integer.parseUnsignedInt(hex.substring(0, 2), 16);
		int g = Integer.parseUnsignedInt(hex.substring(2, 4), 16);
		int b = Integer.parseUnsignedInt(hex.substring(4, 6), 16);
		color = new Color(r, g, b);
	    }
	    return new HighlightControlCharacter(color);
	}

	@Override
	public Pattern getPattern() {
	    return PATTERN;
	}

	@Override
	public String unescape(String text) {
	    return text
		    .replaceAll("\\\\" + Pattern.quote(TO_ESCAPE), TO_ESCAPE);
	}

	@Override
	public boolean patternMatchesBeginOfLine() {
	    return false;
	}

    }

    public static void main(String[] args) throws Exception {

	// register our custom highlight annotation processor
	AnnotationProcessorFactory.register(HighlightAnnotationProcessor.class);

	Document document = new Document(PageFormat.with().A4()
		.margins(40, 60, 40, 60).portrait().build());

	Paragraph paragraph = new Paragraph();
	paragraph.addText("Hello there, here is ", 10, PDType1Font.HELVETICA);

	// now add some annotated text using our custom highlight annotation
	HighlightAnnotation annotation = new HighlightAnnotation(Color.red);
	AnnotatedStyledText highlightedText = new AnnotatedStyledText(
		"highlighted text", 10, PDType1Font.HELVETICA, Color.black,
		Collections.singleton(annotation));
	paragraph.add(highlightedText);

	paragraph
		.addText(
			". Do whatever you want here...strike, squiggle, whatsoever\n\n",
			10, PDType1Font.HELVETICA);
	paragraph.setMaxWidth(150);
	document.add(paragraph);

	// /////////////////////////////////////
	// and now a bit shorter using markup
	// /////////////////////////////////////

	// register markup processing for the highlight annotation
	AnnotationCharacters.register(new HighlightControlCharacterFactory());

	paragraph = new Paragraph();
	paragraph
		.addMarkup(
			"Hello there, here is {hl:#ffff00}highlighted text{hl}. "
				+ "Do whatever you want here...strike, squiggle, whatsoever\n\n",
			10, BaseFont.Helvetica);
	paragraph.setMaxWidth(150);
	document.add(paragraph);

	final OutputStream outputStream = new FileOutputStream(
		"customannotation.pdf");
	document.save(outputStream);
    }
}
