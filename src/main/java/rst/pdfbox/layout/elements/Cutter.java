package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Coords;

/**
 * A cutter transforms any Drawable element into a {@link Dividable}. It simply
 * <em>cuts</em> the drawable vertically into pieces matching the target height.
 */
public class Cutter implements Dividable, Drawable {

	private final Drawable undividable;
	private final float viewPortY;
	private final float viewPortHeight;

	public Cutter(Drawable undividableElement) throws IOException {
		this(undividableElement, 0, undividableElement.getHeight());
	}

	protected Cutter(Drawable undividable, float viewPortY, float viewPortHeight) {
		this.undividable = undividable;
		this.viewPortY = viewPortY;
		this.viewPortHeight = viewPortHeight;
	}

	@Override
	public Divided divide(float maxHeight) {
		return new Divided(new Cutter(undividable, viewPortY, maxHeight),
				new Cutter(undividable, viewPortY - maxHeight, viewPortHeight
						- maxHeight));
	}

	@Override
	public float getWidth() throws IOException {
		return undividable.getWidth();
	}

	@Override
	public float getHeight() throws IOException {
		return viewPortHeight;
	}

	@Override
	public Coords getAbsolutePosition() {
		return null;
	}

	@Override
	public void draw(PDPageContentStream contentStream, Coords origin)
			throws IOException {
		Coords viewPortOrigin = origin.add(0, -viewPortY);
		undividable.draw(contentStream, viewPortOrigin);
	}

}
