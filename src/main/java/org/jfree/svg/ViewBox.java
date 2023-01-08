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

import java.util.function.DoubleFunction;

/**
 * Represents a view box in SVG.
 * 
 * @since 3.2
 */
public class ViewBox {

    private final double minX;
    
    private final double minY;
    
    private final double width;
    
    private final double height;
    
    /**
     * Creates a new instance with the specified dimensions.
     * 
     * @param minX  the x coordinate.
     * @param minY  the y coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    public ViewBox(double minX, double minY, double width, double height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns a string containing the view box coordinates and dimensions.
     *
     * @param df  the converter function ({@code null} not permitted).
     *
     * @return A string containing the view box coordinates and dimensions. 
     */
    public String valueStr(DoubleFunction<String> df) {
        return new StringBuilder().append(df.apply(this.minX)).append(' ')
                .append(df.apply(this.minY)).append(' ')
                .append(df.apply(this.width)).append(' ')
                .append(df.apply(this.height)).toString();
    }
            
}
