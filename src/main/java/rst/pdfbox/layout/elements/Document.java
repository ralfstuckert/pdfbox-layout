package rst.pdfbox.layout.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.BaseFont;
import rst.pdfbox.layout.Coords;
import rst.pdfbox.layout.PdfUtil;
import rst.pdfbox.layout.TextFlow;

public class Document {

	private final float marginLeft;
	private final float marginRight;
	private final float marginTop;
	private final float marginBottom;
	private final PDRectangle mediaBox;

	private final List<Element> elements = new ArrayList<Element>();

	public Document(PDRectangle mediaBox) {
		this(mediaBox, 0, 0, 0, 0);
	}

	public Document(PDRectangle mediaBox, float marginLeft, float marginRight,
			float marginTop, float marginBottom) {
		this.mediaBox = mediaBox;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
	}

	public void add(final Element element) {
		elements.add(element);
	}

	public void remove(final Element element) {
		elements.remove(element);
	}
	
	public float getMarginLeft() {
		return marginLeft;
	}

	public float getMarginRight() {
		return marginRight;
	}

	public float getMarginTop() {
		return marginTop;
	}

	public float getMarginBottom() {
		return marginBottom;
	}

	public PDRectangle getMediaBox() {
		return mediaBox;
	}

	public PDDocument render() throws IOException {
		PDDocument document = new PDDocument();
		RenderContext renderContext = new RenderContext(this, document);
		for (Element element : elements) {
			renderContext.draw(element);
		}
		renderContext.close();
		return document;
	}

	public void safe(final File file) throws IOException {
		try (PDDocument document = render()) {
			document.save(file);
		}
	}

	public void safe(final OutputStream output) throws IOException {
		try (PDDocument document = render()) {
			document.save(output);
		}
	}

	public static void main(String[] args) throws IOException {
		String text = "The MIT License (MIT)\n\nCopyright (c) 2016 Ralf Stuckert\n\n"
				+ "Permission is hereby granted, free of charge, to any person obtaining a "
				+ "copy of this software and associated documentation files (the _Software_), "
				+ "to deal in the Software without restriction, including without limitation "
				+ "the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or "
				+ "sell copies of the Software, and to permit persons to whom the Software is "
				+ "furnished to do so, subject to the following conditions:"
				+ "\n\n"
				+ "The above copyright notice and this permission notice shall be included "
				+ "in all copies or substantial portions of the Software."
				+ "\n\n"
				+ "*THE SOFTWARE IS PROVIDED _AS IS_, WITHOUT WARRANTY OF ANY KIND, EXPRESS "
				+ "OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, "
				+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE "
				+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, "
				+ "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN "
				+ "CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*\n";

		text += text;
		text += text;
		text += text;
		text += text;
		// text += text;
		// text += text;
		// text += text;
		// text += text;
		// text += text;

		Document document = new Document(PDRectangle.A4, 20, 40, 20, 40);
		Paragraph paragraph = new Paragraph();
		TextFlow textFlow = PdfUtil.createTextFlowFromMarkup(text, 11,
				BaseFont.Times);
		paragraph.add(textFlow);
		
		Paragraph para2 = new Paragraph();
		para2.add(PdfUtil.createTextFlowFromMarkup("Huhu", 30,
				BaseFont.Times));
		para2.setAbsolutePosition(new Coords(40, 100));
		document.add(para2);
		
		document.add(paragraph);
		
		final OutputStream outputStream = new FileOutputStream("test.pdf");
		document.safe(outputStream);
	}
}
