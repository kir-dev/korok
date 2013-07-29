package hu.sch.ejb.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author konvergal
 * @author messo
 * @since 2.4
 */
public class ImageResizer {

    public static final String IMAGE_EXT = "jpg";
    private BufferedImage originalImage = null;
    private BufferedImage resizedImage = null;
    private int maxSize;

    public ImageResizer(byte[] image, int maxSize) throws IOException {
        this.maxSize = maxSize;
        try (InputStream is = new ByteArrayInputStream(image)) {
            originalImage = ImageIO.read(is);
        }
    }

    /**
     * Resizes the image.
     *
     * @return
     */
    public ImageResizer resizeImage() {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth > maxSize || originalHeight > maxSize) {
            int newWidth;
            int newHeight;

            if (originalWidth > originalHeight) {
                newWidth = maxSize;
                newHeight = (maxSize * originalHeight) / originalWidth;
            } else {
                newWidth = (maxSize * originalWidth) / originalHeight;
                newHeight = maxSize;
            }
            Image image = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            // convert Image to BufferedImage
            resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
        } else {
            resizedImage = originalImage;
        }

        return this;
    }

    /**
     * Gets the bytes for the resized image.
     *
     * @return
     * @throws IOException
     */
    public byte[] getBytes() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, IMAGE_EXT, out);

        return out.toByteArray();
    }
}
