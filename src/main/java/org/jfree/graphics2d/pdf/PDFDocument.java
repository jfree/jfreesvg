/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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
 * Represents a PDF document.
 */
public class PDFDocument {
    
    private static final Logger LOGGER = Logger.getLogger(PDFDocument.class.getName());

    private DictionaryObject catalog;
    
    private DictionaryObject outlines;
    
    private Pages pages;
    
    private int nextNumber = 1;

    /**
     * Creates a new PDFDocument.
     */
    public PDFDocument() {
        this.catalog = new DictionaryObject(this.nextNumber++, 0, "/Catalog");
        this.outlines = new DictionaryObject(this.nextNumber++, 0, "/Outlines");
        this.outlines.put("Count", Integer.valueOf(0));
        this.catalog.put("Outlines", this.outlines);
        this.pages = new Pages(this.nextNumber++, 0, this);
        this.catalog.put("Pages", this.pages);
    }
    
    public Page createPage(Rectangle2D bounds) {
        Page page = new Page(this.nextNumber++, 0, this.pages, bounds);
        this.pages.add(page);
        return page;
    }

    public int getNextNumber() {
        int result = this.nextNumber;
        this.nextNumber++;
        return result;
    }

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
            bos.write(toBytes("0 " + String.valueOf(this.nextNumber - 1) + "\n"));
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
            bos.write(toBytes(trailer.toPDFString()));
            bos.write(toBytes("startxref\n"));
            bos.write(toBytes(String.valueOf(xref[this.nextNumber - 1]) + "\n"));
            bos.write(toBytes("%%EOF"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return bos.toByteArray();
    }
    
    /**
     * Writes the PDF document to a file.
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
