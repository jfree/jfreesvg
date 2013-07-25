/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
 * Represents a PDF font.
 */
public class PDFFont extends PDFObject {
  
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_ITALIC = "Courier-Italic";
    public static final String COURIER_BOLDITALIC = "Courier-BoldItalic";
    
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
