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

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * A dictionary is a map and supports writing the bytes for the dictionary
 * in the PDF syntax.
 */
public class Dictionary {
    
    /** 
     * The type entry.  We treat this as a special case, because when a type is
     * defined, we want it to appear first in the PDF output.  Note that it
     * can be set to null for some dictionaries.
     */
    private String type;
    
    /** Data storage. */
    private Map map;

    /**
     * Creates a new instance with no type.
     */
    public Dictionary() {
        this(null);
    }
    
    /**
     * Creates a new dictionary with the specified type (which can be 
     * {@code null}).
     * 
     * @param type  the type value (for example, "/Catalog").
     */
    public Dictionary(String type) {
        this.type = type;
        this.map = new HashMap();    
    }
    
    /**
     * Puts an entry in the dictionary.
     * 
     * @param key  the key.
     * @param value  the value.
     */
    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    /**
     * Returns a string containing the PDF text describing the dictionary.
     * Note that this is a Java string, conversion to byte format happens
     * elsewhere.
     * 
     * @return A string.
     */
    public String toPDFString() {
        StringBuilder b = new StringBuilder();
        b.append("<< ");
        if (this.type != null) {
            b.append("/Type ").append(this.type).append("\n");
        }
        // now iterate through the dictionary and write it's values
        for (Object key : this.map.keySet()) {
            Object value = this.map.get(key);
            if (value instanceof Number || value instanceof String) {
                b.append(key.toString()).append(" ");
                b.append(value.toString()).append("\n");                
            } else if (value instanceof PDFObject) {
                PDFObject pdfObj = (PDFObject) value;
                b.append(key.toString()).append(" ");
                b.append(pdfObj.getReference()).append("\n");
            } else if (value instanceof PDFObject[]) {
                b.append(key.toString()).append(" ");
                PDFObject[] array = (PDFObject[]) value;
                b.append("[");
                for (int i = 0; i < array.length; i++) {
                    if (i != 0) {
                        b.append(" ");
                    }
                    b.append(array[i].getReference());
                }
                b.append("]\n");
            } else if (value instanceof Rectangle2D) {
                Rectangle2D r = (Rectangle2D) value;
                b.append(key.toString()).append(" ");
                b.append("[").append(r.getX()).append(" ");
                b.append(r.getY()).append(" ").append(r.getWidth()).append(" ");
                b.append(r.getHeight()).append("]\n");
            } else if (value instanceof Dictionary) {
                b.append(key.toString()).append(" ");
                Dictionary d = (Dictionary) value;
                b.append(d.toPDFString());
            }
        }
        b.append(">>\n");
        return b.toString();
    }

}
