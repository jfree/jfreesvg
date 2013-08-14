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

package org.jfree.graphics2d.pdf.shading;

import org.jfree.graphics2d.Args;
import org.jfree.graphics2d.pdf.Dictionary;
import org.jfree.graphics2d.pdf.PDFObject;

/**
 * A PDF shading object (this class is abstract, subclasses implement 
 * particular shading types).
 */
public abstract class Shading extends PDFObject {

    /** The shading type. */
    private ShadingType shadingType;
    
    /** The dictionary. */
    protected Dictionary dictionary;
    
    /**
     * Creates a new shading instance.
     * 
     * @param number  the PDF object number.
     * @param shadingType  the shading type (<code>null</code> not permitted).
     */
    protected Shading(int number, ShadingType shadingType) {
        super(number);
        Args.nullNotPermitted(shadingType, "shadingType");
        this.shadingType = shadingType;
        this.dictionary = new Dictionary();
        this.dictionary.put("/ShadingType", String.valueOf(
                shadingType.getNumber()));
    }
    
    /**
     * Returns the shading type.
     * 
     * @return The shading type (never <code>null</code>). 
     */
    public ShadingType getShadingType() {
        return this.shadingType;
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
