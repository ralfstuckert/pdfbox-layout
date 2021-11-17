package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Assert;
import org.junit.Test;
import rst.pdfbox.layout.elements.Paragraph;

import java.util.List;

public class TextSequenceUtilTest {
    @Test(timeout = 5_000L)
    public void wordBreakerTerminatesWhenAttemptingImpossibleWrap() throws Exception {
        String textToWrap = "N";

        Paragraph paragraph = createDummyParagraph(textToWrap);

        List<TextLine> lines = TextSequenceUtil.wordWrapToLines(paragraph, 2);
        Assert.assertEquals(1, lines.size());

        TextLine line = lines.get(0);
        Assert.assertEquals(textToWrap, line.getStyledTexts().get(0).getText());
    }

    private Paragraph createDummyParagraph(String textToWrap) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new StyledText(textToWrap, 12, PDType1Font.COURIER));
        return paragraph;
    }
}