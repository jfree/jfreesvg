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

package org.jfree.svg.util;

import java.awt.LinearGradientPaint;
import java.util.Arrays;

/**
 * A wrapper for a {@code LinearGradientPaint} that can be used as the key 
 * for a {@code Map} (including a {@code HashMap}).  This class is 
 * used internally by {@code SVGGraphics2D} to track and re-use gradient 
 * definitions.  {@code LinearGradientPaint} itself does not implement the 
 * {@code equals()} and {@code hashCode()} methods, so it doesn't make a good 
 * key for a {@code Map}.
 * 
 * @since 1.9
 */
public class LinearGradientPaintKey {
    
    private final LinearGradientPaint paint;
    
    /**
     * Creates a new instance.
     * 
     * @param lgp  the linear gradient paint ({@code null} not permitted).
     */
    public LinearGradientPaintKey(LinearGradientPaint lgp) {
        Args.nullNotPermitted(lgp, "lgp");
        this.paint = lgp;
    }
 
    /**
     * Returns the {@code LinearGradientPaint} that was supplied to the 
     * constructor.
     * 
     * @return The {@code LinearGradientPaint} (never {@code null}). 
     */
    public LinearGradientPaint getPaint() {
        return this.paint;
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     * 
     * @param obj  the object to test ({@code null} permitted).
     * 
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (! (obj instanceof LinearGradientPaint)) {
            return false;
        }
        LinearGradientPaint that = (LinearGradientPaint) obj;
        if (!this.paint.getStartPoint().equals(that.getStartPoint())) {
            return false;
        }
        if (!this.paint.getEndPoint().equals(that.getEndPoint())) {
            return false;
        }
        if (!Arrays.equals(this.paint.getColors(), that.getColors())) {
            return false;
        }
        if (!Arrays.equals(this.paint.getFractions(), that.getFractions())) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a hash code for this instance.
     * 
     * @return A hash code. 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.paint.getStartPoint().hashCode();
        hash = 47 * hash + this.paint.getEndPoint().hashCode();
        hash = 47 * hash + Arrays.hashCode(this.paint.getColors());
        hash = 47 * hash + Arrays.hashCode(this.paint.getFractions());
        return hash;
    }

}
