package rst.pdfbox.layout;

import java.awt.Color;


public interface TextFragment extends Area {
	
	FontDescriptor getFontDescriptor();

	String getText();

	Color getColor();
 }
