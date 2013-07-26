/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d;

import java.awt.BasicStroke;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.graphics2d.pdf.PDFDocument;
import org.jfree.graphics2d.pdf.Page;
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
        // to test a reference implementation, use this Graphics2D from a
        // BufferedImage in the JDK
        //BufferedImage img = new BufferedImage(10, 20, BufferedImage.TYPE_INT_ARGB);
        //this.g2 = img.createGraphics();
        
        // Test SVGGraphics2D...
        //this.g2 = new SVGGraphics2D(10, 20);
 
        // Test PDFGraphics2D...
        PDFDocument pdfDoc = new PDFDocument();
        Page page = pdfDoc.createPage(new Rectangle(0, 0, 300, 200));
        this.g2 = page.getGraphics2D();

        // Test CanvasGraphics2D...
        //this.g2 = new CanvasGraphics2D("id");
    }
    
    /**
     * Checks that the default transform is an identity transform.
     */
    @Test
    public void checkDefaultTransform() {
        assertEquals(new AffineTransform(), g2.getTransform());
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
    
    @Test
    public void checkSetNonInvertibleTransform() {
        AffineTransform t = AffineTransform.getScaleInstance(0.0, 0.0);
        g2.setTransform(t);
        Assert.assertEquals(t, g2.getTransform());
        
        // after setting the clip, we cannot retrieve it while the transform
        // is non-invertible...
        Rectangle2D clip = new Rectangle2D.Double(1, 2, 3, 4);
        g2.setClip(clip);
        Assert.assertNull(g2.getClip());
        
        g2.setTransform(new AffineTransform());
        Assert.assertEquals(new Rectangle2D.Double(0, 0, 0, 0), 
                g2.getClip().getBounds2D());
    }

    /**
     * A check for a call to transform() with a rotation, that follows a
     * translation.
     */
    @Test
    public void checkTransform() {
        AffineTransform t = new AffineTransform();
        this.g2.setTransform(t);
        this.g2.translate(30, 30);
        AffineTransform rt = AffineTransform.getRotateInstance(Math.PI / 2.0, 300, 200);
        this.g2.transform(rt);
        t = this.g2.getTransform();
        assertEquals(0, t.getScaleX(), EPSILON);
        assertEquals(0, t.getScaleY(), EPSILON);
        assertEquals(-1.0, t.getShearX(), EPSILON);
        assertEquals(1.0, t.getShearY(), EPSILON);
        assertEquals(530.0, t.getTranslateX(), EPSILON);
        assertEquals(-70, t.getTranslateY(), EPSILON);
    }
    
    @Test
    public void checkTransformNull() {
        try {
            this.g2.transform(null);
            Assert.fail("Expected a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
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
    
    @Test
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
    
    @Test
    public void checkDrawStringWithNullString() {
        try {
            g2.drawString((String) null, 1, 2);
            Assert.fail("There should be a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
        try {
            g2.drawString((String) null, 1.0f, 2.0f);
            Assert.fail("There should be a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
    }
    
    /**
     * Some checks for the create() method.
     */
    @Test 
    public void checkCreate() {
        this.g2.setClip(new Rectangle(1, 2, 3, 4));
        Graphics2D copy = (Graphics2D) g2.create();
        assertEquals(copy.getBackground(), g2.getBackground());
        assertEquals(copy.getClip().getBounds2D(), 
                g2.getClip().getBounds2D());
        assertEquals(copy.getColor(), g2.getColor());
        assertEquals(copy.getComposite(), g2.getComposite());
        assertEquals(copy.getFont(), g2.getFont());
        assertEquals(copy.getRenderingHints(), g2.getRenderingHints());        
        assertEquals(copy.getStroke(), g2.getStroke()); 
        assertEquals(copy.getTransform(), g2.getTransform()); 
    }
    
    /**
     * The setPaint() method allows a very minor state leakage in the sense 
     * that it is possible to modify a GradientPaint externally after a call
     * to the setPaint() method and have it impact the state of the 
     * Graphics2D implementation.  Avoiding this would require cloning the
     * Paint object, but there is no good way to do that for an arbitrary
     * Paint instance.  
     */
    @Test
    public void checkSetPaintSafety() {
        Point2D pt1 = new Point2D.Double(1.0, 2.0);
        Point2D pt2 = new Point2D.Double(3.0, 4.0);
        GradientPaint gp = new GradientPaint(pt1, Color.RED, pt2, Color.BLUE);
        this.g2.setPaint(gp);
        assertEquals(gp, this.g2.getPaint());
        Assert.assertTrue(gp == this.g2.getPaint());
        pt1.setLocation(7.0, 7.0);
        assertEquals(gp, this.g2.getPaint());
    }
    
    /**
     * According to the Javadocs, setting the paint to null should have no 
     * impact on the current paint (that is, the call is silently ignored).
     */
    @Test
    public void checkSetPaintNull() {
        this.g2.setPaint(Color.RED);
        // this next call should have no impact
        this.g2.setPaint(null);
        assertEquals(Color.RED, this.g2.getPaint());
    }
    
    /**
     * Passing a Color to setPaint() also updates the color, but not the
     * background color.
     */
    @Test
    public void checkSetPaintAlsoUpdatesColorButNotBackground() {
        Color existingBackground = this.g2.getBackground();
        this.g2.setPaint(Color.MAGENTA);
        assertEquals(Color.MAGENTA, this.g2.getPaint());
        assertEquals(Color.MAGENTA, this.g2.getColor());
        assertEquals(existingBackground, this.g2.getBackground());
    }
    
    @Test
    public void checkSetColorAlsoUpdatesPaint() {
        this.g2.setColor(Color.MAGENTA);
        assertEquals(Color.MAGENTA, this.g2.getPaint());
        assertEquals(Color.MAGENTA, this.g2.getColor());
    }
    
    /**
     * The behaviour of the reference implementation has been observed as
     * ignoring null.  This matches the documented behaviour of the
     * setPaint() method.
     */
    @Test
    public void checkSetColorNull() {
        this.g2.setColor(Color.RED);
        this.g2.setColor(null);
        assertEquals(Color.RED, this.g2.getColor());
    }
    
    /**
     * Setting the background color does not change the color or paint.
     */
    @Test
    public void checkSetBackground() {
        this.g2.setBackground(Color.CYAN);
        assertEquals(Color.CYAN, this.g2.getBackground());
        assertFalse(Color.CYAN.equals(this.g2.getColor()));
        assertFalse(Color.CYAN.equals(this.g2.getPaint()));
    }

    /**
     * The behaviour of the reference implementation has been observed as
     * allowing null (this is inconsistent with the behaviour of setColor()).
     */
    @Test
    public void checkSetBackgroundNull() {
        this.g2.setBackground(Color.RED);
        this.g2.setBackground(null);
        assertEquals(null, this.g2.getBackground());
    }
    
    /**
     * Since the setBackground() method is allowing null, we should ensure
     * that the clearRect() method doesn't fail in this case.  With no
     * background color, the clearRect() method should be a no-op but there
     * is no easy way to test for that.
     */
    @Test
    public void checkClearRectWithNullBackground() {
        this.g2.setBackground(null);
        this.g2.clearRect(1, 2, 3, 4);
        //no exceptions and we're good
    }

    /**
     * In the reference implementation, setting a null composite has been 
     * observed to throw an IllegalArgumentException.
     */
    @Test
    public void checkSetCompositeNull() {
        try {
            this.g2.setComposite(null);
            Assert.fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // this exception is expected in the test   
        }
    }
    
    @Test
    public void checkSetStrokeNull() {
        try {
            this.g2.setStroke(null);
            Assert.fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // this exception is expected in the test   
        }
    }
    
    /**
     * Basic check of set then get.
     */
    @Test
    public void checkSetRenderingHint() {
        this.g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                RenderingHints.VALUE_STROKE_PURE);
        assertEquals(RenderingHints.VALUE_STROKE_PURE, 
                this.g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL));
    }
    
    /**
     * The reference implementation has been observed to throw a 
     * NullPointerException when the key is null.
     */
    @Test
    public void checkSetRenderingHintWithNullKey() {
        try {
            this.g2.setRenderingHint(null, "XYZ");
            Assert.fail("NullPointerException is expected here.");
        } catch (NullPointerException e) {
            // this is expected
        }
    }
    
    /**
     * The reference implementation has been observed to accept a null key 
     * and return null in that case.
     */
    @Test
    public void checkGetRenderingHintWithNullKey() {
        assertNull(this.g2.getRenderingHint(null));
    }
    
    /**
     * Check setting a hint with a value that doesn't match the key.
     */
    @Test
    public void checkSetRenderingHintWithInconsistentValue() {
        try {
            this.g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                    RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            Assert.fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // we expect this exception
        }
    }
    
    /**
     * A call to getRenderingHints() is returning a copy of the hints, so 
     * changing it will not affect the state of the Graphics2D instance.
     */
    @Test
    public void checkGetRenderingHintsSafety() {
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_OFF);
        RenderingHints hints = this.g2.getRenderingHints();
        hints.put(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        assertEquals(RenderingHints.VALUE_ANTIALIAS_OFF, 
                this.g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING));   
    }
    
    @Test
    public void checkSetRenderingHintsNull() {
        try {
            this.g2.setRenderingHints(null);
            Assert.fail("NullPointerException expected.");
        } catch (NullPointerException e) {
            // this is expected
        }
    }
    
    @Test
    public void checkHit() {
        Shape shape = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        Rectangle r = new Rectangle(2, 2, 2, 2);
        assertFalse(this.g2.hit(r, shape, false));
        this.g2.scale(3.0, 3.0);
        assertTrue(this.g2.hit(r, shape, false));
    }
    
    @Test
    public void checkHitForOutline() {
        Shape shape = new Rectangle2D.Double(0.0, 0.0, 3.0, 3.0);
        Rectangle r = new Rectangle(1, 1, 1, 1);
        assertFalse(this.g2.hit(r, shape, true));
        this.g2.scale(0.5, 0.5);
        // now the rectangle is entirely inside the shape, but does not touch
        // the outline...
        assertTrue(this.g2.hit(r, shape, true));
    } 
    
    /**
     * We have observed in the reference implementation that setting the font
     * to null does not change the current font setting.
     */
    @Test
    public void checkSetFontNull() {
        Font f = new Font("Serif", Font.PLAIN, 8);
        this.g2.setFont(f);
        assertEquals(f, this.g2.getFont());
        this.g2.setFont(null);
        assertEquals(f, this.g2.getFont());
    }
    
    @Test
    public void checkDefaultStroke() {
        BasicStroke s = (BasicStroke) this.g2.getStroke();
        assertEquals(BasicStroke.CAP_SQUARE, s.getEndCap());
        assertEquals(1.0f, s.getLineWidth(), EPSILON);
        assertEquals(BasicStroke.JOIN_MITER, s.getLineJoin());
    }
}