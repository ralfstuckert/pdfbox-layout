package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.Cutter;
import rst.pdfbox.layout.elements.Dividable;
import rst.pdfbox.layout.elements.Dividable.Divided;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Drawable;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.Coords;
import rst.pdfbox.layout.text.WidthRespecting;

public class VerticalLayout implements Layout {

	@Override
	public void render(final RenderContext renderContext, Drawable drawable,
			final LayoutHint layoutHint) throws IOException {
		if (drawable.getAbsolutePosition() != null) {
			drawAbsolute(renderContext, drawable, layoutHint,
					drawable.getAbsolutePosition());
		} else {
			drawReleative(renderContext, drawable, layoutHint);
		}
	}

	protected void drawAbsolute(final RenderContext renderContext,
			Drawable drawable, final LayoutHint layoutHint, final Coords coords)
			throws IOException {
		PDPageContentStream contentStream = renderContext.getContentStream();
		drawable.draw(contentStream, coords);
	}

	protected void drawReleative(final RenderContext renderContext,
			Drawable drawable, final LayoutHint layoutHint) throws IOException {
		VerticalLayoutHint verticalLayoutHint = null;
		if (layoutHint instanceof VerticalLayoutHint) {
			verticalLayoutHint = (VerticalLayoutHint) layoutHint;
			if (verticalLayoutHint.getMarginTop() > 0) {
				drawReleativePart(renderContext, new VerticalSpacer(
						verticalLayoutHint.getMarginTop()), verticalLayoutHint);
			}
		}

		drawReleativePart(renderContext, drawable, verticalLayoutHint);

		if (verticalLayoutHint != null) {
			if (verticalLayoutHint.getMarginBottom() > 0) {
				drawReleativePart(renderContext, new VerticalSpacer(
						verticalLayoutHint.getMarginBottom()),
						verticalLayoutHint);
			}
		}
	}

	protected void drawReleativePart(final RenderContext renderContext,
			Drawable drawable, final LayoutHint layoutHint) throws IOException {

		float targetWidth = renderContext.getWidth();
		VerticalLayoutHint verticalLayoutHint = null;
		if (layoutHint instanceof VerticalLayoutHint) {
			verticalLayoutHint = (VerticalLayoutHint) layoutHint;
			targetWidth -= verticalLayoutHint.getMarginLeft();
			targetWidth -= verticalLayoutHint.getMarginRight();
		}
		
		float oldMaxWidth = -1;
		if (drawable instanceof WidthRespecting) {
			WidthRespecting flowing = (WidthRespecting) drawable;
			oldMaxWidth = flowing.getMaxWidth();
			if (oldMaxWidth < 0) {
				flowing.setMaxWidth(targetWidth);
			}
		}

		Drawable drawablePart = drawable;
		while (renderContext.getRemainingHeight() < drawablePart.getHeight()) {
			Dividable dividable = null;
			if (drawablePart instanceof Dividable) {
				dividable = (Dividable) drawablePart;
			} else {
				dividable = new Cutter(drawablePart);
			}
			Divided divided = dividable.divide(renderContext
					.getRemainingHeight());
			drawReletivePartAndMovePosition(renderContext, divided.getFirst(),
					layoutHint);

			// new page
			renderContext.newPage();

			drawablePart = divided.getTail();
		}

		drawReletivePartAndMovePosition(renderContext, drawablePart, layoutHint);

		if (drawable instanceof WidthRespecting) {
			if (oldMaxWidth < 0) {
				((WidthRespecting) drawable).setMaxWidth(oldMaxWidth);
			}
		}
	}

	protected void drawReletivePartAndMovePosition(
			final RenderContext renderContext, Drawable drawable,
			final LayoutHint layoutHint) throws IOException {
		PDPageContentStream contentStream = renderContext.getContentStream();
		Document document = renderContext.getDocument();
		float offsetX = 0;
		if (layoutHint instanceof VerticalLayoutHint) {
			VerticalLayoutHint verticalLayoutHint = (VerticalLayoutHint)layoutHint;
			Alignment alignment = verticalLayoutHint
					.getAlignment();
			float horizontalExtraSpace = renderContext.getWidth()
					- drawable.getWidth();
			switch (alignment) {
			case Right:
				offsetX = horizontalExtraSpace - verticalLayoutHint.getMarginRight();
				break;
			case Center:
				offsetX = horizontalExtraSpace / 2f;
				break;
			default:
				offsetX = verticalLayoutHint.getMarginLeft();
				break;
			}
		}

		contentStream.saveGraphicsState();
		contentStream.addRect(document.getMarginLeft(),
				document.getMarginBottom(), renderContext.getWidth(),
				renderContext.getHeight());
		contentStream.clip();

		drawable.draw(contentStream,
				renderContext.getCurrentPosition().add(offsetX, 0));

		contentStream.restoreGraphicsState();

		renderContext.movePositionBy(0, -drawable.getHeight());
	}
}
