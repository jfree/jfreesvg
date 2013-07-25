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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jfree.graphics2d.TextAnchor;
import org.jfree.graphics2d.TextUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D;

/**
 * A small demo that draws an image as part of the rendering to SVG.
 */
public class SVGImageDemo1 {
    
    public static void main(String[] args) throws IOException {
        BufferedWriter writer = null;
        try {
            File file = new File("SVGImageDemo1.html");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>SVGImageDemo1.html</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"); 
            writer.write("</head>\n");
            writer.write("<body>\n");
            SVGGraphics2D g2 = new SVGGraphics2D(600, 400);
//            g2.setRenderingHint(SVGHints.KEY_IMAGE_HANDLING, 
//                    SVGHints.VALUE_IMAGE_HANDLING_REFERENCE);
            g2.setTransformDP(0);
            //ImageIcon icon = new ImageIcon("/jfree_chart_1.jpg");
            //g2.rotate(Math.PI / 12);
            g2.setStroke(new BasicStroke(2.0f));
            g2.setPaint(Color.WHITE);
            g2.fill(new Rectangle(0, 0, 600, 400));
            g2.setPaint(Color.RED);
            g2.draw(new Rectangle(0, 0, 600, 400));
            //g2.drawImage(icon.getImage(), 10, 20, null);
            TextUtils.drawAlignedString("Aligned", g2, 600, 10, TextAnchor.TOP_RIGHT);
            g2.translate(30, 30);
            TextUtils.drawAlignedString("Centered", g2, 300, 200, TextAnchor.CENTER);
        TextUtils.drawRotatedString("Rotated", g2, 300, 200, TextAnchor.CENTER_LEFT, Math.PI / 2, TextAnchor.CENTER_LEFT);
//        for (int i = 0; i < 15; i++) {
//          TextUtils.drawRotatedString("Rotated", g2, 300, 200, TextAnchor.CENTER_LEFT, Math.PI / 15.0 * i, TextAnchor.CENTER_LEFT);
//        }
            writer.write(g2.getSVGElement() + "\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        } 
    }
}
