/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
 * An image reference.  This object provides the name (href) used in the
 * SVG element to refer to an external image, and the Image itself.
 */
public class ImageElement {
    
    /** The filename specified in the href. */
    private String href;
    
    /** The image. */
    private Image image;
    
    /**
     * Creates a new instance.
     * 
     * @param href  the href.
     * @param image  the image.
     */
    public ImageElement(String href, Image image) {
        this.href = href;
        this.image = image;
    }

    /**
     * Returns the href.
     * 
     * @return The href.
     */
    public String getHref() {
        return href;
    }

    /**
     * Returns the image.
     * 
     * @return The image. 
     */
    public Image getImage() {
        return image;
    }
 
}
