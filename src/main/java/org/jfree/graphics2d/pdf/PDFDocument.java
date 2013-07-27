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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
        
/**
 * Represents a PDF document.  The current focus of this implementation is to
 * allow the use of the {@link PDFGraphics2D} class to generate PDF content, 
 * typically in the following manner:
 * <p>
 * <code>PDFDocument pdfDoc = new PDFDocument();<br></code>
 * <code>Page page = pdfDoc.createPage(new Rectangle(612, 468));<br></code>
 * <code>PDFGraphics2D g2 = page.getGraphics2D();<br></code>
 * <code>g2.setPaint(Color.RED);<br></code>
 * <code>g2.draw(new Rectangle(10, 10, 40, 50));<br></code>
 * <code>pdfDoc.writeToFile(new File("demo.pdf"));<br></code>
 * <p>
 * The implementation is light-weight and even this initial release shows
 * promising results working alongside packages such as <b>JFreeChart</b>.
 */
public class PDFDocument {
    
    private static final Logger LOGGER = Logger.getLogger(
            PDFDocument.class.getName());

    /** The document catalog. */
    private DictionaryObject catalog;
    
    /** The outlines (placeholder, outline support is not implemented). */
    private DictionaryObject outlines;
    
    /** Document info. */
    private DictionaryObject info;
    
    /** The pages of the document. */
    private Pages pages;
    
    /** The next PDF object number in the document. */
    private int nextNumber = 1;

    /**
     * Creates a new <code>PDFDocument</code>, initially with no content.
     */
    public PDFDocument() {
        this.catalog = new DictionaryObject(this.nextNumber++, 0, "/Catalog");
        this.outlines = new DictionaryObject(this.nextNumber++, 0, "/Outlines");
        this.info = new DictionaryObject(this.nextNumber++, 0, "/Info");
        this.info.put("Producer", "(JFreeGraphics2D 1.0)");
        this.outlines.put("Count", Integer.valueOf(0));
        this.catalog.put("Outlines", this.outlines);
        this.pages = new Pages(this.nextNumber++, 0, this);
        this.catalog.put("Pages", this.pages);
    }
    
    /**
     * Creates a new <code>Page</code>, adds it to the document, and returns
     * a reference to the <code>Page</code>.
     * 
     * @param bounds  the page bounds (<code>null</code> not permitted).
     * 
     * @return The new page. 
     */
    public Page createPage(Rectangle2D bounds) {
        Page page = new Page(this.nextNumber++, 0, this.pages, bounds);
        this.pages.add(page);
        return page;
    }

    /**
     * Returns a new PDF object number and increments the internal counter
     * for the next PDF object number.  This method is used to ensure that
     * all objects in the document are assigned a unique number.
     * 
     * @return A new PDF object number. 
     */
    public int getNextNumber() {
        int result = this.nextNumber;
        this.nextNumber++;
        return result;
    }

    /**
     * Returns a byte array containing the encoding of this PDF document.
     * 
     * @return A byte array containing the encoding of this PDF document. 
     */
    public byte[] getPDFBytes() {
        int[] xref = new int[this.nextNumber];
        int obj = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(toBytes("%PDF-1.4\n"));
            xref[obj++] = bos.size();  // offset to catalog
            bos.write(toBytes(this.catalog.toPDF()));
            xref[obj++] = bos.size();  // offset to outlines
            bos.write(toBytes(this.outlines.toPDF()));            
            xref[obj++] = bos.size();  // offset to info
            bos.write(toBytes(this.info.toPDF()));
            xref[obj++] = bos.size();  // offset to pages
            bos.write(toBytes(this.pages.toPDF()));
            xref[obj++] = bos.size();
            for (Page page : this.pages.getPages()) {
                bos.write(toBytes(page.toPDF()));
                xref[obj++] = bos.size();
                PDFObject contents = page.getContents();
                bos.write(toBytes(contents.toPDF()));
                xref[obj++] = bos.size();
            }
            for (PDFFont font: this.pages.getFonts()) {
                bos.write(toBytes(font.toPDF()));
                xref[obj++] = bos.size();
            }
            
            // write the xref table
            bos.write(toBytes("xref\n"));
            bos.write(toBytes("0 " + String.valueOf(this.nextNumber - 1) 
                    + "\n"));
            bos.write(toBytes("0000000000 65535 f\n"));
            for (int i = 0; i < this.nextNumber - 1; i++) {
                String offset = String.valueOf(xref[i]);
                int len = offset.length();
                String offset10 = "0000000000".substring(len) + offset;
                bos.write(toBytes(offset10 + " 00000 n\n"));
            }
  
            // write the trailer
            bos.write(toBytes("trailer\n"));
            Dictionary trailer = new Dictionary();
            trailer.put("/Size", Integer.valueOf(this.nextNumber - 1));
            trailer.put("/Root", this.catalog);
            trailer.put("/Info", this.info);
            bos.write(toBytes(trailer.toPDFString()));
            bos.write(toBytes("startxref\n"));
            bos.write(toBytes(String.valueOf(xref[this.nextNumber - 1]) 
                    + "\n"));
            bos.write(toBytes("%%EOF"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return bos.toByteArray();
    }
    
    /**
     * Writes the PDF document to a file.  This is not a robust method, it
     * exists mainly for the demo output. 
     * 
     * @param f  the file.
     */
    public void writeToFile(File f) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(getPDFBytes());
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
   
    /**
     * A utility method to convert a string to US-ASCII byte format.
     * 
     * @param s  the string.
     * 
     * @return The corresponding byte array.
     */
    private byte[] toBytes(String s) {
        byte[] result = null;
        try {
            result = s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

}
