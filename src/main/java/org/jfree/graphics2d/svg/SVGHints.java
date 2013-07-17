/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.svg;

import java.awt.RenderingHints;

/**
 * Rendering hints that can be used with the SVGGraphics2D implementation.
 */
public class SVGHints {

    public static final SVGHints.Key SVG_IMAGE_HANDLING_KEY = new SVGHints.Key();
    
    public static final Object IMAGE_EMBED_PNG_DATA_VAL = "IMAGE_EMBED_PNG_DATA_VAL";
    
    public static final Object IMAGE_HREF_PNG_FILE_VAL = "IMAGE_HREF_PNG_FILE_VAL";
    
    public static class Key extends RenderingHints.Key {
        private Key() {
            super(0);    
        }
    
        @Override
        public boolean isCompatibleValue(Object val) {
            return IMAGE_EMBED_PNG_DATA_VAL.equals(val)
                    || IMAGE_HREF_PNG_FILE_VAL.equals(val);
        }
    }
    
}
