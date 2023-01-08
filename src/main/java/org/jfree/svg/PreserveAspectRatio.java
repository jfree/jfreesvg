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

/**
 * An enumeration of the values for the {@code preserveAspectRatio} attribute.
 * 
 * @since 3.2
 */
public enum PreserveAspectRatio {

    /** Value 'none'. */
    NONE("none"), 
    
    /** Value 'xMinYMin'. */
    XMIN_YMIN("xMinYMin"),
    
    /** Value 'xMinYMid'. */
    XMIN_YMID("xMinYMid"), 
    
    /** Value 'xMinYMax'. */
    XMIN_YMAX("xMinYMax"),
    
    /** Value 'xMidYMin'. */
    XMID_YMIN("xMidYMin"), 
    
    /** Value 'xMidYMid'. */
    XMID_YMID("xMidYMid"), 
    
    /** Value 'xMidYMax'. */
    XMID_YMAX("xMidYMax"),
    
    /** Value 'xMaxYMin'. */
    XMAX_YMIN("xMaxYMin"), 
    
    /** Value 'xMaxYMid'. */
    XMAX_YMID("xMaxYMid"), 
    
    /** Value 'xMaxYMax'. */
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
