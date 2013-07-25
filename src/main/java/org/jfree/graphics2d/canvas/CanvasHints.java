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

package org.jfree.graphics2d.canvas;

import java.awt.RenderingHints;

/**
 * Rendering hints that can be used with the CanvasGraphics2D implementation.
 * For the moment, there is a single hint that controls how geometrical 
 * coordinates are written (absolute or relative coordinates).
 */
public class CanvasHints {

    /**
     * The key for the hint that controls...
     */
    public static final CanvasHints.Key KEY_COORDINATES 
            = new CanvasHints.Key();
    
    /**
     * Hint value to say that ...
     */
    public static final Object VALUE_COORDINATES_ABSOLUTE 
            = "VALUE_COORDINATES_ABSOLUTE";
    
    /**
     * Hint value to say that ...
     */
    public static final Object VALUE_COORDINATES_RELATIVE 
            = "VALUE_COORDINATES_RELATIVE";
    
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
            return VALUE_COORDINATES_ABSOLUTE.equals(val)
                    || VALUE_COORDINATES_RELATIVE.equals(val);
        }
    }
    
}

