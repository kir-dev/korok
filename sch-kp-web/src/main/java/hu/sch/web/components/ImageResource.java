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

import java.awt.image.BufferedImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

/**
 *
 * @author konvergal
 */
public class ImageResource extends DynamicImageResource {

    // has to save this. or get the image another way!
    private byte[] image;

    public ImageResource(byte[] image, String format) {
        this.image = image;
        setFormat(format);
    }

    public ImageResource(BufferedImage image) {
        this.image = toImageData(image);
    }

    @Override
    protected byte[] getImageData() {
        if (image != null) {
            return image;
        } else {
            return new byte[0];
        }

    }

    /**
     * 1 day!
     */
    @Override
    protected int getCacheDuration() {
        return 3600 * 24;
    }
}
