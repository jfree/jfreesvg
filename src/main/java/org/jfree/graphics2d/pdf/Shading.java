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

import org.jfree.graphics2d.Args;

/**
 * A shading object.
 */
public abstract class Shading extends PDFObject {

    public static final class AxialShading extends Shading {
       
        /** Coordinates (x0, y0, x1, y1) defining the axis. */
        private double[] coords;
        
        /** The shading function. */
        private Function function;
        
        private double[] domain;
        
        /** The extend flags (two of them). */
        private boolean[] extend;
        
        public AxialShading(int number, double[] coords, Function function) {
            super(number, ShadingType.AXIAL);
            this.dictionary.put("/ColorSpace", "/DeviceRGB");
            this.dictionary.put("/Extend", "[true true]");
            setCoords(coords);
            setFunction(function);
            this.domain = new double[] {0.0, 1.0};
            this.extend = new boolean[] {false, false};
        }
        
        public double[] getCoords() {
            return this.coords.clone();
        }
        
        public void setCoords(double[] coords) {
            Args.arrayMustHaveLength(4, coords, "coords");
            this.coords = coords.clone();
            this.dictionary.put("/Coords", PDFUtils.toPDFArray(this.coords));
        }
        
        public Function getFunction() {
            return this.function;
        }
        
        public void setFunction(Function function) {
            Args.nullNotPermitted(function, "function");
            this.function = function;
            this.dictionary.put("/Function", this.function);
        }
        
        public double[] getDomain() {
            return this.domain.clone();
        }
        
        public void setDomain(double[] domain) {
            Args.arrayMustHaveLength(2, domain, "domain");
            this.domain = domain.clone();
            this.dictionary.put("/Domain", PDFUtils.toPDFArray(this.domain));
        }
        
        public boolean[] getExtend() {
            return this.extend.clone();
        }
        
        public void setExtend(boolean[] extend) {
            this.extend = extend.clone();
            this.dictionary.put("/Extend", PDFUtils.toPDFArray(this.extend));
        }
        
    }
    
    private ShadingType shadingType;
    
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
        if (this.shadingType == ShadingType.AXIAL) {
            this.dictionary.put("/ShadingType", "2");
        }
    }
    
    /**
     * Returns the shading type.
     * 
     * @return The shading type. 
     */
    public ShadingType getShadingType() {
        return this.shadingType;
    }

    /**
     * Returns the PDF object string.
     * 
     * @return The PDF object string. 
     */
    @Override
    public String getObjectString() {
        return this.dictionary.toPDFString();
    }
}
