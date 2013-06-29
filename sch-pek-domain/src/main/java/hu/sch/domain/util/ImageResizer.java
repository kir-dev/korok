package hu.sch.domain.util;

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

    private BufferedImage originalImage = null;
    private BufferedImage resizedImage = null;
    private int maxSize;

    public ImageResizer(byte[] image, int maxSize) throws IOException {
        this.maxSize = maxSize;

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(image);
            originalImage = ImageIO.read(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public BufferedImage resizeImage() {
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
            resizedImage = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_ARGB);
            resizedImage.createGraphics().drawImage(image, 0, 0, null);
        } else {
            resizedImage = originalImage;
        }

        return resizedImage;
    }

    public byte[] getByteArray() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", out);

        return out.toByteArray();
    }
}
