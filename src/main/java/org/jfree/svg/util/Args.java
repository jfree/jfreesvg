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

/**
 * A utility class that performs checks for method argument validity.
 */
public class Args {

    private Args() {
        // no need to instantiate this
    }
 
    /**
     * Checks that an argument is non-{@code null} and throws an 
     * {@code IllegalArgumentException} otherwise.
     * 
     * @param obj  the object to check for {@code null}.
     * @param ref  the text name for the parameter (to include in the exception
     *     message).
     */
    public static void nullNotPermitted(Object obj, String ref) {
        if (obj == null) {
            throw new IllegalArgumentException("Null '" + ref + "' argument.");
        }
    }

    /**
     * Checks that the value falls within the specified range and, if it does
     * not, throws an {@code IllegalArgumentException}.
     * 
     * @param value  the value.
     * @param name  the parameter name.
     * @param lowerBound  the lower bound of the permitted range.
     * @param upperBound  the upper bound fo the permitted range.
     */
    public static void requireInRange(int value, String name, int lowerBound, 
            int upperBound) {
        if (value < lowerBound || value > upperBound) {
            throw new IllegalArgumentException("Require '" + name + "' (" 
                    + value + ") to be in the range " + lowerBound + " to " 
                    + upperBound);
        }
    }

    /** 
     * Checks that an argument is a finite positive value and throws an 
     * {@code IllegalArgumentException} otherwise.
     * 
     * @param d  the value to check.
     * @param ref  the text name for the parameter (to include in the exception
     *     message).
     */
    public static void requireFinitePositive(double d, String ref) {
        if (d <= 0.0) {
            throw new IllegalArgumentException("Require positive value for '" 
                    + ref + "' argument.");
        }
        if (!Double.isFinite(d)) {
            throw new IllegalArgumentException("Require finite value for '" 
                    + ref + "' argument.");
        }
    }
    
    /**
     * Checks an array to ensure it has the correct length and throws an
     * {@code IllegalArgumentException} if it does not.
     * 
     * @param length  the required length.
     * @param array  the array to check.
     * @param ref  the text name of the array parameter (to include in the 
     *     exception message).
     */
    public static void arrayMustHaveLength(int length, boolean[] array, 
            String ref) {
        nullNotPermitted(array, "array");
        if (array.length != length) {
            throw new IllegalArgumentException("Array '" + ref 
                    + "' requires length " + length);
        }
    }

    /**
     * Checks an array to ensure it has the correct length and throws an
     * {@code IllegalArgumentException} if it does not.
     * 
     * @param length  the required length.
     * @param array  the array to check ({@code null} not permitted).
     * @param ref  the text name of the array parameter (to include in the 
     *     exception message).
     */
    public static void arrayMustHaveLength(int length, double[] array, 
            String ref) {
        nullNotPermitted(array, "array");
        if (array.length != length) {
            throw new IllegalArgumentException("Array '" + ref 
                    + "' requires length " + length);
        }
    }
}
