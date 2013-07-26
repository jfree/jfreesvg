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

package org.jfree.graphics2d;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
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
    
    private GraphicsUtils() {
        // no need to instantiate this
    }
    
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
       if (shape instanceof Rectangle2D) {
           Rectangle2D r = (Rectangle2D) shape;
           return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), 
                   r.getHeight());
       }
       if (shape instanceof RoundRectangle2D) {
           RoundRectangle2D rr = (RoundRectangle2D) shape;
           return new RoundRectangle2D.Double(rr.getX(), rr.getY(), 
                   rr.getWidth(), rr.getHeight(), rr.getArcWidth(), 
                   rr.getArcHeight());
       }
       if (shape instanceof Arc2D) {
           Arc2D arc = (Arc2D) shape;
           return new Arc2D.Double(arc.getX(), arc.getY(), arc.getWidth(),
                   arc.getHeight(), arc.getAngleStart(), arc.getAngleExtent(),
                   arc.getArcType());
       }
       if (shape instanceof Ellipse2D) {
           Ellipse2D ell = (Ellipse2D) shape;
           return new Ellipse2D.Double(ell.getX(), ell.getY(), ell.getWidth(),
                   ell.getHeight());
       }
       if (shape instanceof Polygon) {
           Polygon p = (Polygon) shape;
           return new Polygon(p.xpoints, p.ypoints, p.npoints);
       }
       return new Path2D.Double(shape);
    }

    /**
     * Creates a polygon for from the specified <code>x</code> and 
     * <code>y</code> coordinates.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polyline.
     * @param close  closed?
     * 
     * @return A polygon.
     */
    public static GeneralPath createPolygon(int[] xPoints, int[] yPoints, 
            int nPoints, boolean close) {
        GeneralPath p = new GeneralPath();
        p.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            p.lineTo(xPoints[i], yPoints[i]);
        }
        if (close) {
            p.closePath();
        }
        return p;
    }

    /**
     * Converts a rendered image to a <code>BufferedImage</code>.  This utility
     * method has come from a forum post by Jim Moore at:
     * <p>
     * <a href="http://www.jguru.com/faq/view.jsp?EID=114602">
     * http://www.jguru.com/faq/view.jsp?EID=114602</a>
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
