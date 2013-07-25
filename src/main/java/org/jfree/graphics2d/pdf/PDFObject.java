/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
 * A PDF Object (also referred to as an 'Indirect Object' in the PDF
 * specification).
 * 
 * Subclasses will include DictionaryObject, Page, PDFFont and Stream 
 * (e.g. GraphicsStream).
 */
public abstract class PDFObject {

    private int number;
    
    private int generation;
    
    protected PDFObject(int number, int generation) {
        this.number = number;
        this.generation = generation;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public int getGeneration() {
        return this.generation;
    }
    
    /**
     * Returns the PDF reference string for this object (for example, "2 0 R").
     * 
     * @return The PDF reference string. 
     */
    public String getReference() {
        return this.number + " " + this.generation + " R";
    }
    
    public String toPDF() {
        StringBuilder b = new StringBuilder();
        b.append(this.number).append(" ").append(this.generation).append(" ");
        b.append("obj\n");
        b.append(getObjectString());
        b.append("endobj\n");
        return b.toString();
    }
    
    public abstract String getObjectString();

}
