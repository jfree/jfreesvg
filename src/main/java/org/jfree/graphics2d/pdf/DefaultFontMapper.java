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
import java.util.HashMap;
import java.util.Map;

/**
 * A class that can be used to map AWT/Java2D fonts to PDF built-in font names.
 * This is a very minimal way to support fonts in our <code>PDFGraphics2D</code> 
 * implementation...hope to improve it later.
 */
public class DefaultFontMapper implements FontMapper {

    private Map<FontKey, String> map;
    
    /**
     * Creates a new instance.
     */
    public DefaultFontMapper() {
        this.map = new HashMap<FontKey, String>();
        this.map.put(new FontKey("Dialog", false, false), PDFFont.HELVETICA);
        this.map.put(new FontKey("Dialog", true, false), PDFFont.HELVETICA_BOLD);
        this.map.put(new FontKey("Dialog", false, true), PDFFont.HELVETICA_OBLIQUE);
        this.map.put(new FontKey("Dialog", true, true), PDFFont.HELVETICA_BOLDOBLIQUE);
        this.map.put(new FontKey("Arial", false, false), PDFFont.HELVETICA);
        this.map.put(new FontKey("Arial", true, false), PDFFont.HELVETICA_BOLD);
        this.map.put(new FontKey("Arial", false, true), PDFFont.HELVETICA_OBLIQUE);
        this.map.put(new FontKey("Arial", true, true), PDFFont.HELVETICA_BOLDOBLIQUE);
        this.map.put(new FontKey("Courier", false, false), PDFFont.COURIER);
        this.map.put(new FontKey("Courier", true, false), PDFFont.COURIER_BOLD);
        this.map.put(new FontKey("Courier", false, true), PDFFont.COURIER_ITALIC);
        this.map.put(new FontKey("Courier", true, true), PDFFont.COURIER_BOLDITALIC);
        this.map.put(new FontKey("Courier New", false, false), PDFFont.COURIER);
        this.map.put(new FontKey("Courier New", true, false), PDFFont.COURIER_BOLD);
        this.map.put(new FontKey("Courier New", false, true), PDFFont.COURIER_ITALIC);
        this.map.put(new FontKey("Courier New", true, true), PDFFont.COURIER_BOLDITALIC);
        this.map.put(new FontKey("DialogInput", false, false), PDFFont.HELVETICA);
        this.map.put(new FontKey("DialogInput", true, false), PDFFont.HELVETICA_BOLD);
        this.map.put(new FontKey("DialogInput", false, true), PDFFont.HELVETICA_OBLIQUE);
        this.map.put(new FontKey("DialogInput", true, true), PDFFont.HELVETICA_BOLDOBLIQUE);
        this.map.put(new FontKey("Monospaced", false, false), PDFFont.COURIER);
        this.map.put(new FontKey("Monospaced", true, false), PDFFont.COURIER_BOLD);
        this.map.put(new FontKey("Monospaced", false, true), PDFFont.COURIER_ITALIC);
        this.map.put(new FontKey("Monospaced", true, true), PDFFont.COURIER_BOLDITALIC);
        this.map.put(new FontKey("Palatino", false, false), PDFFont.TIMES_ROMAN);
        this.map.put(new FontKey("Palatino", true, false), PDFFont.TIMES_BOLD);
        this.map.put(new FontKey("Palatino", false, true), PDFFont.TIMES_ITALIC);
        this.map.put(new FontKey("Palatino", true, true), PDFFont.TIMES_BOLDITALIC);
        this.map.put(new FontKey("SansSerif", false, false), PDFFont.HELVETICA);
        this.map.put(new FontKey("SansSerif", true, false), PDFFont.HELVETICA_BOLD);
        this.map.put(new FontKey("SansSerif", false, true), PDFFont.HELVETICA_OBLIQUE);
        this.map.put(new FontKey("SansSerif", true, true), PDFFont.HELVETICA_BOLDOBLIQUE);
        this.map.put(new FontKey("Serif", false, false), PDFFont.TIMES_ROMAN);
        this.map.put(new FontKey("Serif", true, false), PDFFont.TIMES_BOLD);
        this.map.put(new FontKey("Serif", false, true), PDFFont.TIMES_ITALIC);
        this.map.put(new FontKey("Serif", true, true), PDFFont.TIMES_BOLDITALIC);
        this.map.put(new FontKey("Tahoma", false, false), PDFFont.TIMES_ROMAN);
        this.map.put(new FontKey("Tahoma", true, false), PDFFont.TIMES_BOLD);
        this.map.put(new FontKey("Tahoma", false, true), PDFFont.TIMES_ITALIC);
        this.map.put(new FontKey("Tahoma", true, true), PDFFont.TIMES_BOLDITALIC);
        this.map.put(new FontKey("Times New Roman", false, false), PDFFont.TIMES_ROMAN);
        this.map.put(new FontKey("Times New Roman", true, false), PDFFont.TIMES_BOLD);
        this.map.put(new FontKey("Times New Roman", false, true), PDFFont.TIMES_ITALIC);
        this.map.put(new FontKey("Times New Roman", true, true), PDFFont.TIMES_BOLDITALIC);
    }
    
    @Override
    public String mapToBaseFont(Font f) {
        String result = this.map.get(FontKey.createFontKey(f));
        if (result == null) {
            System.out.println("FONT = " + f);
            result = "Courier";
        }
        return result;
    }
    
}
