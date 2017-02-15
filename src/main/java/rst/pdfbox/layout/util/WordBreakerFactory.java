package rst.pdfbox.layout.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rst.pdfbox.layout.util.WordBreakers.DefaultWordBreaker;
import rst.pdfbox.layout.util.WordBreakers.NonBreakingWordBreaker;

/**
 * Factory for creating a {@link WordBreaker}. This may be used to define a
 * custom strategy for breaking words. By default the {@link DefaultWordBreaker}
 * is used. Another predefined word breaker is the
 * {@link NonBreakingWordBreaker} which may be used to get the legacy behavior.
 * To switch to a different word breaker, just set the system property
 * {@link #WORD_BREAKER_CLASS_PROPERTY pdfbox.layout.word.breaker} to the class
 * name of the breaker to use.
 */
public class WordBreakerFactory {

    /**
     * constant for the system property <code>pdfbox.layout.word.breaker</code>.
     */
    public final static String WORD_BREAKER_CLASS_PROPERTY = "pdfbox.layout.word.breaker";
    
    /**
     * class name of the default word breaker.
     */
    public final static String DEFAULT_WORD_BREAKER_CLASS_NAME = DefaultWordBreaker.class
	    .getName();
    
    /**
     * class name of the (legacy) non-breaking word breaker.
     */
    public final static String LEGACY_WORD_BREAKER_CLASS_NAME = NonBreakingWordBreaker.class
	    .getName();

    private final static WordBreaker DEFAULT_WORD_BREAKER = new DefaultWordBreaker();
    private final static Map<String, WordBreaker> WORD_BREAKERS = new ConcurrentHashMap<String, WordBreaker>();

    /**
     * @return the word breaker instance to use.
     */
    public static WordBreaker getWorkBreaker() {
	return getWorkBreaker(System.getProperty(WORD_BREAKER_CLASS_PROPERTY));
    }

    private static WordBreaker getWorkBreaker(String className) {
	if (className == null) {
	    return DEFAULT_WORD_BREAKER;
	}
	WordBreaker wordBreaker = WORD_BREAKERS.get(className);
	if (wordBreaker == null) {
	    wordBreaker = createWordBreakerInstance(className);
	    WORD_BREAKERS.put(className, wordBreaker);
	}
	return wordBreaker;
    }

    private static WordBreaker createWordBreakerInstance(final String className) {
	try {
	    return (WordBreaker) Class.forName(className).newInstance();
	} catch (Exception e) {
	    throw new RuntimeException(String.format(
		    "failed to create word breaker '%s'", className), e);
	}

    }

}
