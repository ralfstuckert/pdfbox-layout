package rst.pdfbox.layout.elements;

import java.io.IOException;

/**
 * If a drawable is marked as {@link Dividable}, it can be (vertically) divided
 * in case it does not fit on the (remaining) page.
 */
public interface Dividable {

	/**
	 * A container for the result of a {@link Dividable#divide(float)}
	 * operation.
	 */
	public static class Divided {

		private final Drawable first;
		private final Drawable tail;

		public Divided(Drawable first, Drawable tail) {
			this.first = first;
			this.tail = tail;
		}

		public Drawable getFirst() {
			return first;
		}

		public Drawable getTail() {
			return tail;
		}

	}

	/**
	 * Divides the drawable vetically into pieces where the first part is to
	 * respect the given max height.
	 * 
	 * @param maxHeight
	 *            the max height of the first part.
	 * @return the Divided containing the first part and the tail.
	 * @throws IOException
	 */
	Divided divide(final float maxHeight) throws IOException;
}
