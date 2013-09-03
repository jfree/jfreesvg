package org.jfree.graphics2d.demo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dgilbert
 */
public class ImageTest {

    private static void drawClipTest(Graphics2D g2) {
        g2.translate(10, 20);
        g2.setColor(Color.RED);
        g2.fillRect(10, 10, 100, 100);
        g2.clip(new Rectangle(0, 0, 60, 60));
        g2.setPaint(Color.BLUE);
        g2.fillRect(10, 10, 100, 100);
        g2.setClip(null);
        g2.setPaint(Color.GREEN);
        g2.fillRect(60, 60, 50, 50);        
    }
    
    private static void drawGradientPaintTest(Graphics2D g2) {
        g2.setPaint(new GradientPaint(10f, 10f, Color.RED, 10f, 60f, Color.YELLOW));
        g2.fillRect(10, 10, 50, 50);
        g2.setPaint(Color.BLUE);
        g2.fillRect(60, 10, 50, 50);
    }
    
    private static void drawRadialGradientPaintTest(Graphics2D g2) {
        RadialGradientPaint rgp = new RadialGradientPaint(50, 50, 40, 30, 30, 
                new float[] {0f, 0.75f, 1f}, new Color[] {Color.RED, 
                Color.GREEN, Color.BLUE}, 
                MultipleGradientPaint.CycleMethod.NO_CYCLE);

        g2.setPaint(rgp);
        Ellipse2D circle = new Ellipse2D.Double(10, 10, 80, 80);
        g2.fill(circle);
    }
    
    private static void drawArcTest(Graphics2D g2) {
        g2.setPaint(Color.GREEN);
        g2.drawRect(0, 20, 70, 50);
        g2.setPaint(Color.RED);
        Path2D path1 = new Path2D.Double();
        double[] pts = calculateReferenceArc(90);
        path1.moveTo(pts[0], pts[1]);
        path1.curveTo(pts[2], pts[3], pts[4], pts[5], pts[6], pts[7]);
        AffineTransform t = new AffineTransform();
        t.translate(35, 45);
        t.scale(35, 25);
        t.rotate(Math.PI / 4);
        path1.transform(t);
        g2.draw(path1);

        Path2D path2 = new Path2D.Double();
        path2.moveTo(pts[0], pts[1]);
        path2.curveTo(pts[2], pts[3], pts[4], pts[5], pts[6], pts[7]);
        AffineTransform t2 = new AffineTransform();
        t2.rotate(3 * Math.PI / 4);
        t2.scale(35, 25);
        t2.translate(35, 35);
        path2.transform(t2);
        //g2.draw(path2);
        Path2D arc = new Path2D.Double();
        arc.append(path1, false);
        arc.append(path2, false);
        //g2.draw(arc);
        //g2.draw(path1);
        //g2.transform(t);
        g2.setPaint(Color.BLUE);
        g2.drawArc(0, 20, 70, 50, 0, -270);  
        //Arc2D arc2d = new Arc2D.Double(0d, 20d, 70d, 50d, 0d, 90, Arc2D.OPEN);
        //g2.draw(arc2d);
    }
    
    private static double[] calculateReferenceArc(double angle) {
        double a = (angle / 180 * Math.PI) / 2.0;
        double x0 = Math.cos(a);
        double y0 = Math.sin(a);
        double x1 = (4 - x0) / 3;
        double y1 = (1 - x0) * (3 - x0) / (3 * y0);
        double x2 = x1;
        double y2 = - y1;
        double x3 = x0;
        double y3 = -y0;
        return new double[] { x0, y0, x1, y1, x2, y2, x3, y3 };
    } 
    
    /**
     * Starting point for the demo.
     * 
     * @param args  ignored.
     * 
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(200, 200, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        drawClipTest(g2);
        //drawGradientPaintTest(g2);
        //drawRadialGradientPaintTest(g2);
        //drawArcTest(g2);
        ImageIO.write(image, "png", new File("clip-test.png"));
    }
}
