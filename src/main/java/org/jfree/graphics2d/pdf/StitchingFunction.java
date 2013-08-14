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
 * A stitching function.
 */
public final class StitchingFunction extends Function {
    
    private Function[] functions;
    
    private float[] bounds;
    
    private float[] encode;
    
    /**
     * Creates a new stitching function.
     * 
     * @param number  the PDF object number.
     * @param functions  the functions to be stitched.
     * @param bounds  the bounds.
     * @param encode  the encoding.
     */
    public StitchingFunction(int number, Function[] functions, float[] bounds,
            float[] encode) {
        super(number, FunctionType.STITCHING);
        this.functions = functions;
        this.dictionary.put("/Functions", functions);
        this.bounds = bounds;
        this.dictionary.put("/Bounds", bounds);
        this.encode = encode;
        this.dictionary.put("/Encode", encode);
    }

}
