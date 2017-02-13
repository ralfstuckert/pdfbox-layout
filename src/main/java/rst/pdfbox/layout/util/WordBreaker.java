package rst.pdfbox.layout.util;

import java.io.IOException;

import rst.pdfbox.layout.text.FontDescriptor;

/**
 * This interface may be used to implement different strategies on how to break
 * a word, if it does not fit into a line.
 */
public interface WordBreaker {

    /**
     * Calculates the index at which to break the given word, where the word
     * from character 0 to index (inclusive) must fit the given max width.
     * 
     * @param word
     *            the word to break.
     * @param fontDescriptor
     *            describing the font's type and size.
     * @param maxWidth
     *            the maximum width to obey.
     * @return the break index, where <code>-1</code> indicates that the word cannot be broke.
     * @throws IOException
     */
    int calculateBreakIndex(final String word,
	    final FontDescriptor fontDescriptor, final float maxWidth)
	    throws IOException;

}
