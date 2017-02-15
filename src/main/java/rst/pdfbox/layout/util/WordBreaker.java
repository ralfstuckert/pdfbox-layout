package rst.pdfbox.layout.util;

import java.io.IOException;

import rst.pdfbox.layout.text.FontDescriptor;

/**
 * This interface may be used to implement different strategies on how to break
 * a word, if it does not fit into a line.
 */
public interface WordBreaker {

    /**
     * Breaks the word in order to fit the given maximum width.
     * 
     * @param word
     *            the word to break.
     * @param fontDescriptor
     *            describing the font's type and size.
     * @param maxWidth
     *            the maximum width to obey.
     * @param breakHardIfNecessary
     *            indicates if the word should be broken hard to fit the width,
     *            in case there is no suitable position for breaking it
     *            adequately.
     * @return the broken word, or <code>null</code> if it cannot be broken.
     * @throws IOException by pdfbox
     */
    Pair<String> breakWord(final String word,
	    final FontDescriptor fontDescriptor, final float maxWidth,
	    final boolean breakHardIfNecessary) throws IOException;

}
