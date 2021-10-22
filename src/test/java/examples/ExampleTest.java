package examples;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.WordBreakerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

public class ExampleTest {

  private File newPdf;

  @Before
  public void setUp() throws Exception {
    // reset test situation
    System.clearProperty(WordBreakerFactory.WORD_BREAKER_CLASS_PROPERTY);
  }

  @After
  public void tearDown() throws Exception {
    if (newPdf != null && newPdf.exists()) {
      newPdf.deleteOnExit();
    }
  }

  @Test
  public void testAligned() throws Exception {
    checkExample("Aligned");
  }

  @Test
  public void testColumns() throws Exception {
    checkExample("Columns");
  }

  @Test
  public void testCustomAnnotation() throws Exception {
    checkExample("CustomAnnotation");
  }

  @Test
  public void testFrames() throws Exception {
    checkExample("Frames");
  }

  @Test
  public void testHelloDoc() throws Exception {
    checkExample("HelloDoc");
  }

  @Test
  public void testIndentation() throws Exception {
    checkExample("Indentation");
  }

  @Test
  public void testLandscape() throws Exception {
    checkExample("Landscape");
  }

  @Test
  public void testLetter() throws Exception {
    checkExample("Letter");
  }

  @Test
  public void testLineSpacing() throws Exception {
    checkExample("LineSpacing");
  }

  @Test
  public void testLinks() throws Exception {
    checkExample("Links");
  }

  @Test
  public void testListener() throws Exception {
    checkExample("Listener");
  }

  @Test
  public void testLowLevelText() throws Exception {
    checkExample("LowLevelText");
  }

  @Test
  public void testMargin() throws Exception {
    checkExample("Margin");
  }

  @Test
  public void testMarkup() throws Exception {
    checkExample("Markup");
  }

  @Test
  public void testMultiplePages() throws Exception {
    checkExample("MultiplePages");
  }

  @Test
  public void testRotation() throws Exception {
    checkExample("Rotation");
  }

  public void checkExample(final String example) throws Exception {
    Class<?> exampleClass = Class.forName(example);
    Method mainMethod = exampleClass.getDeclaredMethod("main",
            String[].class);
    mainMethod.invoke(null, new Object[]{new String[0]});

    String pdfName = example.toLowerCase() + ".pdf";
    newPdf = new File("./" + pdfName);

    InputStream oldPdf = this.getClass().getResourceAsStream(
            "/examples/pdf/" + pdfName);
    assertNotNull(oldPdf);

    comparePdfs(newPdf, oldPdf);

  }

  public static BufferedImage toImage(final PDDocument document,
                                      final int pageIndex) throws IOException {
    return CompatibilityHelper
            .createImageFromPage(document, pageIndex, 175);
  }

  protected static void comparePdfs(final File newPdf, InputStream toCompareTo)
          throws IOException, AssertionError {

    try (PDDocument currentDoc = PDDocument.load(newPdf);
         PDDocument oldDoc = PDDocument.load(toCompareTo)) {

      if (currentDoc.getNumberOfPages() != oldDoc.getNumberOfPages()) {
        throw new AssertionError(String.format(
                "expected %d pages, but is %d",
                oldDoc.getNumberOfPages(),
                currentDoc.getNumberOfPages()));
      }

      for (int i = 0; i < oldDoc.getNumberOfPages(); i++) {
        BufferedImage currentPageImg = toImage(currentDoc, i);
        BufferedImage oldPageImg = toImage(oldDoc, i);
        BufferedImage diff = compareImage(currentPageImg, oldPageImg);
        if (diff != null) {
          File diffFile = new File(newPdf.getAbsoluteFile()
                  + ".diff.png");
          ImageIO.write(diff, "png", diffFile);
          throw new AssertionError(String.format(
                  "page %d different, wrote diff image %s", i + 1,
                  diffFile));
        }

      }
    }
  }

  public static BufferedImage compareImage(final BufferedImage img1,
                                           final BufferedImage img2) throws IOException {

    final double colorDistanceTolerance = 0.08;
    final int w = img1.getWidth();
    final int h = img1.getHeight();
    final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
    final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
    final BufferedImage out = new BufferedImage(w, h,
            BufferedImage.TYPE_INT_ARGB);
    boolean foundDiff = false;

    if (!(java.util.Arrays.equals(p1, p2))) {
      for (int i = 0; i < p1.length; i++) {
        if (normalizedRgbDistance(p1[i], p2[i]) > colorDistanceTolerance) {
          foundDiff = true;
          p1[i] = Color.red.getRGB();
        }
      }
      out.setRGB(0, 0, w, h, p1, 0, w);
    }

    if (foundDiff) return out;

    return null;
  }


  private static double normalizedRgbDistance(int one, int other) {
    return normalizedDistance(new Color(one), new Color(other));
  }

  private static double normalizedDistance(Color one, Color other) {
    int distanceR = one.getRed() - other.getRed();
    int distanceG = one.getGreen() - other.getGreen();
    int distanceB = one.getBlue() - other.getBlue();

    double distance = Math.sqrt((double) (distanceR * distanceR +
            distanceG * distanceG +
            distanceB * distanceB));

    return distance / MAX_VECTOR_LENGTH;
  }


  private static final double MAX_VECTOR_LENGTH = Math.sqrt(3.0 * 255.0 * 255.0);

}

