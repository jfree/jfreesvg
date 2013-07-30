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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import org.jfree.graphics2d.Ascii85OutputStream;

/**
 * A PDFImage.
 */
public class PDFImage extends Stream {

    int width;
    
    int height;

    Image image;
    
    public PDFImage(int number, Image img) {
        super(number);
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
        this.image = img;
    }
    
    @Override
    public String getStreamContentString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Ascii85OutputStream out = new Ascii85OutputStream(baos);
        try {
            out.write(getRawStreamData());
            out.flush();
            out.close();
        } catch (Exception e) {
            // oh no!
        }
        return baos.toString();
    }

    @Override
    public byte[] getRawStreamData() {
        return getFilteredStreamData();
        //return getImageData();
    }
    
    private byte[] getImageData() {
        BufferedImage bi;
        if (!(this.image instanceof BufferedImage)) {
            bi = new BufferedImage(this.width, this.height, 
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(this.image, 0, 0, null);
        } else {
            bi = (BufferedImage) this.image;
        }
        // take the image and encode it Ascii85
        byte[] result = new byte[this.width * this.height * 3];
        int i = 0;
        for (int hh = this.height - 1; hh >= 0; hh--) {
            for (int ww = 0; ww < this.width; ww++) {
                int rgb = bi.getRGB(ww, hh);
                byte r = (byte) (rgb >> 16);
                byte g = (byte) (rgb >> 8);
                byte b = (byte) rgb;
                result[i++] = r;
                result[i++] = g;
                result[i++] = b;
            }
        }
        return result;
    }
    
    public byte[] getFilteredStreamData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Ascii85OutputStream out = new Ascii85OutputStream(baos);
        try {
            out.write(getImageData());
            out.flush();
            out.close();
        } catch (Exception e) {
            // oh no!
        }
        return baos.toByteArray();
    }
    
    @Override
    protected Dictionary createDictionary(int streamLength) {
        Dictionary dictionary = super.createDictionary(streamLength);
        dictionary.setType("/XObject");
        dictionary.put("/Subtype", "/Image");
        dictionary.put("/ColorSpace", "/DeviceRGB");
        dictionary.put("/BitsPerComponent", 8);
        dictionary.put("/Width", this.width);
        dictionary.put("/Height", this.height);
        dictionary.put("/Filter", "/ASCII85Decode");
        return dictionary;
    }
}
