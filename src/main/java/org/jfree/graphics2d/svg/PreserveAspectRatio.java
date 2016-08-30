/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-2016, by Object Refinery Limited.  All rights reserved.
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

package org.jfree.graphics2d.svg;

/**
 * An enumeration of the values for the {@code preserveAspectRatio} attribute.
 * 
 * @since 3.2
 */
public enum PreserveAspectRatio {
    
    NONE("none"), 
    
    XMIN_YMIN("xMinYMin"),
    
    XMIN_YMID("xMinYMid"), 
    
    XMIN_YMAX("xMinYMax"),
    
    XMID_YMIN("xMidYMin"), 
    
    XMID_YMID("xMidYMid"), 
    
    XMID_YMAX("xMidYMax"),
    
    XMAX_YMIN("xMaxYMin"), 
    
    XMAX_YMID("xMaxYMid"), 
    
    XMAX_YMAX("xMaxYMax");

    private final String label;
    
    PreserveAspectRatio(String label) {
        this.label = label;
    }
   
    @Override
    public String toString() {
        return this.label;
    }
}
