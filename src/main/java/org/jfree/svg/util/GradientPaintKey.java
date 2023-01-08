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

import java.awt.GradientPaint;

/**
 * A wrapper for a {@code GradientPaint} that can be used as the key for 
 * a {@code Map} (including a {@code HashMap}).  This class is used 
 * internally by {@code SVGGraphics2D} to track and re-use gradient 
 * definitions.  {@code GradientPaint} itself does not implement the 
 * equals() and hashCode() methods, so it doesn't make a good key for a 
 * {@code Map}.
 */
public final class GradientPaintKey {

    private final GradientPaint paint;
    
    /**
     * Creates a new instance based on the specified {@code paint}.
     * 
     * @param paint  the paint ({@code null} not permitted). 
     */
    public GradientPaintKey(GradientPaint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
    }
    
    /**
     * Returns the {@code GradientPaint} that was supplied to the 
     * constructor.
     * 
     * @return The {@code GradientPaint} (never {@code null}). 
     */
    public GradientPaint getPaint() {
        return this.paint;
    }
    
    /**
     * Checks this instance for equality with an arbitrary object.
     * 
     * @param obj  the object to test against ({@code null} permitted).
     * 
     * @return A boolean. 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GradientPaintKey)) {
            return false;
        }
        GradientPaintKey that = (GradientPaintKey) obj;
        GradientPaint thisGP = this.paint;
        GradientPaint thatGP = that.getPaint();
        if (!thisGP.getColor1().equals(thatGP.getColor1())) {
            return false;
        }
        if (!thisGP.getColor2().equals(thatGP.getColor2())) {
            return false;
        }
        if (!thisGP.getPoint1().equals(thatGP.getPoint1())) {
            return false;
        }
        if (!thisGP.getPoint2().equals(thatGP.getPoint2())) {
            return false;
        }
        if (thisGP.getTransparency() != thatGP.getTransparency()) {
            return false;
        }
        if (thisGP.isCyclic() != thatGP.isCyclic()) {
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
        hash = 47 * hash + this.paint.getPoint1().hashCode();
        hash = 47 * hash + this.paint.getPoint2().hashCode();
        hash = 47 * hash + this.paint.getColor1().hashCode();
        hash = 47 * hash + this.paint.getColor2().hashCode();
        return hash;
    }

}
