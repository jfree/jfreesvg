/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Some general shape utilities.
 */
public class Shapes {
    
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
}
