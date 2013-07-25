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
