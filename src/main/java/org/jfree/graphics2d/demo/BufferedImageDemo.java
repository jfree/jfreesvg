/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jfree.graphics2d.TextUtils;
import org.jfree.ui.TextAnchor;

/**
 * A small demo that draws an image as part of the rendering to SVG.
 */
public class BufferedImageDemo {
    
    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
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
        ImageIO.write(image, "png", new File("image-test.png"));
    }
}
