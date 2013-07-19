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
     * The key for the hint that controls whether images are embedded in the
     * SVG or referenced externally.
     */
    public static final SVGHints.Key KEY_IMAGE_HANDLING 
            = new SVGHints.Key();
    
    /**
     * Hint value to say that images should be embedded in the SVG output
     * using PNG data Base64 encoded.
     */
    public static final Object VALUE_IMAGE_HANDLING_EMBED 
            = "VALUE_IMAGE_HANDLING_EMBED";
    
    /**
     * Hint value to say that images should be referenced externally.
     */
    public static final Object VALUE_IMAGE_HANDLING_REFERENCE 
            = "VALUE_IMAGE_HANDLING_REFERENCE";
    
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
            return VALUE_IMAGE_HANDLING_EMBED.equals(val)
                    || VALUE_IMAGE_HANDLING_REFERENCE.equals(val);
        }
    }
    
}
