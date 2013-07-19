/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

import java.awt.Font;

/**
 * Maps an AWT font to a PDF built-in font.
 */
public interface FontMapper {

    /**
     * Returns the name of the PDF built-in font that should be used in place
     * of the specified AWT/Java2D font.
     * 
     * @param f  the font.
     * 
     * @return The name of the built-in PDF font. 
     */
    String mapToBaseFont(Font f);
    
}
