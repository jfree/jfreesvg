/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

/**
 * A PDF object that is represented by a dictionary.  This is used to
 * represent the Catalog and the Outline (the latter being a placeholder
 * implementation only).
 */
public class DictionaryObject extends PDFObject {
    
    private Dictionary dictionary;

    /**
     * Creates a new instance.
     * 
     * @param number  the object number.
     * @param generation  the generation number.
     * @param type  the object type (for example, "/Catalog"). 
     */
    DictionaryObject(int number, int generation, String type) {
        super(number, generation);
        this.dictionary = new Dictionary(type);
    }
    
    /**
     * Puts an item in the dictionary.
     * 
     * @param name  the name.
     * @param value  the value.
     */
    public void put(String name, Object value) {
        this.dictionary.put("/" + name, value);
    }
    
    /**
     * Returns a string containing the PDF entry for this object.
     * 
     * @return A string containing the PDF entry for this object. 
     */
    @Override
    public String getObjectString() {
        return this.dictionary.toPDFString();
    }
}
