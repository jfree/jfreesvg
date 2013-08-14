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

package org.jfree.graphics2d.pdf;

/**
 * A PDF "Type 2" function.
 */
public final class ExponentialInterpolationFunction extends Function {
 
    double n;
        
    float[] c0;
        
    float[] c1;
        
    /**
     * Creates a new function.
     * 
     * @param number  the PDF object number.
     * @param c0  
     * @param c1 
     */
    public ExponentialInterpolationFunction(int number, float[] c0, 
            float[] c1) {
        super(number, FunctionType.EXPONENTIAL_INTERPOLATION);
        this.dictionary.put("/N", "1");
        this.n = 1;
        setC0(c0);
        setC1(c1);            
    }
        
    /**
     * Returns the interpolation exponent.  The default value is 
     * <code>1</code>.
     * 
     * @return The interpolation exponent.   
     */
    public double getN() {
        return this.n;
    }
        
    /**
     * Sets the interpolation exponent.
     * 
     * @param n  the new interpolation exponent. 
     */
    public void setN(double n) {
        this.n = n;
        this.dictionary.put("/N", String.valueOf(n));
    }
        
    /**
     * Returns the function result when x is 0.0.  The returned array is a 
     * copy.
     * 
     * @return The function result when x is 0.0. 
     */
    public float[] getC0() {
        return this.c0.clone(); 
    }
     
    /**
     * Sets the function result to be used when x is 0.0.
     * 
     * @param c0  the function result.
     */
    public void setC0(float[] c0) {
        this.c0 = c0.clone();
        this.dictionary.put("/C0", PDFUtils.toPDFArray(c0));
    }
        
    /**
     * Returns the function result when x is 1.0.  The returned array is a 
     * copy.
     * 
     * @return The function result when x is 1.0. 
     */
    public float[] getC1() {
        return this.c1.clone(); 
    }
        
    /**
     * Sets the function result to be used when x is 1.0.
     * 
     * @param c1  the function result.
     */
    public void setC1(float[] c1) {
       this.c1 = c1.clone();
       this.dictionary.put("/C1", PDFUtils.toPDFArray(c1));
    }
}