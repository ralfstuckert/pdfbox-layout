package rst.pdfbox.layout.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rst.pdfbox.layout.util.Enumerators.AlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.ArabianEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseAlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseRomanEnumerator;
import rst.pdfbox.layout.util.Enumerators.RomanEnumerator;

/**
 * Enumerators are created using this factory. It allows you to register and use
 * your own enumerations, if the built ins does not satisfy your needs.<br>
 * Currently supported are:
 * <table>
 * <tr><th>Name</th><th>Key</th><th>Seperator</th></tr>
 * <tr><td>Arabian</td><td align="center">1</td><td align="center">.</td></tr>
 * <tr><td>Roman</td><td align="center">I</td><td align="center">.</td></tr>
 * <tr><td>Roman Lower Case</td><td align="center">i</td><td align="center">.</td></tr>
 * <tr><td>Alphabetic</td><td align="center">A</td><td align="center">)</td></tr>
 * <tr><td>Alphabetic Lower Case</td><td align="center">a</td><td align="center">)</td></tr>
 * </table>
 */
public class EnumeratorFactory {

    private final static Map<String, Class<? extends Enumerator>> ENUMERATORS = new ConcurrentHashMap<String, Class<? extends Enumerator>>();

    static {
	register("1", ArabianEnumerator.class);
	register("I", RomanEnumerator.class);
	register("i", LowerCaseRomanEnumerator.class);
	register("A", AlphabeticEnumerator.class);
	register("a", LowerCaseAlphabeticEnumerator.class);
    }

    /**
     * Registers an Enumerator class for a given key.
     * @param key the key (character) used in markup.
     * @param enumeratorClass the enumerator class.
     */
    public static void register(final String key,
	    final Class<? extends Enumerator> enumeratorClass) {
	ENUMERATORS.put(key, enumeratorClass);
    }

    /**
     * Creates an Enumerator for the given key.
     * @param key the key of the enumerator.
     * @return the created enumerator.
     */
    public static Enumerator createEnumerator(final String key) {
	Class<? extends Enumerator> enumeratorClass = ENUMERATORS.get(key);
	if (enumeratorClass == null) {
	    throw new IllegalArgumentException("no enumerator found for '"
		    + key + "'");
	}
	try {
	    return enumeratorClass.newInstance();
	} catch (Exception e) {
	    throw new RuntimeException("failed to create enumerator", e);
	}
    }
}
