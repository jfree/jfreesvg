/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 *
 * (C)opyright 2013-present, by David Gilbert.  All rights reserved.
 *
 * Project Info:  https://www.jfree.org/jfreesvg/index.html
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 * 
 * If you do not wish to be bound by the terms of the GPL, an alternative
 * commercial license can be purchased.  For details, please see visit the
 * JFreeSVG home page:
 * 
 * https://www.jfree.org/jfreesvg
 * 
 */

package org.jfree.svg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Some tests for a Graphics2D implementation.  All tests should pass with the
 * Graphics2D instance from a BufferedImage (which we can treat as a reference
 * implementation).
 */
class TestSVGGraphics2D {
    
    /** 
     * Change this to true to test against a reference Graphics2D 
     * implementation from the JDK.  This is useful to verify that the tests
     * are correct.
     */
    private static final boolean TEST_REFERENCE_IMPLEMENTATION = false;
    
    private Graphics2D g2;
    
    @BeforeEach
    public void setUp() {
        if (TEST_REFERENCE_IMPLEMENTATION) {
            // to test a reference implementation, use this Graphics2D from a
            // BufferedImage in the JDK
            BufferedImage img = new BufferedImage(10, 20, BufferedImage.TYPE_INT_ARGB);
            this.g2 = img.createGraphics();
        } else {
            // Test SVGGraphics2D...
            this.g2 = new SVGGraphics2D(10.0, 20.0);
        }
    }
    
    /**
     * Checks that the default transform is an identity transform.
     */
    @Test
    void checkDefaultTransform() {
        assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * Modifying the transform returned by the Graphics2D should not affect
     * the state of the Graphics2D.  In order for that to happen, the method
     * should be returning a copy of the actual transform object.
     */
    @Test
    void checkGetTransformSafety() {
        AffineTransform t = g2.getTransform();
        t.rotate(Math.PI);
        assertNotEquals(t, g2.getTransform());
        assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * A basic check that setTransform() does indeed update the transform.
     */
    @Test
    void setTransform() {
        AffineTransform t = new AffineTransform(1, 2, 3, 4, 5, 6);
        g2.setTransform(t);
        assertEquals(t, g2.getTransform());
  
        t.setTransform(6, 5, 4, 3, 2, 1);
        g2.setTransform(t);
        assertEquals(t, g2.getTransform());
        
        // in spite of the docs saying that null is accepted this gives
        // a NullPointerException with SunGraphics2D.
        //g2.setTransform(null);
        //assertEquals(new AffineTransform(), g2.getTransform());
    }
    
    /**
     * When calling setTransform() the caller passes in an AffineTransform 
     * instance.  If the caller retains a reference to the AffineTransform 
     * and subsequently modifies it, we don't want the Graphics2D object to
     * be affected...so it should be making an internal copy of the 
     * AffineTransform.
     */
    @Test
    void checkSetTransformSafety() {
        AffineTransform t = AffineTransform.getTranslateInstance(1.0, 2.0);
        g2.setTransform(t);
        assertEquals(t, g2.getTransform());
        t.setToRotation(Math.PI);
        assertNotEquals(t, g2.getTransform());
    }
    
    @Test
    void checkSetNonInvertibleTransform() {
        AffineTransform t = AffineTransform.getScaleInstance(0.0, 0.0);
        g2.setTransform(t);
        assertEquals(t, g2.getTransform());
        
        // after setting the clip, we cannot retrieve it while the transform
        // is non-invertible...
        Rectangle2D clip = new Rectangle2D.Double(1, 2, 3, 4);
        g2.setClip(clip);
        assertNull(g2.getClip());
        
        g2.setTransform(new AffineTransform());
        assertEquals(new Rectangle2D.Double(0, 0, 0, 0), 
                g2.getClip().getBounds2D());
    }

    /**
     * A check for a call to transform() with a rotation, that follows a
     * translation.
     */
    @Test
    void checkTransform() {
        AffineTransform t = new AffineTransform();
        this.g2.setTransform(t);
        this.g2.translate(30, 30);
        AffineTransform rt = AffineTransform.getRotateInstance(Math.PI / 2.0, 
                300, 200);
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
    void checkTransformNull() {
        try {
            this.g2.transform(null);
            fail("Expected a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
    }
    
    /**
     * Basic checks for the scale(x, y) method.
     */
    @Test
    void scale() {
        g2.scale(0.5, 2.0);
        assertEquals(AffineTransform.getScaleInstance(0.5, 2.0), 
                g2.getTransform());
        g2.scale(2.0, -1.0);
        assertEquals(AffineTransform.getScaleInstance(1.0, -2.0), 
                g2.getTransform());    
    }
    
    /**
     * Checks that a call to scale(x, y) on top of an existing translation
     * gives the correct values.
     */
    @Test
    void translateFollowedByScale() {
        g2.translate(2, 3);
        assertEquals(AffineTransform.getTranslateInstance(2.0, 3.0), 
                g2.getTransform());
        g2.scale(10, 20);
        assertEquals(new AffineTransform(10.0, 0.0, 0.0, 20.0, 2.0, 3.0),
                g2.getTransform());
    }
    
    /**
     * Checks that a call to translate(x, y) on top of an existing scale
     * gives the correct values.
     */    
    @Test
    void scaleFollowedByTranslate() {
        g2.scale(2, 2);
        assertEquals(AffineTransform.getScaleInstance(2.0, 2.0), 
                g2.getTransform());
        g2.translate(10, 20);
        assertEquals(new AffineTransform(2.0, 0.0, 0.0, 2.0, 20.0, 40.0),
                g2.getTransform());
    }
    
    private static final double EPSILON = 0.000000001;
    
    @Test
    void scaleFollowedByRotate() {
        g2.scale(2, 2);
        assertEquals(AffineTransform.getScaleInstance(2.0, 2.0), 
                g2.getTransform());
        g2.rotate(Math.PI / 3);
        AffineTransform t = g2.getTransform();
        assertEquals(1.0, t.getScaleX(), EPSILON);
        assertEquals(1.0, t.getScaleY(), EPSILON);
        assertEquals(-1.7320508075688772, t.getShearX(), EPSILON);
        assertEquals(1.7320508075688772, t.getShearY(), EPSILON);
        assertEquals(0.0, t.getTranslateX(), EPSILON);
        assertEquals(0.0, t.getTranslateY(), EPSILON);
    }
    
    @Test
    void rotateFollowedByScale() {
        g2.rotate(Math.PI);
        assertEquals(AffineTransform.getRotateInstance(Math.PI), 
                g2.getTransform());
        g2.scale(2.0, 2.0);
        assertEquals(new AffineTransform(-2.0, 0.0, 0.0, -2.0, 0.0, 0.0),
                g2.getTransform());
    }
    
    /**
     * Checks that the getClip() method returns a different object than what
     * was passed to setClip(), and that multiple calls to getClip() return
     * a new object each time.
     */
    @Test
    void checkGetClipSafety() {
        Rectangle2D r = new Rectangle2D.Double(0, 0, 1, 1);
        this.g2.setClip(r);
        Shape s = this.g2.getClip();
        assertNotSame(r, s);
        Shape s2 = this.g2.getClip();
        assertNotSame(s, s2);
    }
    
    /**
     * The default user clip should be {@code null}.
     */
    @Test
    void checkDefaultClip() {
        assertNull(g2.getClip(), "Default user clip should be null.");
    }
    
    /**
     * Checks that getClipBounds() is returning an integer approximation of
     * the bounds.
     */
    @Test
    void checkGetClipBounds() {
        Rectangle2D r = new Rectangle2D.Double(0.25, 0.25, 0.5, 0.5);
        this.g2.setClip(r);
        assertEquals(new Rectangle(0, 0, 1, 1), this.g2.getClipBounds());       
    }

    /**
     * Checks that getClipBounds() returns {@code null} when the clip is
     * {@code null}.
     */
    @Test
    void checkGetClipBoundsWhenClipIsNull() {
        this.g2.setClip(null);
        assertNull(this.g2.getClipBounds());
    }

    /**
     * Simple check that the clip() methods creates an intersection with the
     * existing clip region.
     */
    @Test
    void checkClip() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Rectangle2D.Double(0.0, 0.0, 2.0, 2.0));
        assertEquals(new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0), 
                this.g2.getClip().getBounds2D());
    }

    /**
     * Check that if the user clip is non-intersecting with the existing clip, then
     * the clip is empty.
     */
    @Test
    void checkNonIntersectingClip() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Rectangle2D.Double(5.0, 5.0, 1.0, 1.0));
        assertTrue(this.g2.getClip().getBounds2D().isEmpty());
    }

    /**
     * After applying a scale transformation, getClip() will return a
     * modified clip.
     */
    @Test
    void checkClipAfterScaling() {
        Rectangle2D r = new Rectangle2D.Double(1, 2, 3, 0.5);
        this.g2.setClip(r);
        assertEquals(r, this.g2.getClip().getBounds2D());
        this.g2.scale(0.5, 2.0);
        assertEquals(new Rectangle2D.Double(2, 1, 6, 0.25), 
                this.g2.getClip().getBounds2D());

        // check that we get a good intersection when clipping after the
        // scaling has been done...
        r = new Rectangle2D.Double(3, 0, 2, 2);
        this.g2.clip(r);
        assertEquals(new Rectangle2D.Double(3, 1, 2, 0.25), 
                this.g2.getClip().getBounds2D());
    }
    
    /** 
     * Translating will change the existing clip.
     */
    @Test
    void checkClipAfterTranslate() {
        Rectangle2D clip = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        this.g2.setClip(clip);
        assertEquals(clip, this.g2.getClip().getBounds2D());
        this.g2.translate(1.0, 2.0);
        assertEquals(new Rectangle(-1, -2, 1 ,1), 
                this.g2.getClip().getBounds2D());
    }
    
    @Test
    void checkSetClipAfterTranslate() {
        this.g2.translate(1.0, 2.0);
        this.g2.setClip(0, 0, 1, 1);
        assertEquals(new Rectangle(0, 0, 1, 1), this.g2.getClip().getBounds());
        this.g2.translate(1.0, 2.0);
        assertEquals(new Rectangle(-1, -2, 1, 1), this.g2.getClip().getBounds());
    }
    
    /**
     * Transforming will change the reported clipping shape.
     */
    @Test
    void checkClipAfterTransform() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 1, 1);
        this.g2.setClip(clip);
        assertEquals(clip, this.g2.getClip().getBounds2D());
        
        this.g2.transform(AffineTransform.getRotateInstance(Math.PI));
        assertEquals(new Rectangle(-1, -1, 1 ,1), 
                this.g2.getClip().getBounds2D());
        
        this.g2.setTransform(new AffineTransform());
        assertEquals(clip, this.g2.getClip().getBounds2D());     
    }
    
    /**
     * Clipping with a line makes no sense, but the API allows it so we should
     * not fail.  In fact, running with a JDK Graphics2D (from a BufferedImage)
     * it seems that the bounding rectangle of the line is used for clipping...
     * does that make sense?  Matching the behaviour for now.
     */
    @Test
    void checkClipWithLine2D() {
        Rectangle2D r = new Rectangle2D.Double(1.0, 1.0, 3.0, 3.0);
        this.g2.setClip(r);
        this.g2.clip(new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        assertEquals(new Rectangle2D.Double(1.0, 2.0, 2.0, 2.0), 
                this.g2.getClip().getBounds2D());
        //assertTrue(this.g2.getClip().getBounds2D().isEmpty());        
    }
    
    /**
     * Clipping with a null argument is "not recommended" according to the 
     * latest API docs (https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6206189).
     */
    @Test
    void checkClipWithNullArgument() {
        // when there is a current clip set, a null pointer exception is expected
        this.g2.setClip(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertThrows(NullPointerException.class, () -> this.g2.clip(null));
        
        this.g2.setClip(null);
        try {
            this.g2.clip(null);
        } catch (Exception e) {
            fail("No exception expected.");             
        }
    }
    
    /**
     * A simple check for a call to clipRect().
     */
    @Test
    void checkClipRect() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 5, 5);
        this.g2.setClip(clip);
        
        this.g2.clipRect(2, 1, 4, 2);
        assertEquals(new Rectangle(2, 1, 3, 2), 
                g2.getClip().getBounds2D());
    }
    
    @Test
    void checkClipRectParams() {
        Rectangle2D clip = new Rectangle2D.Double(0, 0, 5, 5);
        this.g2.setClip(clip);
        
        // negative width
        this.g2.clipRect(2, 1, -4, 2);
        assertTrue(this.g2.getClip().getBounds2D().isEmpty());
        
        // negative height
        this.g2.setClip(clip);
        this.g2.clipRect(2, 1, 4, -2);
        assertTrue(this.g2.getClip().getBounds2D().isEmpty());    
    }

    @Test
    void checkDrawStringWithNullString() {
        try {
            g2.drawString((String) null, 1, 2);
            fail("There should be a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
        try {
            g2.drawString((String) null, 1.0f, 2.0f);
            fail("There should be a NullPointerException.");
        } catch (NullPointerException e) {
            // this exception is expected
        }
    }
    
    @Test
    void checkDrawStringWithEmptyString() {
        // this should not cause any exception 
        g2.setRenderingHint(SVGHints.KEY_DRAW_STRING_TYPE, SVGHints.VALUE_DRAW_STRING_TYPE_VECTOR);
        g2.drawString("", 1, 2);
        g2.setRenderingHint(SVGHints.KEY_DRAW_STRING_TYPE, null);
        g2.drawString("", 1, 2);
    }

    /**
     * Some checks for the create() method.
     */
    @Test
    void checkCreate() {
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
    void checkSetPaintSafety() {
        Point2D pt1 = new Point2D.Double(1.0, 2.0);
        Point2D pt2 = new Point2D.Double(3.0, 4.0);
        GradientPaint gp = new GradientPaint(pt1, Color.RED, pt2, Color.BLUE);
        this.g2.setPaint(gp);
        assertEquals(gp, this.g2.getPaint());
        assertSame(gp, this.g2.getPaint());
        pt1.setLocation(7.0, 7.0);
        assertEquals(gp, this.g2.getPaint());
    }
    
    /**
     * According to the Javadocs, setting the paint to null should have no 
     * impact on the current paint (that is, the call is silently ignored).
     */
    @Test
    void checkSetPaintNull() {
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
    void checkSetPaintAlsoUpdatesColorButNotBackground() {
        Color existingBackground = this.g2.getBackground();
        this.g2.setPaint(Color.MAGENTA);
        assertEquals(Color.MAGENTA, this.g2.getPaint());
        assertEquals(Color.MAGENTA, this.g2.getColor());
        assertEquals(existingBackground, this.g2.getBackground());
    }
    
    /**
     * If setPaint() is called with an argument that is not an instance of
     * Color, then the existing color remains unchanged.
     */
    @Test
    void checkSetPaintDoesNotUpdateColor() {
        GradientPaint gp = new GradientPaint(1.0f, 2.0f, Color.RED, 
                3.0f, 4.0f, Color.BLUE);
        this.g2.setColor(Color.MAGENTA);
        this.g2.setPaint(gp);
        assertEquals(gp, this.g2.getPaint());
        assertEquals(Color.MAGENTA, this.g2.getColor());
    }    
    
    /**
     * Verifies that setting the old AWT color attribute also updates the
     * Java2D paint attribute.
     * 
     * @see #checkSetPaintAlsoUpdatesColorButNotBackground() 
     */
    @Test
    void checkSetColorAlsoUpdatesPaint() {
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
    void checkSetColorNull() {
        this.g2.setColor(Color.RED);
        this.g2.setColor(null);
        assertEquals(Color.RED, this.g2.getColor());
    }
    
    /**
     * Setting the background color does not change the color or paint.
     */
    @Test
    void checkSetBackground() {
        this.g2.setBackground(Color.CYAN);
        assertEquals(Color.CYAN, this.g2.getBackground());
        assertNotEquals(Color.CYAN, this.g2.getColor());
        assertNotEquals(Color.CYAN, this.g2.getPaint());
    }

    /**
     * The behaviour of the reference implementation has been observed as
     * allowing null (this is inconsistent with the behaviour of setColor()).
     */
    @Test
    void checkSetBackgroundNull() {
        this.g2.setBackground(Color.RED);
        this.g2.setBackground(null);
        assertNull(this.g2.getBackground());
    }
    
    /**
     * Since the setBackground() method is allowing null, we should ensure
     * that the clearRect() method doesn't fail in this case.  With no
     * background color, the clearRect() method should be a no-op but there
     * is no easy way to test for that.
     */
    @Test
    void checkClearRectWithNullBackground() {
        this.g2.setBackground(null);
        this.g2.clearRect(1, 2, 3, 4);
        //no exceptions and we're good
    }

    /**
     * In the reference implementation, setting a null composite has been 
     * observed to throw an IllegalArgumentException.
     */
    @Test
    void checkSetCompositeNull() {
        try {
            this.g2.setComposite(null);
            fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // this exception is expected in the test   
        }
    }
    
    @Test
    void checkSetStrokeNull() {
        try {
            this.g2.setStroke(null);
            fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // this exception is expected in the test   
        }
    }
    
    /**
     * Basic check of set then get.
     */
    @Test
    void checkSetRenderingHint() {
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
    void checkSetRenderingHintWithNullKey() {
        try {
            this.g2.setRenderingHint(null, "XYZ");
            fail("NullPointerException is expected here.");
        } catch (NullPointerException e) {
            // this is expected
        }
    }
    
    /**
     * The reference implementation has been observed to accept a null key 
     * and return null in that case.
     */
    @Test
    void checkGetRenderingHintWithNullKey() {
        assertNull(this.g2.getRenderingHint(null));
    }
    
    /**
     * Check setting a hint with a value that doesn't match the key.
     */
    @Test
    void checkSetRenderingHintWithInconsistentValue() {
        try {
            this.g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                    RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // we expect this exception
        }
    }
    
    /**
     * A call to getRenderingHints() is returning a copy of the hints, so 
     * changing it will not affect the state of the Graphics2D instance.
     */
    @Test
    void checkGetRenderingHintsSafety() {
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_OFF);
        RenderingHints hints = this.g2.getRenderingHints();
        hints.put(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        assertEquals(RenderingHints.VALUE_ANTIALIAS_OFF, 
                this.g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING));   
    }
    
    @Test
    void checkSetRenderingHintsNull() {
        try {
            this.g2.setRenderingHints(null);
            fail("NullPointerException expected.");
        } catch (NullPointerException e) {
            // this is expected
        }
    }
    
    @Test
    void checkHit() {
        Shape shape = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        Rectangle r = new Rectangle(2, 2, 2, 2);
        assertFalse(this.g2.hit(r, shape, false));
        this.g2.scale(3.0, 3.0);
        assertTrue(this.g2.hit(r, shape, false));
    }
    
    @Test
    void checkHitForOutline() {
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
    void checkSetFontNull() {
        Font f = new Font("Serif", Font.PLAIN, 8);
        this.g2.setFont(f);
        assertEquals(f, this.g2.getFont());
        this.g2.setFont(null);
        assertEquals(f, this.g2.getFont());
    }
    
    @Test
    void checkDefaultStroke() {
        BasicStroke s = (BasicStroke) this.g2.getStroke();
        assertEquals(BasicStroke.CAP_SQUARE, s.getEndCap());
        assertEquals(1.0f, s.getLineWidth(), EPSILON);
        assertEquals(BasicStroke.JOIN_MITER, s.getLineJoin());
    }
    
    /**
     * Check that a null GlyphVector throws a {@code NullPointerException}.
     */
    @Test
    void drawGlyphVectorNull() {
        try {
            g2.drawGlyphVector(null, 10, 10);
            fail("Expecting a NullPointerException.");
        } catch (NullPointerException e) {
            // expected
        }
    }
    
    /**
     * Check the shear() method.
     */
    @Test
    void shear() {
        g2.setTransform(new AffineTransform());
        g2.shear(2.0, 3.0);
        assertEquals(new AffineTransform(1, 3, 2, 1, 0, 0), g2.getTransform());
    }
    
    /**
     * Checks a translate() followed by a shear().
     */
    @Test
    void shearFollowingTranslate() {
        g2.setTransform(new AffineTransform());
        g2.translate(10.0, 20.0);
        g2.shear(2.0, 3.0);
        assertEquals(new AffineTransform(1, 3, 2, 1, 10, 20), g2.getTransform());
    }
    
    @Test
    void drawImageWithNullBackground() {
        Image img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g2.drawImage(img, 10, 10, null, null);
        assertTrue(true); // won't get here if there's an exception above
    }
    
    /**
     * https://github.com/jfree/jfreesvg/issues/6
     */
    @Test
    void drawImageWithNullTransform() {
        Image img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g2.drawImage(img, null, null);
        assertTrue(true); // won't get here if there's an exception above
    }
    
    @Test
    void drawImageWithNullImage() {
        // API docs say method does nothing if img is null
        // still seems to return true
        assertTrue(g2.drawImage(null, 10, 20, null));
        assertTrue(g2.drawImage(null, 10, 20, 30, 40, null));
        assertTrue(g2.drawImage(null, 10, 20, Color.YELLOW, null));
        assertTrue(g2.drawImage(null, 1, 2, 3, 4, Color.RED, null));
        assertTrue(g2.drawImage(null, 1, 2, 3, 4, 5, 6, 7, 8, null));
        assertTrue(g2.drawImage(null, 1, 2, 3, 4, 5, 6, 7, 8, Color.RED, null));
    }
    
    @Test
    void drawImageWithNegativeDimensions() {
        Image img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        assertTrue(g2.drawImage(img, 1, 2, -10, 10, null));
        assertTrue(g2.drawImage(img, 1, 2, 10, -10, null)); 
    }

    /**
     * Check that the color is not changed by setting a clip.  In some
     * implementations the clip is saved/restored as part of the overall
     * graphics state so clipping can impact other attributes.
     */
    @Test
    void checkColorAfterSetClip() {
        this.g2.setColor(Color.RED);
        assertEquals(Color.RED, this.g2.getColor());
        this.g2.setClip(0, 0, 10, 10);
        assertEquals(Color.RED, this.g2.getColor());
        this.g2.setColor(Color.BLUE);
        assertEquals(Color.BLUE, this.g2.getColor());
        this.g2.setClip(0, 0, 20, 20);
        assertEquals(Color.BLUE, this.g2.getColor());
    }

    /**
     * See https://github.com/jfree/fxgraphics2d/issues/6
     */
    @Test
    void checkFontAfterSetClip() {
        this.g2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        assertEquals(new Font(Font.DIALOG, Font.BOLD, 12), this.g2.getFont());
        this.g2.setClip(0, 0, 10, 10);
        assertEquals(new Font(Font.DIALOG, Font.BOLD, 12), this.g2.getFont());
        this.g2.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
        assertEquals(new Font(Font.DIALOG, Font.BOLD, 24), this.g2.getFont());
        this.g2.setClip(0, 0, 20, 20);
        assertEquals(new Font(Font.DIALOG, Font.BOLD, 24), this.g2.getFont());
    }

    /**
     * See https://github.com/jfree/fxgraphics2d/issues/6
     */
    @Test
    void checkStrokeAfterSetClip() {
        this.g2.setStroke(new BasicStroke(1.0f));
        assertEquals(new BasicStroke(1.0f), this.g2.getStroke());
        this.g2.setClip(0, 0, 10, 10);
        assertEquals(new BasicStroke(1.0f), this.g2.getStroke());
        this.g2.setStroke(new BasicStroke(2.0f));
        assertEquals(new BasicStroke(2.0f), this.g2.getStroke());
        this.g2.setClip(0, 0, 20, 20);
        assertEquals(new BasicStroke(2.0f), this.g2.getStroke());
    }

    /**
     * A test to check whether setting a transform on the Graphics2D affects
     * the results of text measurements performed via getFontMetrics().
     */
    @Test
    void testGetFontMetrics() {
        Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        FontMetrics fm = this.g2.getFontMetrics(f);
        int w = fm.stringWidth("ABC");
        Rectangle2D bounds = fm.getStringBounds("ABC", this.g2);
        
        // after scaling, the string width is not changed
        this.g2.setTransform(AffineTransform.getScaleInstance(3.0, 2.0));
        fm = this.g2.getFontMetrics(f);
        assertEquals(w, fm.stringWidth("ABC"));
        assertEquals(bounds.getWidth(), fm.getStringBounds("ABC", this.g2).getWidth(), EPSILON);
    }
    
    @Test
    void drawImageWithNullImageOp() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g2.drawImage(img, null, 2, 3);
        assertTrue(true); // won't get here if there's an exception above        
    }
    
    /**
     * API docs say the method does nothing when called with a null image.
     */
    @Test
    void drawRenderedImageWithNullImage() {
        g2.drawRenderedImage(null, AffineTransform.getTranslateInstance(0, 0));
        assertTrue(true); // won't get here if there's an exception above                
    }

    /**
     * Filling and/or stroking a Rectangle2D with a negative width will not display anything but
     * should not throw an exception.
     */
    @Test
    void fillOrStrokeRectangleWithNegativeWidthMustNotFail() {
        g2.draw(new Rectangle2D.Double(0, 0, 0, 10));
        g2.draw(new Rectangle2D.Double(0, 0, -10, 10));
        g2.fill(new Rectangle2D.Double(0, 0, 0, 10));
        g2.fill(new Rectangle2D.Double(0, 0, -10, 10));
        assertTrue(true); // won't get here if there's an exception above
    }

    /**
     * Filling and/or stroking a Rectangle2D with a negative height will not display anything but
     * should not throw an exception.
     */
    @Test
    void fillOrStrokeRectangleWithNegativeHeightMustNotFail() {
        g2.draw(new Rectangle2D.Double(0, 0, 0, 10));
        g2.draw(new Rectangle2D.Double(0, 0, 10, -10));
        g2.fill(new Rectangle2D.Double(0, 0, 0, 10));
        g2.fill(new Rectangle2D.Double(0, 0, 10, -10));
        assertTrue(true); // won't get here if there's an exception above
    }

    @Test
    void checkClipAfterCreate() {
        this.g2.setClip(10, 20, 30, 40);
        assertEquals(new Rectangle(10, 20, 30, 40), g2.getClip().getBounds2D());

        Graphics2D g2copy = (Graphics2D) this.g2.create();
        g2copy.clipRect(11, 21, 10, 10);
        assertEquals(new Rectangle(11, 21, 10, 10), g2copy.getClip().getBounds2D());
        g2copy.dispose();
        assertEquals(new Rectangle(10, 20, 30, 40), g2.getClip().getBounds2D());
    }

    @Test
    void checkGradientPaintRefGeneration() {
        if (!(this.g2 instanceof SVGGraphics2D)) {
            return;
        }
        SVGGraphics2D svg2 = (SVGGraphics2D) this.g2;
        GradientPaint gp0 = new GradientPaint(1.0f, 2.0f, Color.RED, 3.0f, 4.0f, Color.BLUE);
        svg2.setPaint(gp0);
        assertEquals(svg2.defsKeyPrefix + "gp0", svg2.gradientPaintRef);
        GradientPaint gp1 = new GradientPaint(1.0f, 2.0f, Color.YELLOW, 3.0f, 4.0f, Color.GREEN);
        svg2.setPaint(gp1);
        assertEquals(svg2.defsKeyPrefix + "gp1", svg2.gradientPaintRef);
        svg2.setPaint(gp0);
        assertEquals(svg2.defsKeyPrefix + "gp0", svg2.gradientPaintRef);
    }

    @Test
    void checkLinearGradientPaintRefGeneration() {
        if (!(this.g2 instanceof SVGGraphics2D)) return;
        SVGGraphics2D svg2 = (SVGGraphics2D) this.g2;
        var lgp0 = new LinearGradientPaint(1.0f, 2.0f,3.0f, 4.0f, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { Color.RED, Color.BLUE, Color.GREEN });
        svg2.setPaint(lgp0);
        assertEquals(svg2.defsKeyPrefix + "lgp0", svg2.gradientPaintRef);
        var lgp1 = new LinearGradientPaint(1.0f, 2.0f,3.0f, 4.0f, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { Color.YELLOW, Color.CYAN, Color.GRAY });
        svg2.setPaint(lgp1);
        assertEquals(svg2.defsKeyPrefix + "lgp1", svg2.gradientPaintRef);
        svg2.setPaint(lgp0);
        assertEquals(svg2.defsKeyPrefix + "lgp0", svg2.gradientPaintRef);
    }

    @Test
    void checkRadialGradientPaintRefGeneration() {
        if (!(this.g2 instanceof SVGGraphics2D)) return;
        SVGGraphics2D svg2 = (SVGGraphics2D) this.g2;
        var rgp0 = new RadialGradientPaint(1.0f, 2.0f, 3.0f, new float[] { 0.0f, 0.5f, 1.0f },
                new Color[] { Color.RED, Color.BLUE, Color.GREEN });
        svg2.setPaint(rgp0);
        assertEquals(svg2.defsKeyPrefix + "rgp0", svg2.gradientPaintRef);
        var rgp1 = new RadialGradientPaint(1.0f, 2.0f, 4.0f, new float[] { 0.0f, 0.5f, 1.0f },
                new Color[] { Color.YELLOW, Color.CYAN, Color.GRAY });
        svg2.setPaint(rgp1);
        assertEquals(svg2.defsKeyPrefix + "rgp1", svg2.gradientPaintRef);
        svg2.setPaint(rgp0);
        assertEquals(svg2.defsKeyPrefix + "rgp0", svg2.gradientPaintRef);
    }
}
