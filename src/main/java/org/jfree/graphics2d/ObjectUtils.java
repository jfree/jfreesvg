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

package org.jfree.graphics2d;

/**
 * Provides a couple of static utility methods used throughout 
 * <b>JFreeSVG</b>.
 */
public final class ObjectUtils {
    
    private ObjectUtils() {
        // no need to instantiate
    }
    
    /**
     * Returns {@code true} if the objects are equal or both 
     * {@code null}, and {@code false} otherwise.
     * <br><br>
     * In Java 7, we could use methods in the {@code Objects} class 
     * instead, but for now JFreeSVG is supporting Java 6 and above.
     * 
     * @param obj1  object 1 ({@code null} permitted).
     * @param obj2  object 2 ({@code null} permitted).
     * 
     * @return A boolean. 
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }
    
    /**
     * Returns the hash code for the object, or {@code 0} if the object is 
     * {@code null}.
     * <br><br>
     * In Java 7, we could use methods in the {@code Objects} class 
     * instead, but for now JFreeSVG is supporting Java 6 and above.
     * 
     * @param obj  the object ({@code null} permitted).
     * 
     * @return The hash code or 0. 
     */
    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
}
