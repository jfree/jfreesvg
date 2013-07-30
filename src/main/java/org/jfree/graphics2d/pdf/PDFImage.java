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
import java.io.IOException;
import org.jfree.graphics2d.demo.Ascii85OutputStream;

/**
 * A PDFImage.
 */
public class PDFImage extends Stream {

    int width;
    
    int height;

    byte[] data;
    
    public PDFImage(int number, Image img) {
        super(number);
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
        BufferedImage bi;
        if (!(img instanceof BufferedImage)) {
            bi = new BufferedImage(this.width, this.height, 
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(img, 0, 0, null);
        } else {
            bi = (BufferedImage) img;
        }
        // take the image and encode it Ascii85
        this.data = new byte[this.width * this.height * 3];
        int i = 0;
        for (int hh = this.height - 1; hh >= 0; hh--) {
            for (int ww = 0; ww < this.width; ww++) {
                int rgb = bi.getRGB(ww, hh);
                byte r = (byte) (rgb >> 16);
                byte g = (byte) (rgb >> 8);
                byte b = (byte) rgb;
                this.data[i++] = r;
                this.data[i++] = g;
                this.data[i++] = b;
            }
        }
    }
    
    @Override
    public String getStreamContentString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Ascii85OutputStream out = new Ascii85OutputStream(baos);
        try {
            out.write(this.data);
            out.flush();
            out.close();
        } catch (Exception e) {
            // oh no!
        }
        return baos.toString();
    }

    @Override
    public byte[] getRawStreamData() {
        return this.data;
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
        //this.dictionary.put("/Filter", "/ASCII85Decode");
        return dictionary;
    }
}
