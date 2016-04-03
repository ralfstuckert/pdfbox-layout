package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class Constants {

    private static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;
    private static final float MM_TO_UNITS = 1 / (10 * 2.54f)
	    * DEFAULT_USER_SPACE_UNIT_DPI;

    public static final PDRectangle A0 = new PDRectangle(841 * MM_TO_UNITS,
	    1189 * MM_TO_UNITS);
    public static final PDRectangle A1 = new PDRectangle(594 * MM_TO_UNITS,
	    841 * MM_TO_UNITS);
    public static final PDRectangle A2 = new PDRectangle(420 * MM_TO_UNITS,
	    594 * MM_TO_UNITS);
    public static final PDRectangle A3 = new PDRectangle(297 * MM_TO_UNITS,
	    420 * MM_TO_UNITS);
    public static final PDRectangle A4 = new PDRectangle(210 * MM_TO_UNITS,
	    297 * MM_TO_UNITS);
    public static final PDRectangle A5 = new PDRectangle(148 * MM_TO_UNITS,
	    210 * MM_TO_UNITS);
    public static final PDRectangle A6 = new PDRectangle(105 * MM_TO_UNITS,
	    148 * MM_TO_UNITS);
}
