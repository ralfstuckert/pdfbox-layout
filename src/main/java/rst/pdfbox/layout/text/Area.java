package rst.pdfbox.layout.text;

import java.io.IOException;

/**
 * Defines an area with a width and height.
 */
public interface Area {

	/**
	 * @return the width of the area.
	 * @throws IOException
	 */
	float getWidth() throws IOException;

	/**
	 * @return the height of the area.
	 * @throws IOException
	 */
	float getHeight() throws IOException;
}
