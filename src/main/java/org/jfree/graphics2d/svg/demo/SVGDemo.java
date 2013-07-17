/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.svg.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import org.jfree.graphics2d.svg.SVGGraphics2D;

/**
 * A quick demo.
 */
public class SVGDemo {
    
    public static void main(String[] args) throws IOException {
        BufferedWriter writer = null;
        try {
            File file = new File("SVGWithImage.html");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>SVGGraphics2D Test</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"); 
            writer.write("</head>\n");
            writer.write("<body>\n");
            SVGGraphics2D g2 = new SVGGraphics2D(600, 400);
            ImageIcon icon = new ImageIcon("/Users/dgilbert/jfree_chart_1.jpg");
            //g2.rotate(Math.PI / 12);
            g2.setStroke(new BasicStroke(2.0f));
            g2.setPaint(Color.WHITE);
            g2.fill(new Rectangle(0, 0, 600, 400));
            g2.setPaint(Color.RED);
            g2.draw(new Rectangle(0, 0, 600, 400));
            g2.clipRect(30, 40, 50, 60);
            g2.drawImage(icon.getImage(), 10, 20, null);
            //this.chart.draw(g2, new Rectangle2D.Double(0, 0, 600, 400));
            writer.write(g2.getSVG() + "\n");
            System.out.println(g2.getSVG());
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            writer.close();
        } 
    }
}
