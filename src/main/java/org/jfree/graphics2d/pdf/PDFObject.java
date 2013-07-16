/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
