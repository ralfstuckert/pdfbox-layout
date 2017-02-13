package rst.pdfbox.layout.util;

import java.io.IOException;

import rst.pdfbox.layout.text.FontDescriptor;

public class WordBreakers {

    /**
     * Dummy implementation that always returns 0. This might be used to achieve
     * the legacy behavior of not breaking words at all.
     */
    public static class LegacyDummyWordBreaker implements WordBreaker {

	@Override
	public int calculateBreakIndex(String word,
		FontDescriptor fontDescriptor, float maxWidth)
		throws IOException {
	    return -1;
	}
    }

    public static class DefaultWordBreaker implements WordBreaker {
	
	public int calculateBreakIndex(final String word,
		final FontDescriptor fontDescriptor, final float maxWidth)
		throws IOException {
	    int cutIndex = (int) (maxWidth / getEmWidth(fontDescriptor));
	    float currentWidth = 0;
	    do {
		currentWidth = getStringWidth(word.substring(0, cutIndex),
			fontDescriptor);
		--cutIndex;
	    } while (currentWidth > maxWidth);

	    return ++cutIndex;
	}

	private float getEmWidth(final FontDescriptor fontDescriptor)
		throws IOException {
	    return getStringWidth("M", fontDescriptor);
	}

	private float getStringWidth(final String text,
		final FontDescriptor fontDescriptor) throws IOException {
	    return fontDescriptor.getSize()
		    * fontDescriptor.getFont().getStringWidth(text) / 1000;
	}

    }

}
