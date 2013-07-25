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

package org.jfree.graphics2d;

/**
 * Used to indicate the position of an anchor point for a text string.  This is
 * frequently used to align a string to a fixed point in some coordinate space.
 */
public enum TextAnchor {

    /** Top/left. */
    TOP_LEFT("TextAnchor.TOP_LEFT"),

    /** Top/center. */
    TOP_CENTER("TextAnchor.TOP_CENTER"),

    /** Top/right. */
    TOP_RIGHT("TextAnchor.TOP_RIGHT"),

    /** Half-ascent/left. */
    HALF_ASCENT_LEFT("TextAnchor.HALF_ASCENT_LEFT"),

    /** Half-ascent/center. */
    HALF_ASCENT_CENTER("TextAnchor.HALF_ASCENT_CENTER"),

    /** Half-ascent/right. */
    HALF_ASCENT_RIGHT("TextAnchor.HALF_ASCENT_RIGHT"),

    /** Middle/left. */
    CENTER_LEFT("TextAnchor.CENTER_LEFT"),

    /** Middle/center. */
    CENTER("TextAnchor.CENTER"),

    /** Middle/right. */
    CENTER_RIGHT("TextAnchor.CENTER_RIGHT"),

    /** Baseline/left. */
    BASELINE_LEFT("TextAnchor.BASELINE_LEFT"),

    /** Baseline/center. */
    BASELINE_CENTER("TextAnchor.BASELINE_CENTER"),

    /** Baseline/right. */
    BASELINE_RIGHT("TextAnchor.BASELINE_RIGHT"),

    /** Bottom/left. */
    BOTTOM_LEFT("TextAnchor.BOTTOM_LEFT"),

    /** Bottom/center. */
    BOTTOM_CENTER("TextAnchor.BOTTOM_CENTER"),

    /** Bottom/right. */
    BOTTOM_RIGHT("TextAnchor.BOTTOM_RIGHT");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private TextAnchor(final String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return this.name;
    }

}