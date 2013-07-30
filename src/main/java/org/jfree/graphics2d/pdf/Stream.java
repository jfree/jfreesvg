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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A <code>Stream</code> is a {@link PDFObject} that has a {@link Dictionary} 
 * and a byte stream.
 */
public abstract class Stream extends PDFObject {
    
    /**
     * Creates a new stream.
     * 
     * @param number  the PDF object number.
     */
    Stream(int number) {
        super(number);
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
        StringBuilder b = new StringBuilder();
        b.append(createDictionary(streamContent.length()).toPDFString());
        b.append("stream\n");
        b.append(streamContent);
        b.append("endstream\n");
        return b.toString();   
    }
    
    @Override
    public byte[] getObjectBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] streamData = getRawStreamData();
        Dictionary dictionary = createDictionary(streamData.length);
        baos.write(dictionary.toPDFBytes());
        baos.write(PDFUtils.toBytes("stream\n"));
        baos.write(streamData);
        baos.write(PDFUtils.toBytes("endstream\n"));
        return baos.toByteArray();
    }

    protected Dictionary createDictionary(int streamLength) {
        Dictionary dictionary = new Dictionary();
        dictionary.put("/Length", Integer.valueOf(streamLength));
        return dictionary;
    }
    
    /**
     * Returns the PDF string describing the stream content.
     * 
     * @return The PDF string describing the stream content. 
     */
    public abstract String getStreamContentString();
    
    /**
     * Returns the raw data for the stream.
     * 
     * @return The raw data for the stream. 
     */
    public abstract byte[] getRawStreamData();
    
    public abstract byte[] getFilteredStreamData();
}
