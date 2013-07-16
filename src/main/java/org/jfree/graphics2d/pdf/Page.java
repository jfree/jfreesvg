/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a page.
 */
public class Page extends PDFObject {
    
    /** The pages of the document. */
    private Pages parent;
    
    private Rectangle2D bounds;
    
    private GraphicsStream contents;
    
    private Dictionary resources;
    
    /**
     * The list of font (names) used on the page.  We let the parent take
     * care of tracking the font objects.
     */
    private List<String> fontsOnPage;
    
    /**
     * Creates a new page.
     * 
     * @param number
     * @param generation
     * @param parent
     * @param bounds 
     */
    Page(int number, int generation, Pages parent, Rectangle2D bounds) {
        super(number, generation);
        this.parent = parent;
        this.bounds = bounds;
        this.resources = new Dictionary();
        this.resources.put("/ProcSet", "[/PDF /Text]");
        this.fontsOnPage = new ArrayList<String>();
        int n = this.parent.getDocument().getNextNumber();
        this.contents = new GraphicsStream(n, 0, this);
    }
    
    public PDFObject getContents() {
        return this.contents;
    }
    
    public PDFGraphics2D getGraphics2D() {
        return new PDFGraphics2D(this.contents, (int) this.bounds.getWidth(), 
                (int) this.bounds.getHeight());
    }

    /**
     * Finds the font reference corresponding to the given Java2D font, 
     * creating a new one if there isn't one already.
     * 
     * @param font  the AWT font.
     * 
     * @return The font reference.
     */
    public String findOrCreateFontReference(Font font) {
        String ref = this.parent.findOrCreateFontReference(font);
        if (!this.fontsOnPage.contains(ref)) {
            this.fontsOnPage.add(ref);
        }
        return ref;
    }
    
    private Dictionary createFontDictionary() {
        Dictionary d = new Dictionary();
        for (String name : this.fontsOnPage) {
            PDFFont f = this.parent.getFont(name);
            d.put(name, f.getReference());
        }
        return d;
    }
    
    @Override
    public String getObjectString() {
        Dictionary dictionary = new Dictionary("/Page");
        dictionary.put("/Parent", this.parent);
        dictionary.put("/MediaBox", this.bounds);
        dictionary.put("/Contents", this.contents);
        dictionary.put("/Resources", this.resources);
       
        if (!this.fontsOnPage.isEmpty()) {
            this.resources.put("/Font", createFontDictionary());
        }
        String result = dictionary.toPDFString();
        this.resources.put("/Font", null);
        return result;
    }
}
