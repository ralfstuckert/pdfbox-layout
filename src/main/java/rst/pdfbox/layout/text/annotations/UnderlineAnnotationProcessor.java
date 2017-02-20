package rst.pdfbox.layout.text.annotations;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import rst.pdfbox.layout.text.DrawContext;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.StyledText;
import rst.pdfbox.layout.text.annotations.Annotations.UnderlineAnnotation;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * This annotation processor handles the {@link UnderlineAnnotation}s, and adds
 * the needed hyperlink metadata to the PDF document.
 */
public class UnderlineAnnotationProcessor implements AnnotationProcessor {

    @Override
    public void annotatedObjectDrawn(Annotated drawnObject,
	    DrawContext drawContext, Position upperLeft, float width,
	    float height) throws IOException {

	Iterable<UnderlineAnnotation> underlineAnnotations = drawnObject
		.getAnnotationsOfType(UnderlineAnnotation.class);

	if (underlineAnnotations.iterator().hasNext()) {

	    PDAnnotationTextMarkup markup = new PDAnnotationTextMarkup(
		    PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE);

	    PDRectangle bounds = new PDRectangle();
	    bounds.setLowerLeftX(upperLeft.getX());
	    bounds.setLowerLeftY(upperLeft.getY() - height);
	    bounds.setUpperRightX(upperLeft.getX() + width);
	    bounds.setUpperRightY(upperLeft.getY());
	    markup.setRectangle(bounds);
	    float[] quadPoints = CompatibilityHelper.toQuadPoints(bounds);
	    quadPoints = CompatibilityHelper.transformToPageRotation(
		    quadPoints, drawContext.getCurrentPage());
	    markup.setQuadPoints(quadPoints);

	    if (drawnObject instanceof StyledText) {
		Color color = ((StyledText) drawnObject).getColor();
		CompatibilityHelper.setAnnotationColor(markup, color);
	    }

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
