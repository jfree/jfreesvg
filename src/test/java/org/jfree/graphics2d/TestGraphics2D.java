/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Some tests for a Graphics2D implementation.
 */
public class TestGraphics2D {
    
    private Graphics2D g2;
    
    @Before
    public void setUp() {
        BufferedImage img = new BufferedImage(10, 20, BufferedImage.TYPE_INT_ARGB);
        //this.g2 = img.createGraphics();
        this.g2 = new SVGGraphics2D(10, 20);
        //this.g2 = new CanvasGraphics2D("id");
    }
    
    /**
     * Checks that the default transform is an identity transform.
     */
    @Test
    public void checkDefaultTransform() {
        Assert.assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * Modifying the transform returned by the Graphics2D should not affect
     * the state of the Graphics2D.  In order for that to happen, the method
     * should be returning a copy of the actual transform object.
     */
    @Test
    public void checkGetTransformSafety() {
        AffineTransform t = g2.getTransform();
        t.rotate(Math.PI);
        Assert.assertNotEquals(t, g2.getTransform());
        Assert.assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * A basic check that setTransform() does indeed update the transform.
     */
    @Test
    public void setTransform() {
        AffineTransform t = new AffineTransform(1, 2, 3, 4, 5, 6);
        g2.setTransform(t);
        Assert.assertEquals(t, g2.getTransform());
  
        t.setTransform(6, 5, 4, 3, 2, 1);
        g2.setTransform(t);
        Assert.assertEquals(t, g2.getTransform());
        
        // in spite of the docs saying that null is accepted this gives
        // a NullPointerException with SunGraphics2D.
        //g2.setTransform(null);
        //Assert.assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * When calling setTransform() the caller passes in an AffineTransform 
     * instance.  If the caller retains a reference to the AffineTransform 
     * and subsequently modifies it, we don't want the Graphics2D object to
     * be affected...so it should be making an internal copy of the 
     * AffineTransform.
     */
    @Test
    public void checkSetTransformSafety() {
        AffineTransform t = AffineTransform.getTranslateInstance(1.0, 2.0);
        g2.setTransform(t);
        Assert.assertEquals(t, g2.getTransform());
        t.setToRotation(Math.PI);
        Assert.assertNotEquals(t, g2.getTransform());
    }
    
    /**
     * Basic checks for the scale(x, y) method.
     */
    @Test
    public void scale() {
        g2.scale(0.5, 2.0);
        Assert.assertEquals(AffineTransform.getScaleInstance(0.5, 2.0), 
                g2.getTransform());
        g2.scale(2.0, -1.0);
        Assert.assertEquals(AffineTransform.getScaleInstance(1.0, -2.0), 
                g2.getTransform());    
    }
    
    /**
     * Checks that a call to scale(x, y) on top of an existing translation
     * gives the correct values.
     */
    @Test
    public void translateFollowedByScale() {
        g2.translate(2, 3);
        Assert.assertEquals(AffineTransform.getTranslateInstance(2.0, 3.0), 
                g2.getTransform());
        g2.scale(10, 20);
        Assert.assertEquals(new AffineTransform(10.0, 0.0, 0.0, 20.0, 2.0, 3.0),
                g2.getTransform());
    }
    
    /**
     * Checks that a call to translate(x, y) on top of an existing scale
     * gives the correct values.
     */    
    @Test
    public void scaleFollowedByTranslate() {
        g2.scale(2, 2);
        Assert.assertEquals(AffineTransform.getScaleInstance(2.0, 2.0), 
                g2.getTransform());
        g2.translate(10, 20);
        Assert.assertEquals(new AffineTransform(2.0, 0.0, 0.0, 2.0, 20.0, 40.0),
                g2.getTransform());
    }
    
    private static final double EPSILON = 0.000000001;
    
    @Test
    public void scaleFollowedByRotate() {
        g2.scale(2, 2);
        Assert.assertEquals(AffineTransform.getScaleInstance(2.0, 2.0), 
                g2.getTransform());
        g2.rotate(Math.PI / 3);
        AffineTransform t = g2.getTransform();
        Assert.assertEquals(1.0, t.getScaleX(), EPSILON);
        Assert.assertEquals(1.0, t.getScaleY(), EPSILON);
        Assert.assertEquals(-1.7320508075688772, t.getShearX(), EPSILON);
        Assert.assertEquals(1.7320508075688772, t.getShearY(), EPSILON);
        Assert.assertEquals(0.0, t.getTranslateX(), EPSILON);
        Assert.assertEquals(0.0, t.getTranslateY(), EPSILON);
    }
    
    @Test
    public void rotateFollowedByScale() {
        g2.rotate(Math.PI);
        Assert.assertEquals(AffineTransform.getRotateInstance(Math.PI), 
                g2.getTransform());
        g2.scale(2.0, 2.0);
        Assert.assertEquals(new AffineTransform(-2.0, 0.0, 0.0, -2.0, 0.0, 0.0),
                g2.getTransform());
    }
    
    /**
     * Checks that the getClip() method returns a different object than what
     * was passed to setClip(), and that multiple calls to getClip() return
     * a new object each time.
     */
    @Test
    public void checkGetClipSafety() {
        Rectangle2D r = new Rectangle2D.Double(0, 0, 1, 1);
        this.g2.setClip(r);
        Shape s = this.g2.getClip();
        Assert.assertFalse(r == s);
        Shape s2 = this.g2.getClip();
        Assert.assertFalse(s == s2);
    }
    
    /**
     * The default user clip should be <code>null</code>.
     */
    @Test
    public void checkDefaultClip() {
        Assert.assertNull("Default user clip should be null.", g2.getClip());
    }
    
    /**
     * Checks that getClipBounds() is returning an integer approximation of
     * the bounds.
     */
    @Test
    public void checkGetClipBounds() {
        Rectangle2D r = new Rectangle2D.Double(0.25, 0.25, 0.5, 0.5);
        this.g2.setClip(r);
        Assert.assertEquals(new Rectangle(0, 0, 1, 1), this.g2.getClipBounds());       
    }

    /**
     * Simple check that the clip() methods creates an intersection with the
     * existing clip region.
     */
    @Test
    public void checkClip() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Rectangle2D.Double(0.0, 0.0, 2.0, 2.0));
        Assert.assertEquals(new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0), 
                this.g2.getClip().getBounds2D());
    }
    
    @Test
    public void checkNonIntersectingClip() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Rectangle2D.Double(5.0, 5.0, 1.0, 1.0));
        Assert.assertTrue(this.g2.getClip().getBounds2D().isEmpty());
    }

    /**
     * After applying a scale transformation, getClip() will return a
     * modified clip.
     */
    @Test
    public void checkClipAfterScaling() {
        Rectangle2D r = new Rectangle2D.Double(1, 2, 3, 0.5);
        this.g2.setClip(r);
        Assert.assertEquals(r, this.g2.getClip().getBounds2D());
        this.g2.scale(0.5, 2.0);
        Assert.assertEquals(new Rectangle2D.Double(2, 1, 6, 0.25), 
                this.g2.getClip().getBounds2D());

        // check that we get a good intersection when clipping after the
        // scaling has been done...
        r = new Rectangle2D.Double(3, 0, 2, 2);
        this.g2.clip(r);
        Assert.assertEquals(new Rectangle2D.Double(3, 1, 2, 0.25), 
                this.g2.getClip().getBounds2D());
    }
    
    /** 
     * Translating will change the existing clip.
     */
    @Test
    public void checkClipAfterTranslate() {
        Rectangle2D clip = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        this.g2.setClip(clip);
        Assert.assertEquals(clip, this.g2.getClip().getBounds2D());
        this.g2.translate(1.0, 2.0);
        Assert.assertEquals(new Rectangle(-1, -2, 1 ,1), 
                this.g2.getClip().getBounds2D());
    }
    
    /**
     * Transforming will change the reported clipping shape.
     */
    @Test
    public void checkClipAfterTransform() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 1, 1);
        this.g2.setClip(clip);
        Assert.assertEquals(clip, this.g2.getClip().getBounds2D());
        
        this.g2.transform(AffineTransform.getRotateInstance(Math.PI));
        Assert.assertEquals(new Rectangle(-1, -1, 1 ,1), 
                this.g2.getClip().getBounds2D());
        
        this.g2.setTransform(new AffineTransform());
        Assert.assertEquals(clip, this.g2.getClip().getBounds2D());     
    }
    
    /**
     * Clipping with a line makes no sense, but the API allows it so we should
     * not fail.  In fact, running with a JDK Graphcis2D (from a BufferedImage)
     * it seems that the bounding rectangle of the line is used for clipping...
     * does that make sense?  Switching off the test for now.
     */
    @Test
    @Ignore
    public void checkClipWithLine2D() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        //Assert.assertEquals(new Rectangle2D.Double(1.0, 2.0, 2.0, 2.0), 
        //        this.g2.getClip().getBounds2D());
        //Assert.assertTrue(this.g2.getClip().getBounds2D().isEmpty());        
    }
    
    /**
     * A simple check for a call to clipRect().
     */
    @Test
    public void checkClipRect() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 5, 5);
        this.g2.setClip(clip);
        
        this.g2.clipRect(2, 1, 4, 2);
        Assert.assertEquals(new Rectangle(2, 1, 3, 2), 
                g2.getClip().getBounds2D());
    }
    
    public void checkClipRectParams() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 5, 5);
        this.g2.setClip(clip);
        
        // negative width
        this.g2.clipRect(2, 1, -4, 2);
        Assert.assertTrue(this.g2.getClip().getBounds2D().isEmpty());
        
        // negative height
        this.g2.setClip(clip);
        this.g2.clipRect(2, 1, 4, -2);
        Assert.assertTrue(this.g2.getClip().getBounds2D().isEmpty());    
    }
    
}
