/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

/**
 * A stream will have a Dictionary and a byte stream (and is also a PDFObject).
 */
public abstract class Stream extends PDFObject {

    private Dictionary dictionary;
    
    Stream(int number, int generation) {
        super(number, generation);
        this.dictionary = new Dictionary();
    }
    
    public String getObjectString() {
        String streamContent = getStreamContentString();
        this.dictionary.put("/Length", Integer.valueOf(streamContent.length()));
        StringBuilder b = new StringBuilder();
        b.append(this.dictionary.toPDFString());
        b.append(streamContent);
        return b.toString();   
    }
    
    public abstract String getStreamContentString();
    
}
