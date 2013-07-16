/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

import java.awt.Font;
import java.util.Objects;

/**
 * A key to represent a Java2D font.  This is used to maintain a mapping 
 * between Java2D fonts and PDF fonts.
 */
public class FontKey {
 
    /** The key name. */
    private String name;
    
    /** Flag for bold. */
    private boolean isBold;
    
    /** Flag for italic. */
    private boolean isItalic;
    
    /**
     * Creates a new key for a given font.
     * 
     * @param f  the font.
     * 
     * @return The font key. 
     */
    public static FontKey createFontKey(Font f) {
        String family = f.getFamily();
        boolean bold = f.isBold();
        boolean italic = f.isItalic();
        return new FontKey(family, bold, italic);
    }
    
    /**
     * Creates a new font key.
     * 
     * @param name  the name.
     * @param bold  the bold flag.
     * @param italic  the italic flag.
     */
    public FontKey(String name, boolean bold, boolean italic) {
        this.name = name;
        this.isBold = bold;
        this.isItalic = italic;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FontKey other = (FontKey) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.isBold != other.isBold) {
            return false;
        }
        if (this.isItalic != other.isItalic) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (this.isBold ? 1 : 0);
        hash = 97 * hash + (this.isItalic ? 1 : 0);
        return hash;
    }

}
