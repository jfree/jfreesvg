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

import org.jfree.graphics2d.pdf.shading.Shading;
import java.awt.geom.AffineTransform;
import org.jfree.graphics2d.Args;

/**
 * A pattern object (see the concrete subclass {@link ShadingPattern}).
 */
public abstract class Pattern extends PDFObject {
    
    /**
     * A shading pattern object.
     */
    public static final class ShadingPattern extends Pattern {
    
        private Shading shading;
        
        /**
         * Creates a new shading pattern.
         * 
         * @param number  the PDF object number.
         * @param shading  the shading.
         * @param t  the transform from the initial page space to Java2D space.
         */
        public ShadingPattern(int number, Shading shading, AffineTransform t) {
            super(number);
            this.dictionary.put("/PatternType", "2");
            this.dictionary.put("/Matrix", PDFUtils.transformToPDF(t));
            setShading(shading);
        }
        
        /**
         * Returns the shading.
         * 
         * @return The shading (never <code>null</code>). 
         */
        public Shading getShading() {
            return this.shading;
        }
        
        /**
         * Sets the shading.
         * 
         * @param shading  the shading (<code>null</code> not permitted). 
         */
        public void setShading(Shading shading) {
            Args.nullNotPermitted(shading, "shading");
            this.shading = shading;
            this.dictionary.put("/Shading", this.shading);
        }
    }
    
    protected Dictionary dictionary;
    
    /**
     * Creates a new pattern object.
     * 
     * @param number  the PDF object number. 
     */
    protected Pattern(int number) {
        super(number);
        this.dictionary = new Dictionary("/Pattern");
    }

    /**
     * Returns the bytes that go between the 'obj' and 'endobj' in the
     * PDF output for this object.
     * 
     * @return A byte array.
     */
    @Override
    public byte[] getObjectBytes() {
        return this.dictionary.toPDFBytes(); 
    }
}
