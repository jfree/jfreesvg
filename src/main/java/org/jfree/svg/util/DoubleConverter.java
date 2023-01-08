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
 */

package org.jfree.svg.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.DoubleFunction;

/**
 * A function that converts double values to a string representation with 
 * a maximum number of decimal places.
 */
public class DoubleConverter implements DoubleFunction<String> {

    private final DecimalFormat formatter;
    
    /**
     * Creates a new function that converts double values to strings with
     * the maximum number of decimal places as specified.
     * 
     * @param dp  the max decimal places (in the range 1 to 10). 
     */    
    public DoubleConverter(int dp) {
        Args.requireInRange(dp, "dp", 1, 10);
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.US);
        this.formatter = new DecimalFormat("0." + "##########".substring(0, dp), dfs);
    }

    /**
     * Returns a string representation of the specified value.
     * 
     * @param value  the value.
     * 
     * @return A string representation of the specified value. 
     */
    @Override
    public String apply(double value) {
        return this.formatter.format(value);
    }

}
