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

/**
 * A <code>Stream</code> is a {@link PDFObject} that has a {@link Dictionary} 
 * and a byte stream.
 */
public abstract class Stream extends PDFObject {

    /** The dictionary. */
    private Dictionary dictionary;
    
    /**
     * Creates a new stream with an empty dictionary.
     * 
     * @param number  the PDF object number.
     * @param generation  the PDF object generation number.
     */
    Stream(int number, int generation) {
        super(number, generation);
        this.dictionary = new Dictionary();
    }
    
    /**
     * Returns the PDF string describing this stream. This will eventually
     * be written to the byte array for the PDF document.
     * 
     * @return The PDF string. 
     */
    @Override
    public String getObjectString() {
        String streamContent = getStreamContentString();
        this.dictionary.put("/Length", Integer.valueOf(streamContent.length()));
        StringBuilder b = new StringBuilder();
        b.append(this.dictionary.toPDFString());
        b.append(streamContent);
        return b.toString();   
    }
    
    /**
     * Returns the PDF string describing the stream content.
     * 
     * @return The PDF string describing the stream content. 
     */
    public abstract String getStreamContentString();
    
}
