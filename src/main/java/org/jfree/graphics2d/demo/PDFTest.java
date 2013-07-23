/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import org.jfree.graphics2d.pdf.PDFDocument;
import org.jfree.graphics2d.pdf.PDFGraphics2D;
import org.jfree.graphics2d.pdf.Page;

/**
 * A test for PDF output.
 */
public class PDFTest {
 
    public static void main(String[] args) throws IOException {
        PDFDocument pdfDoc = new PDFDocument();
        Page page = pdfDoc.createPage(new Rectangle(612, 468));
        PDFGraphics2D g2 = page.getGraphics2D();
        //g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        AffineTransform saved = g2.getTransform();
        g2.setColor(Color.RED);
        g2.translate(50, 100);
        g2.drawRect(10, 10, 20, 40);
        g2.setTransform(saved);
        g2.drawString("DONE", 10, 10);
        File f = new File("PDFTest.pdf");
        pdfDoc.writeToFile(f);
    }

}
