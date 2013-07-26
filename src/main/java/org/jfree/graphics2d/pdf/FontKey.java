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
     * @param f  the font (<code>null</code> not permitted).
     * 
     * @return The font key. 
     */
    public static FontKey createFontKey(Font f) {
        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");
        }
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

    /**
     * Tests this key for equality with an arbitrary object.
     * 
     * @param obj  the object to test against (<code>null</code> permitted).
     * 
     * @return A boolean. 
     */
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
    
    /**
     * Returns a hash code for this instance.
     * 
     * @return A hash code. 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (this.isBold ? 1 : 0);
        hash = 97 * hash + (this.isItalic ? 1 : 0);
        return hash;
    }

}
