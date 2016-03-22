package rst.pdfbox.layout.elements;

import java.io.IOException;

public interface Dividable {

	public static class Divided {

		private final Drawable first;
		private final Drawable rest;

		public Divided(Drawable first, Drawable rest) {
			this.first = first;
			this.rest = rest;
		}

		public Drawable getFirst() {
			return first;
		}

		public Drawable getRest() {
			return rest;
		}

	}

	Divided divide(final float maxHeight) throws IOException;
}
