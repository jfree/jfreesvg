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

import org.jfree.graphics2d.Args;

/**
 * A PDF object that is represented by a dictionary.  This is used to
 * represent the <code>Catalog</code> and the <code>Outlines</code> (the latter 
 * being a placeholder implementation only since we don't generate outlines).
 */
public class DictionaryObject extends PDFObject {
    
    protected Dictionary dictionary;

    /**
     * Creates a new instance.
     * 
     * @param number  the object number.
     * @param type  the object type (for example, "/Catalog"). 
     */
    DictionaryObject(int number, String type) {
        super(number);
        this.dictionary = new Dictionary(type);
    }
    
    /**
     * Puts an item in the dictionary.
     * 
     * @param name  the name (without the leading "/", <code>null</code> not 
     *     permitted).
     * @param value  the value (<code>null</code> not permitted).
     */
    public void put(String name, Object value) {
        Args.nullNotPermitted(value, "value");
        this.dictionary.put("/" + name, value);
    }
    
    /**
     * Removes an item from the dictionary.
     * 
     * @param name  the name (without the leading "/").
     * 
     * @return The value that was previously stored with the given name.
     */
    public Object remove(String name) {
        return this.dictionary.remove("/" + name);
    }

    /**
     * Returns a string containing the PDF description for this object (the
     * text that goes between the 'obj' and 'endobj' in the PDF output).
     * 
     * @return A string containing the PDF entry for this object. 
     */
    @Override
    public String getObjectString() {
        return this.dictionary.toPDFString();
    }
    
    /**
     * Returns the bytes that go between the 'obj' and 'endobj' in the
     * PDF output for this object.
     * 
     * @return A byte array.
     */
    @Override
    public byte[] getObjectBytes() {
        return this.dictionary.toPDFBytes(); 
    }
}
