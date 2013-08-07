/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreegraphics2d/index.html
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
 * Some utility methods.
 */
public class ObjectUtils {
    
    private ObjectUtils() {
        // no need to instantiate
    }
    
    /**
     * Returns <code>true</code> if the objects are equal or both 
     * <code>null</code>, and <code>false</code> otherwise. In Java 7, we 
     * could use the <code>Objects</code> class instead.
     * 
     * @param obj1  object 1.
     * @param obj2  object 2.
     * 
     * @return A boolean. 
     */
    public static final boolean equals(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }
    
    /**
     * Returns the hash code for the object, or 0 if the object is 
     * <code>null</code>.  In Java 7, we could use the <code>Objects</code> 
     * class instead.
     * 
     * @param obj  the object (<code>null</code> permitted).
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
