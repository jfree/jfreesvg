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
 * A PDF Object (also referred to as an 'Indirect Object' in the PDF
 * specification).  Objects are identified within a document by their
 * PDF object number (which must be unique within the document) and their
 * PDF object generation number.
 */
public abstract class PDFObject {

    private int number;
    
    private int generation;
    
    /**
     * Creates a new instance with the specified object number and the default
     * generation number (0).
     * 
     * @param number  the object number. 
     */
    protected PDFObject(int number) {
        this(number, 0);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param number  the PDF object number (must be unique within the 
     *     document).
     * @param generation  the PDF generation number.
     */
    protected PDFObject(int number, int generation) {
        this.number = number;
        this.generation = generation;
    }
    
    /**
     * Returns the PDF object number (specified in the constructor).
     * 
     * @return The PDF object number. 
     */
    public int getNumber() {
        return this.number;
    }
    
    /**
     * Returns the PDF object generation number.
     * 
     * @return The PDF object generation number. 
     */
    public int getGeneration() {
        return this.generation;
    }
    
    /**
     * Returns the PDF reference string for this object (for example, "2 0 R").
     * This is the PDF object number followed by a space followed by the 
     * PDF object generation number, followed by a space and an 'R'.
     * 
     * @return The PDF reference string. 
     */
    public String getReference() {
        return this.number + " " + this.generation + " R";
    }
    
    /**
     * Returns a string describing this object in PDF format.
     * 
     * @return A string describing this object in PDF format.
     */
    public String toPDF() {
        StringBuilder b = new StringBuilder();
        b.append(this.number).append(" ").append(this.generation).append(" ");
        b.append("obj\n");
        b.append(getObjectString());
        b.append("endobj\n");
        return b.toString();
    }
    
    /**
     * Returns the PDF object string (a complete description of the object
     * in string format).
     * 
     * @return The PDF object string.
     * 
     * @see #toPDF() 
     */
    public abstract String getObjectString();

}
