package rst.pdfbox.layout.elements;

import java.io.IOException;

/**
 * If a drawable is marked as {@link Dividable}, it can be (vertically) divided
 * in case it does not fit on the (remaining) page.
 */
public interface Dividable {

	/**
	 * Divides the drawable vetically into pieces where the first part is to
	 * respect the given remaining height. The page height allows to make better
	 * decisions on how to divide best.
	 * 
	 * @param remainingHeight
	 *            the remaining height on the page dictating the height of the
	 *            first part.
	 * @param nextPageHeight
	 *            the height of the next page allows to make better decisions on how to
	 *            divide best, e.g. maybe the element fits completely on the next page.
	 * @return the Divided containing the first part and the tail.
	 * @throws IOException
	 */
	Divided divide(final float remainingHeight, final float nextPageHeight)
			throws IOException;

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

}
