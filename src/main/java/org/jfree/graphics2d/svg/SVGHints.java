/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.svg;

import java.awt.RenderingHints;

/**
 * Rendering hints that can be used with the SVGGraphics2D implementation.
 * For the moment, there is a single hint that controls how images are
 * handled (embedded in the SVG, or referenced externally).
 */
public class SVGHints {

    /**
     * The key for the hint that controls how images are supported.
     */
    public static final SVGHints.Key SVG_IMAGE_HANDLING_KEY 
            = new SVGHints.Key();
    
    public static final Object IMAGE_EMBED_PNG_DATA_VAL 
            = "IMAGE_EMBED_PNG_DATA_VAL";
    
    public static final Object IMAGE_HREF_PNG_FILE_VAL 
            = "IMAGE_HREF_PNG_FILE_VAL";
    
    /**
     * A key for hints used by the SVGGraphics2D class.
     */
    public static class Key extends RenderingHints.Key {

        private Key() {
            super(0);    
        }
    
        /**
         * Returns true for values that are compatible with this key.
         * 
         * @param val  the value.
         * 
         * @return A boolean. 
         */
        @Override
        public boolean isCompatibleValue(Object val) {
            return IMAGE_EMBED_PNG_DATA_VAL.equals(val)
                    || IMAGE_HREF_PNG_FILE_VAL.equals(val);
        }
    }
    
}
