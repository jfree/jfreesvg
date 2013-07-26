/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreegraphics2d/index.html
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 * 
 */

package org.jfree.graphics2d.svg;

import java.awt.Image;

/**
 * A <code>(String, Image)</code> pair that links together a reference ID and 
 * the source image.  This is used internally by {@link SVGGraphics2D} to track
 * images as they are rendered.  This is important when images are not embedded
 * in the SVG output, in which case you may need to generate corresponding
 * image files for the images (see also {@link SVGGraphics2D#getSVGImages()}). 
 */
public final class ImageElement {
    
    /** The filename specified in the href. */
    private String href;
    
    /** The image. */
    private Image image;
    
    /**
     * Creates a new instance.
     * 
     * @param href  the href (<code>null</code> not permitted).
     * @param image  the image (<code>null</code> not permitted).
     */
    public ImageElement(String href, Image image) {
        if (href == null) {
            throw new IllegalArgumentException("Null 'href' argument.");
        }
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        this.href = href;
        this.image = image;
    }

    /**
     * Returns the reference ID that was specified in the constructor.
     * 
     * @return The href (never <code>null</code>).
     */
    public String getHref() {
        return href;
    }

    /**
     * Returns the image that was specified in the constructor.
     * 
     * @return The image (never <code>null</code>).
     */
    public Image getImage() {
        return image;
    }
 
}
