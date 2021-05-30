/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-2021, by Object Refinery Limited.  All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Color;
import java.awt.Rectangle;
import org.junit.jupiter.api.Test;

/**
 * Some general tests.
 */
public class TestGeneral {
    
    /**
     * Check that a simple SVG document does not include a DEFS element if 
     * there is no gradient paint and no user clipping.
     */
    @Test
    public void checkDefsNotRequired() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        assertFalse(g2.getSVGElement().contains("<defs>"));
    }
}
