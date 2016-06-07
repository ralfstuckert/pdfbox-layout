package rst.pdfbox.layout.util;


/**
 * Container class for the default enumerators.
 */
public class Enumerators {

    /**
     * Uses arabic numbers for the enumeration, and dot as the default
     * separator. <br>
     * 
     * <pre>
     * 1. At vero eos et accusam.
     * 2. Et justo duo dolores ea rebum. 
     * 3. Stet clita ...
     * </pre>
     */
    public static class ArabicEnumerator implements Enumerator {

	private int count;

	public ArabicEnumerator() {
	    this(1);
	}

	public ArabicEnumerator(final int startCount) {
	    this.count = startCount;
	}

	@Override
	public String next() {
	    return String.valueOf(count++);
	}

	@Override
	public String getDefaultSeperator() {
	    return ".";
	}
    }

    /**
     * Uses lower case letters for the enumeration, and braces as the default
     * separator. <br>
     * 
     * <pre>
     * a) At vero eos et accusam.
     * b) Et justo duo dolores ea rebum. 
     * c) Stet clita ...
     * </pre>
     */
    public static class LowerCaseAlphabeticEnumerator extends
	    AlphabeticEnumerator {

	public LowerCaseAlphabeticEnumerator() {
	    super();
	}

	public LowerCaseAlphabeticEnumerator(final int startCount) {
	    super(startCount);
	}

	@Override
	public String next() {
	    return super.next().toLowerCase();
	}
    }

    /**
     * Uses upper case letters for the enumeration, and braces as the default
     * separator. <br>
     * 
     * <pre>
     * A) At vero eos et accusam.
     * B) Et justo duo dolores ea rebum. 
     * C) Stet clita ...
     * </pre>
     */
    public static class AlphabeticEnumerator implements Enumerator {

	final static char[] digits = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z' };

	private int count;

	public AlphabeticEnumerator() {
	    this(1);
	}

	public AlphabeticEnumerator(final int startCount) {
	    this.count = startCount;
	}

	@Override
	public String next() {
	    return toString(count++ - 1);
	}

	@Override
	public String getDefaultSeperator() {
	    return ")";
	}

	private static String toString(int i) {
	    char buf[] = new char[33];
	    int charPos = 32;

	    i = -i;

	    while (i <= -digits.length) {
		buf[charPos--] = digits[-(i % digits.length)];
		i = i / digits.length;
	    }
	    buf[charPos] = digits[-i];

	    return new String(buf, charPos, (33 - charPos));
	}

    }

    /**
     * Uses lower case roman numbers for the enumeration, and dot as the default
     * separator. <br>
     * 
     * <pre>
     *   i. At vero eos et accusam.
     *  ii. Et justo duo dolores ea rebum. 
     * iii. Stet clita ...
     * </pre>
     */
    public static class LowerCaseRomanEnumerator extends RomanEnumerator {

	public LowerCaseRomanEnumerator() {
	    super();
	}

	public LowerCaseRomanEnumerator(final int startCount) {
	    super(startCount);
	}

	@Override
	public String next() {
	    return super.next().toLowerCase();
	}
    }

    /**
     * Uses upper case roman numbers for the enumeration, and dot as the default
     * separator. <br>
     * 
     * <pre>
     *   I. At vero eos et accusam.
     *  II. Et justo duo dolores ea rebum. 
     * III. Stet clita ...
     * </pre>
     */
    public static class RomanEnumerator implements Enumerator {

	private int count;

	public RomanEnumerator() {
	    this(1);
	}

	public RomanEnumerator(final int startCount) {
	    this.count = startCount;
	}

	@Override
	public String next() {
	    return toRoman(count++);
	}

	@Override
	public String getDefaultSeperator() {
	    return ".";
	}

	private String toRoman(int input) {
	    if (input < 1 || input > 3999)
		return "Invalid Roman Number Value";
	    String s = "";
	    while (input >= 1000) {
		s += "M";
		input -= 1000;
	    }
	    while (input >= 900) {
		s += "CM";
		input -= 900;
	    }
	    while (input >= 500) {
		s += "D";
		input -= 500;
	    }
	    while (input >= 400) {
		s += "CD";
		input -= 400;
	    }
	    while (input >= 100) {
		s += "C";
		input -= 100;
	    }
	    while (input >= 90) {
		s += "XC";
		input -= 90;
	    }
	    while (input >= 50) {
		s += "L";
		input -= 50;
	    }
	    while (input >= 40) {
		s += "XL";
		input -= 40;
	    }
	    while (input >= 10) {
		s += "X";
		input -= 10;
	    }
	    while (input >= 9) {
		s += "IX";
		input -= 9;
	    }
	    while (input >= 5) {
		s += "V";
		input -= 5;
	    }
	    while (input >= 4) {
		s += "IV";
		input -= 4;
	    }
	    while (input >= 1) {
		s += "I";
		input -= 1;
	    }
	    return s;
	}
    }

}
