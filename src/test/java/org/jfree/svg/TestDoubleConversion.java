/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-2022, by David Gilbert.  All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import java.util.function.DoubleFunction;

/**
 * A range of tests to sanity check the double to string conversions performed
 * by JFreeSVG.
 */
public class TestDoubleConversion {
    
    /** Default conversion in Java standard library. */
    private final DoubleFunction<String> d2s = Double::toString;
    
    /** Fast 'Ryu' algorithm. */
    private final DoubleFunction<String> ryu = SVGUtils::doubleToString;
    
    /** Uses DecimalFormat to limit output to max 2dp. */
    private final DoubleFunction<String> dp2 = SVGUtils.createDoubleConverter(2);

    /** Uses DecimalFormat to limit output to max 6dp. */
    private final DoubleFunction<String> dp6 = SVGUtils.createDoubleConverter(6);
    
    @Test
    public void testGeneral() {
        double value = 1;
        assertEquals("1.0", d2s.apply(value));
        assertEquals("1.0", ryu.apply(value));
        assertEquals("1", dp2.apply(value));
        assertEquals("1", dp6.apply(value));

        value = -1;
        assertEquals("-1.0", d2s.apply(value));
        assertEquals("-1.0", ryu.apply(value));
        assertEquals("-1", dp2.apply(value));
        assertEquals("-1", dp6.apply(value));

        value = 1234.5678912345;
        assertEquals("1234.5678912345", d2s.apply(value));
        assertEquals("1234.5678912345", ryu.apply(value));
        assertEquals("1234.57", dp2.apply(value));
        assertEquals("1234.567891", dp6.apply(value));

        value = -1234.5678912345;
        assertEquals("-1234.5678912345", d2s.apply(value));
        assertEquals("-1234.5678912345", ryu.apply(value));
        assertEquals("-1234.57", dp2.apply(value));
        assertEquals("-1234.567891", dp6.apply(value));

        value = 1500;
        assertEquals("1500.0", d2s.apply(value));
        assertEquals("1500.0", ryu.apply(value));
        assertEquals("1500", dp2.apply(value));
        assertEquals("1500", dp6.apply(value));

        value = -1500;
        assertEquals("-1500.0", d2s.apply(value));
        assertEquals("-1500.0", ryu.apply(value));
        assertEquals("-1500", dp2.apply(value));
        assertEquals("-1500", dp6.apply(value));
    }

    @Test
    public void testNaN() {
        double value = Double.NaN;
        assertEquals("NaN", d2s.apply(value));
        assertEquals("NaN", ryu.apply(value));
        assertEquals("NaN", dp2.apply(value));
        assertEquals("NaN", dp6.apply(value));
    }

    @Test
    public void testPositiveInfinity() {
        double value = Double.POSITIVE_INFINITY;
        assertEquals("Infinity", d2s.apply(value));
        assertEquals("Infinity", ryu.apply(value));
        assertEquals("∞", dp2.apply(value));
        assertEquals("∞", dp6.apply(value));
    }
    
    @Test
    public void testNegativeInfinity() {
        double value = Double.NEGATIVE_INFINITY;
        assertEquals("-Infinity", d2s.apply(value));
        assertEquals("-Infinity", ryu.apply(value));
        assertEquals("-∞", dp2.apply(value));
        assertEquals("-∞", dp6.apply(value));
    }
    
    @Test
    public void testMaxValue() {
        double value = Double.MAX_VALUE;
        assertEquals("1.7976931348623157E308", d2s.apply(value));
        assertEquals("1.7976931348623157E308", ryu.apply(value));
        assertEquals("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", dp2.apply(value));
        assertEquals("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", dp6.apply(value));
    }

    @Test
    public void testMinValue() {
        double value = Double.MIN_VALUE;
        assertEquals("4.9E-324", d2s.apply(value));
        assertEquals("4.9E-324", ryu.apply(value));
        assertEquals("0", dp2.apply(value));
        assertEquals("0", dp6.apply(value));
    }

    @Test
    public void testDP() {
        double value = 0.1234567898765;
        assertEquals("0.1234567898765", d2s.apply(value));
        assertEquals("0.1234567898765", ryu.apply(value));
        assertEquals("0.12", dp2.apply(value));
        assertEquals("0.123457", dp6.apply(value));
        
        value = -0.1234567898765;
        assertEquals("-0.1234567898765", d2s.apply(value));
        assertEquals("-0.1234567898765", ryu.apply(value));
        assertEquals("-0.12", dp2.apply(value));
        assertEquals("-0.123457", dp6.apply(value));
    }
    
    @Test
    public void test10DP() {
        DoubleFunction<String> dp10 = SVGUtils.createDoubleConverter(10);
        double value = 0.1234567898765;
        assertEquals("0.1234567899", dp10.apply(value));
        
        value = -0.1234567898765;
        assertEquals("-0.1234567899", dp10.apply(value));
    }
    
    /**
     * The goal of this test is to check that the formatting is not impacted
     * by the locale.
     */
    @Test
    public void testLocale() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        DoubleFunction<String> df4 = SVGUtils.createDoubleConverter(4);
        DoubleFunction<String> df6 = SVGUtils.createDoubleConverter(6);
        double value = 1;
        assertEquals("1.0", d2s.apply(value));
        assertEquals("1.0", ryu.apply(value));
        assertEquals("1", df4.apply(value));
        assertEquals("1", df6.apply(value));

        value = -1;
        assertEquals("-1.0", d2s.apply(value));
        assertEquals("-1.0", ryu.apply(value));
        assertEquals("-1", df4.apply(value));
        assertEquals("-1", df6.apply(value));

        value = 1500;
        assertEquals("1500.0", d2s.apply(value));
        assertEquals("1500.0", ryu.apply(value));
        assertEquals("1500", df4.apply(value));
        assertEquals("1500", df6.apply(value));

        value = -1500;
        assertEquals("-1500.0", d2s.apply(value));
        assertEquals("-1500.0", ryu.apply(value));
        assertEquals("-1500", df4.apply(value));
        assertEquals("-1500", df6.apply(value));
        
        Locale.setDefault(saved);
    }

}
