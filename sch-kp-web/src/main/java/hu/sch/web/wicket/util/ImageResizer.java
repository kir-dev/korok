/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.wicket.util;

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
