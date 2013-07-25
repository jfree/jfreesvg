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
