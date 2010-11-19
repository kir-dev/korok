/**
 * Copyright (c) 2008-2010, Peter Major
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
