/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
