/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013, 2014, by Object Refinery Limited.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreesvg/index.html
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
 * If you do not wish to be bound by the terms of the AGPL, an alternative
 * commercial license can be purchased.  For details, please see visit the
 * JFreeSVG home page:
 * 
 * http://www.jfree.org/jfreesvg
 * 
 */

package org.jfree.graphics2d;

import java.awt.RadialGradientPaint;
import java.util.Arrays;

/**
 * A wrapper for a <code>GradientPaint</code> that can be used as the key for 
 * a <code>Map</code> (including a <code>HashMap</code>.  This class is used 
 * internally by <code>SVGGraphics2D</code> to track and re-use gradient 
 * definitions.  <code>GradientPaint</code> itself does not implement the 
 * equals() and hashCode() methods, so it doesn't make a good key for a 
 * <code>Map</code>.
 */
public class RadialGradientPaintKey {
    
    private RadialGradientPaint paint;
    
    /**
     * Creates a new instance.
     * 
     * @param rgp  the radial gradient paint (<code>null</code> not permitted).
     */
    public RadialGradientPaintKey(RadialGradientPaint rgp) {
        Args.nullNotPermitted(rgp, "mgp");
        this.paint = rgp;
    }
 
    /**
     * Returns the <code>RadialGradientPaint</code> that was supplied to the 
     * constructor.
     * 
     * @return The <code>RadialGradientPaint</code> (never <code>null</code>). 
     */
    public RadialGradientPaint getPaint() {
        return this.paint;
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     * 
     * @param obj  the object to test (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (! (obj instanceof RadialGradientPaint)) {
            return false;
        }
        RadialGradientPaint that = (RadialGradientPaint) obj;
        if (!this.paint.getCenterPoint().equals(that.getCenterPoint())) {
            return false;
        }
        if (!this.paint.getFocusPoint().equals(that.getCenterPoint())) {
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
        hash = 47 * hash + this.paint.getCenterPoint().hashCode();
        hash = 47 * hash + this.paint.getFocusPoint().hashCode();
        hash = 47 * hash + Float.floatToIntBits(this.paint.getRadius());
        hash = 47 * hash + Arrays.hashCode(this.paint.getColors());
        hash = 47 * hash + Arrays.hashCode(this.paint.getFractions());
        return hash;
    }

}
