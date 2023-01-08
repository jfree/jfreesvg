/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-present, by David Gilbert.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreesvg/index.html
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 * 
 * If you do not wish to be bound by the terms of the GPL, an alternative
 * commercial license can be purchased.  For details, please see visit the
 * JFreeSVG home page:
 * 
 * http://www.jfree.org/jfreesvg
 * 
 */

package org.jfree.svg;

import java.awt.Image;
import org.jfree.svg.util.Args;

/**
 * A {@code (String, Image)} pair that links together a reference ID and 
 * the source image.  This is used internally by {@link SVGGraphics2D} to track
 * images as they are rendered.  This is important when images are not embedded
 * in the SVG output, in which case you may need to generate corresponding
 * image files for the images (see also {@link SVGGraphics2D#getSVGImages()}). 
 */
public final class ImageElement {
    
    /** The filename specified in the href. */
    private final String href;
    
    /** The image. */
    private final Image image;
    
    /**
     * Creates a new instance.
     * 
     * @param href  the href ({@code null} not permitted).
     * @param image  the image ({@code null} not permitted).
     */
    public ImageElement(String href, Image image) {
        Args.nullNotPermitted(href, "href");
        Args.nullNotPermitted(image, "image");
        this.href = href;
        this.image = image;
    }

    /**
     * Returns the reference ID that was specified in the constructor.
     * 
     * @return The href (never {@code null}).
     */
    public String getHref() {
        return href;
    }

    /**
     * Returns the image that was specified in the constructor.
     * 
     * @return The image (never {@code null}).
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * Returns a string representation of this object, primarily for debugging
     * purposes.
     * 
     * @return A string. 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ImageElement[");
        sb.append(this.href).append(", ").append(this.image);
        sb.append("]");
        return sb.toString();
    }
 
}
