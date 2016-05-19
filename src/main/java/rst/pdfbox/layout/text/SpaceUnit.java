package rst.pdfbox.layout.text;

import java.io.IOException;

/**
 * Unit to specify space, currently only em and pt.
 */
public enum SpaceUnit {

    em, pt;
    
    /**
     * Converst the given unit to pt.
     * @param size the size with respect to the unit.
     * @param fontDescriptor the font/size to use.
     * @return the size in pt.
     * @throws IOException
     */
    public float toPt(final float size, final FontDescriptor fontDescriptor) throws IOException {
	if (this == em) {
		return fontDescriptor.getSize()
			* fontDescriptor.getFont().getAverageFontWidth() / 1000 * size;
	}
	return size;
    }
}
