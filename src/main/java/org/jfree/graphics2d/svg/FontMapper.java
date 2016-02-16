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
 * An object that can (optionally) translate one font family name to an
 * alternative.  A {@code FontMapper} is assigned to an 
 * {@link SVGGraphics2D} instance.  The default implementation will map
 * Java logical font names to the equivalent SVG generic font names.
 * 
 * @since 1.5
 */
public interface FontMapper {
    
    /**
     * Maps the specified font family name to an alternative, or else returns
     * the same family name.
     * 
     * @param family  the font family name ({@code null} not permitted).
     * 
     * @return The same font family name or an alternative (never {@code null}).
     */
    String mapFont(String family);

}
