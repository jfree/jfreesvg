/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

/**
 * Represents a PDF font.
 */
public class PDFFont extends PDFObject {
  
    private String name;
    
    /** The BaseFont (for example, "/Helvetica"). */
    private String baseFont;
    
    private String encoding;
    
    PDFFont(int number, int generation, String name, String baseFont, 
            String encoding) {
        super(number, generation);
        this.name = name;
        this.baseFont = baseFont;
        this.encoding = encoding;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getObjectString() {
        Dictionary dictionary = new Dictionary("/Font");
        dictionary.put("/Subtype", "/Type1");
        dictionary.put("/Name", this.name);
        dictionary.put("/BaseFont", this.baseFont);
        dictionary.put("/Encoding", this.encoding);
        return dictionary.toPDFString();
    }
}
