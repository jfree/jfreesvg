/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * Utility methods for shapes and images.
 */
public class GraphicsUtils {
    
    /**
     * Returns a shape that is more or less equivalent to the supplied shape.
     * 
     * @param shape  the shape (<code>null</code> not permitted).
     * 
     * @return A copy of the shape (it may not be the same class). 
     */
    public static Shape copyOf(Shape shape) {
       if (shape == null) {
           throw new IllegalArgumentException("Null 'shape' argument.");
       }
       if (shape instanceof Line2D) {
           Line2D l = (Line2D) shape;
           return new Line2D.Double(l.getX1(), l.getY1(), l.getX2(), l.getY2());
       }
       if (shape instanceof RoundRectangle2D) {
           RoundRectangle2D rr = (RoundRectangle2D) shape;
           return new RoundRectangle2D.Double(rr.getX(), rr.getY(), 
                   rr.getWidth(), rr.getHeight(), rr.getArcWidth(), 
                   rr.getArcHeight());
       }
       if (shape instanceof Rectangle2D) {
           Rectangle2D r = (Rectangle2D) shape;
           return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), 
                   r.getHeight());
       }
       return new Path2D.Double(shape);
    }

    /**
     * Converts a rendered image to a BufferedImage.  This utilty method
     * has come from a forum post by Jim Moore at 
     * http://www.jguru.com/faq/view.jsp?EID=114602.
     * 
     * @param img  the rendered image.
     * 
     * @return A buffered image. 
     */
    public static BufferedImage convertRenderedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;	
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, 
                isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }
}
