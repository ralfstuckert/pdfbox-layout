package rst.pdfbox.layout.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.WidthRespecting;
import rst.pdfbox.layout.util.CompatibilityHelper;

public class ImageElement implements Element, Drawable, Dividable,
	WidthRespecting {

    /**
     * Set this to {@link #setWidth(float)} resp. {@link #setHeight(float)}
     * (usually both) in order to respect the {@link WidthRespecting width}.
     */
    public final static float SCALE_TO_RESPECT_WIDTH = -1f;

    private BufferedImage image;
    private float width;
    private float height;
    private float maxWidth = -1;
    private Position absolutePosition;

    public ImageElement(final BufferedImage image) {
	this.image = image;
	this.width = image.getWidth();
	this.height = image.getHeight();
    }

    public ImageElement(final InputStream inputStream) throws IOException {
	this(ImageIO.read(inputStream));
    }

    public ImageElement(final String filePath) throws IOException {
	this(ImageIO.read(new File(filePath)));
    }

    @Override
    public float getWidth() throws IOException {
	if (width == SCALE_TO_RESPECT_WIDTH) {
	    if (getMaxWidth() > 0 && image.getWidth() > getMaxWidth()) {
		return getMaxWidth();
	    }
	    return image.getWidth();
	}
	return width;
    }

    /**
     * Sets the width. Default is the image width. Set to
     * {@link #SCALE_TO_RESPECT_WIDTH} in order to let the image
     * {@link WidthRespecting respect any given width}.
     * 
     * @param width
     *            the width to use.
     */
    public void setWidth(float width) {
	this.width = width;
    }

    @Override
    public float getHeight() throws IOException {
	if (height == SCALE_TO_RESPECT_WIDTH) {
	    if (getMaxWidth() > 0 && image.getWidth() > getMaxWidth()) {
		return getMaxWidth() / (float) image.getWidth()
			* (float) image.getHeight();
	    }
	    return image.getHeight();
	}
	return height;
    }

    /**
     * Sets the height. Default is the image height. Set to
     * {@link #SCALE_TO_RESPECT_WIDTH} in order to let the image
     * {@link WidthRespecting respect any given width}. Usually this makes only
     * sense if you also set the width to {@link #SCALE_TO_RESPECT_WIDTH}.
     * 
     * @param height
     *            the height to use.
     */
    public void setHeight(float height) {
	this.height = height;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
	    throws IOException {
	if (getHeight() <= nextPageHeight) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}
	return new Cutter(this).divide(remainingHeight, nextPageHeight);
    }

    @Override
    public float getMaxWidth() {
	return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
	this.maxWidth = maxWidth;
    }

    @Override
    public Position getAbsolutePosition() {
	return absolutePosition;
    }

    /**
     * Sets the absolute position to render at.
     * @param absolutePosition the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
	this.absolutePosition = absolutePosition;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft) throws IOException {
	CompatibilityHelper.drawImage(image, pdDocument, contentStream,
		upperLeft, getWidth(), getHeight());
    }

}
