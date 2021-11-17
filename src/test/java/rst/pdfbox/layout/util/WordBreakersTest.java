package rst.pdfbox.layout.util;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Assert;
import org.junit.Test;
import rst.pdfbox.layout.text.FontDescriptor;

public class WordBreakersTest {

    @Test
    public void wordBreakerHasEmptyStringAsTheSecondPairWhenAttemptingImpossibleWrap() throws Exception {
        FontDescriptor fontDescriptor = new FontDescriptor(PDType1Font.COURIER, 5);

        WordBreaker workBreaker = WordBreakerFactory.getWorkBreaker();
        Pair<String> broken = workBreaker.breakWord("H", fontDescriptor, 1, true);

        Assert.assertNull(broken);
    }

    @Test
    public void wordBreakerHasEmptyStringAsTheSecondPairWhenTheWordFits() throws Exception {
        FontDescriptor fontDescriptor = new FontDescriptor(PDType1Font.COURIER, 5);

        WordBreaker workBreaker = WordBreakerFactory.getWorkBreaker();
        Pair<String> broken = workBreaker.breakWord("Hest", fontDescriptor, 12, true);

        Assert.assertEquals("The second entry in the pair should be the empty string", "", broken.getSecond());
    }
}