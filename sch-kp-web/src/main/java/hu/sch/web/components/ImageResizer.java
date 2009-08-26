/*
 *  Copyright 2008 konvergal.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.components;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 *
 * @author konvergal
 */
public class ImageResizer {

    FileUpload fileUpload;
    BufferedImage originalImage = null;
    BufferedImage resizedImage = null;
    private List<String> validImageContentTypes = Arrays.asList(new String[]{"image/jpeg", "image/png", "image/gif"});
    int maxSize;
    int scaleHints = Image.SCALE_SMOOTH;

    public ImageResizer(FileUpload fileUpload) throws Exception {
        this.fileUpload = fileUpload;

        if (!validImageContentTypes.contains(fileUpload.getContentType())) {
            throw new NotValidImageException();
        }

        InputStream is = null;
        try {
            is = fileUpload.getInputStream();
            originalImage = ImageIO.read(is);
        } catch (Exception ex) {
            throw new WicketRuntimeException(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public BufferedImage resizeImage() {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        BufferedImage bufferedImage;

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
            java.awt.Image image = originalImage.getScaledInstance(newWidth, newHeight, scaleHints);

            // convert Image to BufferedImage
            bufferedImage = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(image, 0, 0, null);
        } else {
            bufferedImage = originalImage;
        }

        this.resizedImage = bufferedImage;

        return bufferedImage;
    }

    public byte[] getByteArray() {
        try {
            // Create output stream
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Write image using any matching ImageWriter
            ImageIO.write(resizedImage, "png", out);

            // Return the image data
            return out.toByteArray();
        } catch (IOException e) {
            throw new WicketRuntimeException("Unable to convert dynamic image to stream", e);
        }
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public class NotValidImageException extends Exception {
    }
}
