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

package org.jfree.graphics2d.canvas;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.jfree.graphics2d.GraphicsUtils;

/**
 * A <code>Graphics2D</code> implementation that writes out Javascript code 
 * that will draw to an HTML5 Canvas. 
 * <p>
 * Implementation notes:
 * <ul>
 * <li>all rendering hints are ignored;</li>
 * <li>images are not yet supported;</li>
 * <li>the <code>drawString()</code> methods that work with an 
 * <code>AttributedCharacterIterator</code> currently ignore the formatting 
 * information.</li>
 * </ul>
 * <p>
 * For some demos of the use of this class, please look in the
 * <code>org.jfree.graphics2d.demo</code> package in the <code>src</code>
 * directory.
 */
public final class CanvasGraphics2D extends Graphics2D {

    /** The canvas ID. */
    private String canvasID;
    
    /** The buffer for all the Javascript output. */
    private StringBuilder sb;
    
    /** Rendering hints (all ignored). */
    private RenderingHints hints;
    
    private Shape clip;;
    
    private Paint paint = Color.BLACK;
    
    private Color color = Color.BLACK;
    
    private Composite composite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 1.0f);
    
    private Stroke stroke = new BasicStroke(1.0f);
    
    private Font font = new Font("SansSerif", Font.PLAIN, 12);
    
    private AffineTransform transform = new AffineTransform();

    /** The background color, presently ignored. */
    private Color background = Color.BLACK;

    /** A hidden image used for font metrics. */
    private BufferedImage image = new BufferedImage(10, 10, 
            BufferedImage.TYPE_INT_RGB);;
    
    /**
     * An instance that is lazily instantiated in drawLine and then 
     * subsequently reused to avoid creating a lot of garbage.
     */
    private Line2D line;
    
    /**
     * An instance that is lazily instantiated in fillRect and then 
     * subsequently reused to avoid creating a lot of garbage.
     */
    Rectangle2D rect;

    /**
     * An instance that is lazily instantiated in draw/fillRoundRect and then
     * subsequently reused to avoid creating a lot of garbage.
     */
    private RoundRectangle2D roundRect;
    
     /**
     * An instance that is lazily instantiated in draw/fillOval and then
     * subsequently reused to avoid creating a lot of garbage.
     */
   private Ellipse2D oval;
    
    /**
     * An instance that is lazily instantiated in draw/fillArc and then
     * subsequently reused to avoid creating a lot of garbage.
     */
    private Arc2D arc;

    /**
     * Creates a new instance.  The canvas ID is stored but not used in the
     * current implementation.
     * 
     * @param canvasID  the canvas ID. 
     */
    public CanvasGraphics2D(String canvasID) {
        this.canvasID = canvasID;
        this.sb = new StringBuilder();
        this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.clip = null;
    }

    /**
     * Returns the canvas ID that was passed to the constructor.
     * 
     * @return The canvas ID. 
     */
    public String getCanvasID() {
        return this.canvasID;
    }
    
    /**
     * Not yet implemented.
     * 
     * @return The graphics configuration.
     */
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }
    
    /**
     * Creates a new graphics object that is a copy of this graphics object.
     * 
     * @return A new graphics object.
     */
    @Override
    public Graphics create() {
        CanvasGraphics2D copy = new CanvasGraphics2D(this.canvasID);
        copy.setRenderingHints(getRenderingHints());
        copy.setClip(getClip());
        copy.setPaint(getPaint());
        copy.setColor(getColor());
        copy.setComposite(getComposite());
        copy.setStroke(getStroke());
        copy.setFont(getFont());
        copy.setTransform(getTransform());
        copy.setBackground(getBackground());
        return copy;
    }

    /**
     * Returns the paint used to draw or fill shapes (or text).  The default 
     * value is {@link Color#BLACK}.
     * 
     * @return The paint (never <code>null</code>). 
     * 
     * @see #setPaint(java.awt.Paint) 
     */
    @Override
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint used to draw or fill shapes (or text).  If 
     * <code>paint</code> is an instance of <code>Color</code>, this method will
     * also update the current color attribute (see {@link #getColor()}). If 
     * you pass <code>null</code> to this method, it does nothing (in 
     * accordance with the JDK specification).
     * 
     * @param paint  the paint (<code>null</code> is permitted but ignored).
     * 
     * @see #getPaint() 
     */
    @Override
    public void setPaint(Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        if (paint instanceof Color) {
            setColor((Color) paint);
        } else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) paint;
            Point2D p1 = gp.getPoint1();
            Point2D p2 = gp.getPoint2();
            this.sb.append("var g = ctx.createLinearGradient(").append(
                    p1.getX()).append(",").append(p1.getY()).append(",")
                    .append(p2.getX()).append(",").append(p2.getY())
                    .append(");");
            this.sb.append("g.addColorStop(0,'").append(
                    toCSSColorValue(gp.getColor1())).append("');");
            this.sb.append("g.addColorStop(1,'").append(
                    toCSSColorValue(gp.getColor2())).append("');");
            this.sb.append("ctx.fillStyle=g;");
        } else {
            System.err.println("setPaint(" + paint + ")");
        }
    }

    /**
     * Returns the foreground color.  This method exists for backwards
     * compatibility in AWT, you should use the {@link #getPaint()} method.
     * 
     * @return The foreground color (never <code>null</code>).
     * 
     * @see #getPaint() 
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the foreground color.  This method exists for backwards 
     * compatibility in AWT, you should use the 
     * {@link #setPaint(java.awt.Paint)} method.
     * 
     * @param c  the color (<code>null</code> permitted but ignored). 
     * 
     * @see #setPaint(java.awt.Paint) 
     */
    @Override
    public void setColor(Color c) {
        if (c == null || this.color.equals(c)) {
            return;  // nothing to do
        }
        this.color = c;
        this.paint = c;
        String cssColor = toCSSColorValue(c);
        // FIXME: we could avoid writing both of these by tracking dirty
        // flags and only writing the appropriate style when required
        this.sb.append("ctx.fillStyle=\"").append(cssColor).append("\";");        
        this.sb.append("ctx.strokeStyle=\"").append(cssColor).append("\";");
    }
    
    /**
     * A utility method that translates a Color object to a CSS color string.
     * 
     * @param c  the color (<code>null</code> not permitted).
     * 
     * @return The CSS string for the color specification.
     */
    private String toCSSColorValue(Color c) {
        return "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue()
                + "," + c.getAlpha() / 255.0f + ")";
    }

    /**
     * Returns the background color.  The default value is {@link Color#BLACK}.
     * This is used by the {@link #clearRect(int, int, int, int)} method.
     * 
     * @return The background color (possibly <code>null</code>). 
     * 
     * @see #setBackground(java.awt.Color) 
     */
    @Override
    public Color getBackground() {
        return this.background;
    }

    /**
     * Sets the background color.  This is used by the 
     * {@link #clearRect(int, int, int, int)} method.  The reference 
     * implementation allows <code>null</code> for the background color so
     * we allow that too (but for that case, the clearRect method will do 
     * nothing).
     * 
     * @param color  the color (<code>null</code> permitted).
     * 
     * @see #getBackground() 
     */
    @Override
    public void setBackground(Color color) {
        this.background = color;
    }

    /**
     * Returns the current composite.
     * 
     * @return The current composite (never <code>null</code>).
     * 
     * @see #setComposite(java.awt.Composite) 
     */
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    /**
     * Sets the composite (only <code>AlphaComposite</code> is handled).
     * 
     * @param comp  the composite (<code>null</code> not permitted).
     * 
     * @see #getComposite() 
     */
    @Override
    public void setComposite(Composite comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Null 'comp' argument.");
        }
        this.composite = comp;
        if (comp instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite) comp;
            sb.append("ctx.globalAlpha=").append(ac.getAlpha()).append(";");
            sb.append("ctx.globalCompositeOperation=\"").append(
                    toJSCompositeRuleName(ac.getRule())).append("\";");
        } else {
            System.err.println("setComposite(" + comp + ")");        
        }
    }

    private String toJSCompositeRuleName(int rule) {
        switch (rule) {
            case AlphaComposite.CLEAR:
                return "xor";
            case AlphaComposite.SRC_IN:
                return "source-in";
            case AlphaComposite.SRC_OUT:
                return "source-out";
            case AlphaComposite.SRC_OVER:
                return "source-over";
            case AlphaComposite.SRC_ATOP:
                return "source-atop";
            case AlphaComposite.DST_IN:
                return "destination-in";
            case AlphaComposite.DST_OUT:
                return "destination-out";
            case AlphaComposite.DST_OVER:
                return "destination-over";
            case AlphaComposite.DST_ATOP:
                return "destination-atop";
            default:
                throw new IllegalArgumentException("Unknown/unhandled 'rule' " 
                        + rule);
        }
    }

    /**
     * Returns the current stroke (used when drawing shapes). 
     * 
     * @return The current stroke (never <code>null</code>). 
     * 
     * @see #setStroke(java.awt.Stroke) 
     */
    @Override
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Sets the stroke that will be used to draw shapes.  Only 
     * <code>BasicStroke</code> is supported.
     * 
     * @param s  the stroke (<code>null</code> not permitted).
     * 
     * @see #getStroke() 
     */
    @Override
    public void setStroke(Stroke s) {
        if (s == null) {
            throw new IllegalArgumentException("Null 's' argument.");
        }
        this.stroke = s;
        if (s instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) s;
            sb.append("ctx.lineWidth=").append(bs.getLineWidth()).append(";");
            if (bs.getDashArray() != null) {
                sb.append("ctx.setLineDash([");
                for (int i = 0; i < bs.getDashArray().length; i++) {
                    if (i != 0 ) {
                        sb.append(",");
                    } 
                    sb.append((int) bs.getDashArray()[i]);
                }
                sb.append("]);");
            } else {
               sb.append("ctx.setLineDash([]);");
            }
        } else {
            System.err.println("setStroke(" + s + ");");
        }
    }

    /**
     * Returns the current value for the specified hint.  Note that all hints
     * are currently ignored in this implementation.
     * 
     * @param hintKey  the hint key (<code>null</code> permitted, but the
     *     result will be <code>null</code> also).
     * 
     * @return The current value for the specified hint 
     *     (possibly <code>null</code>).
     * 
     * @see #setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object) 
     */
    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.hints.get(hintKey);
    }

    /**
     * Sets the value for a hint.  Note that all hints are currently
     * ignored in this implementation.
     * 
     * @param hintKey  the hint key (<code>null</code> not permitted).
     * @param hintValue  the hint value.
     * 
     * @see #getRenderingHint(java.awt.RenderingHints.Key) 
     */
    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.hints.put(hintKey, hintValue);
    }

    /**
     * Returns a copy of the rendering hints.  Modifying the returned copy
     * will have no impact on the state of this Graphics2D instance.
     * 
     * @return The rendering hints (never <code>null</code>). 
     * 
     * @see #setRenderingHints(java.util.Map) 
     */
    @Override
    public RenderingHints getRenderingHints() {
        return (RenderingHints) this.hints.clone();
    }

    /**
     * Sets the rendering hints to the specified collection.
     * 
     * @param hints  the new set of hints (<code>null</code> not permitted).
     * 
     * @see #getRenderingHints() 
     */
    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        this.hints.clear();
        this.hints.putAll(hints);
    }

    /**
     * Adds all the supplied rendering hints.
     * 
     * @param hints  the hints (<code>null</code> not permitted).
     */
    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        this.hints.putAll(hints);
    }

    /**
     * Draws the specified shape with the current <code>paint</code> and 
     * <code>stroke</code>.  There is direct handling for <code>Line2D</code>, 
     * <code>Rectangle2D</code> and <code>Path2D</code>. All other shapes are
     * mapped to a <code>GeneralPath</code> and then drawn (effectively as 
     * </code>Path2D</code> objects).
     * 
     * @param s  the shape (<code>null</code> not permitted).
     * 
     * @see #fill(java.awt.Shape) 
     */
    @Override
    public void draw(Shape s) {
        if (s instanceof Line2D || s instanceof Rectangle2D 
                || s instanceof Path2D) {
            shapeToPath(s);
            sb.append("ctx.stroke();");
        } else {
            draw(new GeneralPath(s));          
        }
    }

    /**
     * Fills the specified shape with the current <code>paint</code>.  There is
     * direct handling for <code>Rectangle2D</code> and <code>Path2D</code>.  
     * All other shapes are mapped to a <code>GeneralPath</code> and then 
     * filled.
     * 
     * @param s  the shape (<code>null</code> not permitted). 
     * 
     * @see #draw(java.awt.Shape) 
     */
    @Override
    public void fill(Shape s) {
        if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            if (r.isEmpty()) {
                return;
            }
            sb.append("ctx.fillRect(").append(r.getX()).append(",")
                    .append(r.getY()).append(",").append(r.getWidth())
                    .append(",").append(r.getHeight()).append(");");
        } else if (s instanceof Path2D) {
            shapeToPath(s);
            sb.append("ctx.fill();");
        } else {
            fill(new GeneralPath(s));
        }
    }

    private void shapeToPath(Shape s) {
        if (s instanceof Line2D) {
            Line2D l = (Line2D) s;
            sb.append("ctx.beginPath();");
            sb.append("ctx.moveTo(").append(l.getX1()).append(",")
                    .append(l.getY1()).append(");");
            sb.append("ctx.lineTo(").append(l.getX2()).append(",")
                    .append(l.getY2()).append(");");
            sb.append("ctx.closePath();");
        } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            sb.append("ctx.beginPath();");
            sb.append("ctx.rect(").append(r.getX()).append(",")
                    .append(r.getY()).append(",").append(r.getWidth())
                    .append(",").append(r.getHeight()).append(");");
            sb.append("ctx.closePath();");
        } 
        else if (s instanceof Path2D) {
            Path2D p = (Path2D) s;
            float[] coords = new float[6];
            double[] closePt = null;
            PathIterator iterator = p.getPathIterator(getTransform());
            sb.append("ctx.beginPath();");
            while (!iterator.isDone()) {
                int type = iterator.currentSegment(coords);
                switch (type) {
                case (PathIterator.SEG_MOVETO):
                    closePt = new double[2];
                    closePt[0] = coords[0];
                    closePt[1] = coords[1];
                    sb.append("ctx.moveTo(").append(coords[0]).append(",")
                            .append(coords[1]).append(");");
                    break;
                case (PathIterator.SEG_LINETO):
                    sb.append("ctx.lineTo(").append(coords[0]).append(",")
                            .append(coords[1]).append(");");
                    break;
                case (PathIterator.SEG_QUADTO):
                    sb.append("ctx.quadraticCurveTo(").append(coords[0])
                            .append(",").append(coords[1]).append(coords[2])
                            .append(",").append(coords[3]).append(");");
                    break;
                case (PathIterator.SEG_CUBICTO):
                    sb.append("ctx.bezierCurveTo(")
                            .append(coords[0]).append(",")
                            .append(coords[1]).append(",")
                            .append(coords[2]).append(",")
                            .append(coords[3]).append(",")
                            .append(coords[4]).append(",")
                            .append(coords[5]).append(");");
                    break;
                case (PathIterator.SEG_CLOSE):
                    if (closePt != null) {
                        sb.append("ctx.lineTo(").append(closePt[0])
                                .append(",").append(closePt[1]).append(");");
                    }
                    break;
                default:
                    break;
                }
                iterator.next();
            }
            //sb.append("ctx.closePath();");
        } else {
            throw new RuntimeException("Unhandled shape " + s);
        }
    }
    
    /**
     * Returns the current font used for drawing text.
     * 
     * @return The current font (never <code>null</code>).
     * 
     * @see #setFont(java.awt.Font) 
     */
    @Override
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font to be used for drawing text.
     * 
     * @param font  the font (<code>null</code> is permitted but ignored).
     * 
     * @see #getFont() 
     */
    @Override
    public void setFont(Font font) {
        if (font == null || this.font.equals(font)) {
            return;
        }
        this.font = font;
        this.sb.append("ctx.font=\"").append(font.getSize()).append("px ")
                .append(font.getFontName()).append("\";");
    }

    /**
     * Returns the font metrics for the specified font.
     * 
     * @param f  the font.
     * 
     * @return The font metrics. 
     */
    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.image.createGraphics().getFontMetrics(f);
    }
 
    /**
     * Returns the font render context.  The implementation here returns the
     * <code>FontRenderContext</code> for an image that is maintained 
     * internally (as for {@link #getFontMetrics}).
     * 
     * @return The font render context.
     */
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.image.createGraphics().getFontRenderContext();
    }

    /**
     * Draws a string at <code>(x, y)</code>.  The start of the text at the
     * baseline level will be aligned with the <code>(x, y)</code> point.
     * 
     * @param str  the string (<code>null</code> not permitted).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * 
     * @see #drawString(java.lang.String, float, float) 
     */
    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    /**
     * Draws a string at <code>(x, y)</code>. The start of the text at the
     * baseline level will be aligned with the <code>(x, y)</code> point.
     * 
     * @param str  the string (<code>null</code> not permitted).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    @Override
    public void drawString(String str, float x, float y) {
        if (str == null) {
            throw new NullPointerException("Null 'str' argument.");
        }
        sb.append("ctx.save();");
        if (this.paint instanceof Color) {
            this.sb.append("ctx.fillStyle=\"").append(toCSSColorValue(
                    (Color) this.paint)).append("\";");
        } else {
            setPaint(this.paint);
        }
        sb.append("ctx.fillText(\"").append(str).append("\",").append(x)
                .append(",").append(y).append(");");
        sb.append("ctx.restore();");
    }

    /**
     * Draws a string of attributed characters at <code>(x, y)</code>.  The 
     * call is delegated to 
     * {@link #drawString(java.text.AttributedCharacterIterator, float, float)}. 
     * 
     * @param iterator  an iterator for the characters.
     * @param x  the x-coordinate.
     * @param y  the x-coordinate.
     */
    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y); 
    }

    /**
     * Draws a string of attributed characters at <code>(x, y)</code>. 
     * <p>
     * <b>LIMITATION</b>: in the current implementation, the string is drawn 
     * using the current font and the formatting is ignored.
     * 
     * @param iterator  an iterator over the characters (<code>null</code> not 
     *     permitted).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, 
            float y) {
        StringBuilder builder = new StringBuilder();
        int count = iterator.getEndIndex() - iterator.getBeginIndex();
        char c = iterator.first();
        for (int i = 0; i < count; i++) {
            builder.append(c);
            c = iterator.next();
        }
        drawString(builder.toString(), x, y);
    }

    /**
     * Draws the specified glyph vector at the location (x, y).
     * 
     * @param g  the glyph vector.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        draw(g.getOutline(x, y));
    }

    /**
     * Translates the origin to <code>(tx, ty)</code>.  This call is delegated 
     * to {@link #translate(double, double)}.
     * 
     * @param tx  the x-translation.
     * @param ty  the y-translation.
     */
    @Override
    public void translate(int tx, int ty) {
        translate((double) tx, (double) ty);
    }

    /**
     * Applies the translation (tx, ty).
     * 
     * @param tx  the x-translation.
     * @param ty  the y-translation.
     */
    @Override
    public void translate(double tx, double ty) {
        AffineTransform t = getTransform();
        t.translate(tx, ty);
        setTransform(t);
    }

    /**
     * Applies a rotation (anti-clockwise) about <code>(0, 0)</code>.
     * 
     * @param theta  the rotation angle (in radians). 
     */
    @Override
    public void rotate(double theta) {
        AffineTransform t = AffineTransform.getRotateInstance(theta);
        transform(t);
    }

    /**
     * Applies a rotation (anti-clockwise) about <code>(x, y)</code>.
     * 
     * @param theta  the rotation angle (in radians).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    @Override
    public void rotate(double theta, double x, double y) {
        translate(x, y);
        rotate(theta);
        translate(-x, -y);
    }

    /**
     * Applies a scale transformation.
     * 
     * @param sx  the x-scaling factor.
     * @param sy  the y-scaling factor.
     */
    @Override
    public void scale(double sx, double sy) {
        AffineTransform t = getTransform();
        t.scale(sx, sy);
        setTransform(t);
    }

    /**
     * Applies a shear transformation. This is equivalent to the following 
     * call to the <code>transform</code> method:
     * <p>
     * <ul><li>
     * <code>transform(AffineTransform.getShearInstance(shx, shy));</code>
     * </ul>
     * 
     * @param shx  the x-shear factor.
     * @param shy  the y-shear factor.
     */
    @Override
    public void shear(double shx, double shy) {
        transform(AffineTransform.getShearInstance(shx, shy));
    }

    /**
     * Applies this transform to the existing transform by concatenating it.
     * 
     * @param t  the transform (<code>null</code> not permitted). 
     */
    @Override
    public void transform(AffineTransform t) {
        this.sb.append("ctx.transform(");
        this.sb.append(t.getScaleX()).append(","); // m00
        this.sb.append(t.getShearY()).append(","); // m10
        this.sb.append(t.getShearX()).append(","); // m01
        this.sb.append(t.getScaleY()).append(",");  // m11
        this.sb.append(t.getTranslateX()).append(","); // m02
        this.sb.append(t.getTranslateY()); // m12
        this.sb.append(");");
        AffineTransform tx = getTransform();
        tx.concatenate(t);
        setTransform(tx);
    }

    /**
     * Returns a copy of the current transform.
     * 
     * @return A copy of the current transform (never <code>null</code>).
     * 
     * @see #setTransform(java.awt.geom.AffineTransform) 
     */
    @Override
    public AffineTransform getTransform() {
        return (AffineTransform) this.transform.clone();
    }

    /**
     * Sets the transform.
     * 
     * @param t  the new transform (<code>null</code> permitted, resets to the
     *     identity transform).
     */
    @Override
    public void setTransform(AffineTransform t) {
        if (t == null) {
            this.transform = new AffineTransform();
        } else {
            this.transform = new AffineTransform(t);
        }
        this.sb.append("ctx.setTransform(");
        this.sb.append(transform.getScaleX()).append(","); // m00
        this.sb.append(transform.getShearY()).append(","); // m10
        this.sb.append(transform.getShearX()).append(","); // m01
        this.sb.append(transform.getScaleY()).append(",");  // m11
        this.sb.append(transform.getTranslateX()).append(","); // m02
        this.sb.append(transform.getTranslateY()); // m12
        this.sb.append(");");
    }

    /**
     * Returns <code>true</code> if the rectangle (in device space) intersects
     * with the shape (the interior, if <code>onStroke</code> is false, 
     * otherwise the stroked outline of the shape).
     * 
     * @param rect  a rectangle (in device space).
     * @param s the shape.
     * @param onStroke  test the stroked outline only?
     * 
     * @return A boolean. 
     */
    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        Shape ts;
        if (onStroke) {
            ts = this.transform.createTransformedShape(
                    this.stroke.createStrokedShape(s));
        } else {
            ts = this.transform.createTransformedShape(s);
        }
        if (!rect.getBounds2D().intersects(ts.getBounds2D())) {
            return false;
        }
        Area a1 = new Area(rect);
        Area a2 = new Area(ts);
        a1.intersect(a2);
        return !a1.isEmpty();
    }

    /**
     * Not yet implemented.
     */
    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet implemented.
     * 
     * @param c1 
     */
    @Override
    public void setXORMode(Color c1) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Returns the clip bounds.
     * 
     * @return The clip bounds (possibly <code>null</code>). 
     */
    @Override
    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return getClip().getBounds();
    }

    /**
     * Returns the user clipping region.  The initial default value is 
     * <code>null</code>.
     * 
     * @return The user clipping region (possibly <code>null</code>).
     * 
     * @see #setClip(java.awt.Shape) 
     */
    @Override
    public Shape getClip() {
        if (this.clip == null) {
            return null;
        }
        AffineTransform inv;
        try {
            inv = this.transform.createInverse();
            return inv.createTransformedShape(this.clip);
        } catch (NoninvertibleTransformException ex) {
            return null;
        }
    }

    /**
     * Sets the user clipping region.
     * 
     * @param shape  the new user clipping region (<code>null</code> permitted).
     * 
     * @see #getClip()
     */
    @Override
    public void setClip(Shape shape) {
        // null is handled fine here...
        this.clip = this.transform.createTransformedShape(shape);
    }

    /**
     * Clips to the intersection of the current clipping region and the
     * specified shape. 
     * 
     * According to the Oracle API specification, this method will accept a 
     * <code>null</code> argument, but there is an open bug report (since 2004) 
     * that suggests this is wrong:
     * 
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6206189
     * 
     * @param s  the clip shape (<code>null</code> not permitted). 
     */
    @Override
    public void clip(Shape s) {
        if (this.clip == null) {
            setClip(s);
            return;
        }
        Shape ts = this.transform.createTransformedShape(s);
        if (!ts.intersects(this.clip.getBounds2D())) {
            setClip(new Rectangle2D.Double());
        } else {
          Area a1 = new Area(ts);
          Area a2 = new Area(this.clip);
          a1.intersect(a2);
          this.clip = new Path2D.Double(a1);
        }
    }

    /**
     * Clips to the intersection of the current clipping region and the 
     * specified rectangle.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    @Override
    public void clipRect(int x, int y, int width, int height) {
        setRect(x, y, width, height);
        clip(this.rect);
    }

    /**
     * Sets the user clipping region to the specified rectangle.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * 
     * @see #getClip() 
     */
    @Override
    public void setClip(int x, int y, int width, int height) {
        setRect(x, y, width, height);
        setClip(this.rect);
    }

    /**
     * Not yet implemented.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param dx
     * @param dy 
     */
    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Draws a line from <code>(x1, y1)</code> to <code>(x2, y2)</code> using 
     * the current <code>paint</code> and <code>stroke</code>.
     * 
     * @param x1  the x-coordinate of the start point.
     * @param y1  the y-coordinate of the start point.
     * @param x2  the x-coordinate of the end point.
     * @param y2  the x-coordinate of the end point.
     */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (this.line == null) {
            this.line = new Line2D.Double(x1, y1, x2, y2);
        } else {
            this.line.setLine(x1, y1, x2, y2);
        }
        draw(this.line);
    }

    /**
     * Fills the specified rectangle with the current <code>paint</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the rectangle width.
     * @param height  the rectangle height.
     */
    @Override
    public void fillRect(int x, int y, int width, int height) {
        setRect(x, y, width, height);
        fill(this.rect);
    }

    /**
     * Clears the specified rectangle by filling it with the current 
     * background color.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * 
     * @see #getBackground() 
     */
    @Override
    public void clearRect(int x, int y, int width, int height) {
        if (getBackground() == null) {
            return;  // we can't do anything
        }
        Paint saved = getPaint();
        setPaint(getBackground());
        fillRect(x, y, width, height);
        setPaint(saved);
    }
    
    /**
     * Draws a rectangle with rounded corners using the current 
     * <code>paint</code> and <code>stroke</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param arcWidth  the arc-width.
     * @param arcHeight  the arc-height.
     * 
     * @see #fillRoundRect(int, int, int, int, int, int) 
     */
    @Override
    public void drawRoundRect(int x, int y, int width, int height, 
            int arcWidth, int arcHeight) {
        setRoundRect(x, y, width, height, arcWidth, arcHeight);
        draw(this.roundRect);
    }

    /**
     * Fills a rectangle with rounded corners using the current 
     * <code>paint</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param arcWidth  the arc-width.
     * @param arcHeight  the arc-height.
     * 
     * @see #drawRoundRect(int, int, int, int, int, int) 
     */
    @Override
    public void fillRoundRect(int x, int y, int width, int height, 
            int arcWidth, int arcHeight) {
        setRoundRect(x, y, width, height, arcWidth, arcHeight);
        fill(this.roundRect);
    }
    
    /**
     * Draws an oval framed by the rectangle <code>(x, y, width, height)</code>
     * using the current <code>paint</code> and <code>stroke</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * 
     * @see #fillOval(int, int, int, int) 
     */
    @Override
    public void drawOval(int x, int y, int width, int height) {
        setOval(x, y, width, height);
        draw(this.oval);
    }

    /**
     * Fills an oval framed by the rectangle <code>(x, y, width, height)</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * 
     * @see #drawOval(int, int, int, int) 
     */
    @Override
    public void fillOval(int x, int y, int width, int height) {
        setOval(x, y, width, height);
        fill(this.oval);
    }

    /**
     * Draws an arc contained within the rectangle 
     * <code>(x, y, width, height)</code>, starting at <code>startAngle</code>
     * and continuing through <code>arcAngle</code> degrees using 
     * the current <code>paint</code> and <code>stroke</code>.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param startAngle  the start angle in degrees, 0 = 3 o'clock.
     * @param arcAngle  the angle (anticlockwise) in degrees.
     * 
     * @see #fillArc(int, int, int, int, int, int) 
     */
    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, 
            int arcAngle) {
        setArc(x, y, width, height, startAngle, arcAngle);
        draw(this.arc);
    }

    /**
     * Fills an arc contained within the rectangle 
     * <code>(x, y, width, height)</code>, starting at <code>startAngle</code>
     * and continuing through <code>arcAngle</code> degrees, using 
     * the current <code>paint</code>
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param startAngle  the start angle in degrees, 0 = 3 o'clock.
     * @param arcAngle  the angle (anticlockwise) in degrees.
     * 
     * @see #drawArc(int, int, int, int, int, int) 
     */
    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, 
            int arcAngle) {
        setArc(x, y, width, height, startAngle, arcAngle);
        fill(this.arc);
    }
    
    /**
     * Draws the specified multi-segment line using the current 
     * <code>paint</code> and <code>stroke</code>.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polyline.
     */
    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, 
                false);
        draw(p);
    }

    /**
     * Draws the specified polygon using the current <code>paint</code> and 
     * <code>stroke</code>.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polygon.
     * 
     * @see #fillPolygon(int[], int[], int)      */
    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, 
                true);
        draw(p);
    }

    /**
     * Fills the specified polygon using the current <code>paint</code>.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polygon.
     * 
     * @see #drawPolygon(int[], int[], int) 
     */
    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, 
                true);
        fill(p);
    }

    /**
     * Draws an image with the specified transform. Note that the 
     * <code>observer</code> is ignored.     
     * 
     * @param img  the image.
     * @param xform  the transform.
     * @param obs  the image observer (ignored).
     * 
     * @return {@code true} if the image is drawn. 
     */
    @Override
    public boolean drawImage(Image img, AffineTransform xform, 
            ImageObserver obs) {
        AffineTransform savedTransform = getTransform();
        transform(xform);
        boolean result = drawImage(img, 0, 0, obs);
        setTransform(savedTransform);
        return result;
    }

    /**
     * Draws the image resulting from applying the <code>BufferedImageOp</code>
     * to the specified image at the location <code>(x, y)</code>.
     * 
     * @param img  the image.
     * @param op  the operation.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage imageToDraw = op.filter(img, null);
        drawImage(imageToDraw, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
    }

    /**
     * Draws the rendered image.
     * 
     * @param img  the image.
     * @param xform  the transform.
     */
    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        BufferedImage bi = GraphicsUtils.convertRenderedImage(img);
        drawImage(bi, xform, null);
    }

    /**
     * Draws the renderable image.
     * 
     * @param img  the renderable image.
     * @param xform  the transform.
     */
    @Override
    public void drawRenderableImage(RenderableImage img, 
            AffineTransform xform) {
        RenderedImage ri = img.createDefaultRendering();
        drawRenderedImage(ri, xform);
    }

    /**
     * Draws an image at the location <code>(x, y)</code>.  Note that the 
     * <code>observer</code> is ignored.
     * 
     * @param img  the image.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param observer  ignored.
     * 
     * @return {@code true} if the image is drawn. 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        int w = img.getWidth(null);
        if (w < 0) {
            return false;
        }
        int h = img.getHeight(null);
        if (h < 0) {
            return false;
        }
        return drawImage(img, x, y, w, h, observer);
    }

    /**
     * Not yet supported (but no exception is thrown).
     * 
     * @param img
     * @param x
     * @param y
     * @param width
     * @param height
     * @param observer
     * 
     * @return A boolean. 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, 
            ImageObserver observer) {
        // TODO : implement this
        return false;
    }

    /**
     * Draws an image at the location <code>(x, y)</code>.  Note that the 
     * <code>observer</code> is ignored.
     * 
     * @param img  the image.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param bgcolor  the background color (<code>null</code> permitted).
     * @param observer  ignored.
     * 
     * @return {@code true} if the image is drawn. 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, 
            ImageObserver observer) {
        int w = img.getWidth(null);
        if (w < 0) {
            return false;
        }
        int h = img.getHeight(null);
        if (h < 0) {
            return false;
        }
        return drawImage(img, x, y, w, h, bgcolor, observer);
    }

    /**
     * Draws an image to the rectangle <code>(x, y, w, h)</code> (scaling it if
     * required), first filling the background with the specified color.  Note 
     * that the <code>observer</code> is ignored.
     * 
     * @param img  the image.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param w  the width.
     * @param h  the height.
     * @param bgcolor  the background color (<code>null</code> permitted).
     * @param observer  ignored.
     * 
     * @return {@code true} if the image is drawn.      
     */
    @Override
    public boolean drawImage(Image img, int x, int y, int w, int h, 
            Color bgcolor, ImageObserver observer) {
        Paint saved = getPaint();
        setPaint(bgcolor);
        fillRect(x, y, w, h);
        setPaint(saved);
        return drawImage(img, x, y, w, h, observer);
    }

    /**
     * Draws part of an image (defined by the source rectangle 
     * <code>(sx1, sy1, sx2, sy2)</code>) into the destination rectangle
     * <code>(dx1, dy1, dx2, dy2)</code>.  Note that the <code>observer</code> 
     * is ignored.
     * 
     * @param img  the image.
     * @param dx1  the x-coordinate for the top left of the destination.
     * @param dy1  the y-coordinate for the top left of the destination.
     * @param dx2  the x-coordinate for the bottom right of the destination.
     * @param dy2  the y-coordinate for the bottom right of the destination.
     * @param sx1 the x-coordinate for the top left of the source.
     * @param sy1 the y-coordinate for the top left of the source.
     * @param sx2 the x-coordinate for the bottom right of the source.
     * @param sy2 the y-coordinate for the bottom right of the source.
     * 
     * @return {@code true} if the image is drawn. 
     */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, 
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        int w = dx2 - dx1;
        int h = dy2 - dy1;
        BufferedImage img2 = new BufferedImage(BufferedImage.TYPE_INT_ARGB, 
                w, h);
        Graphics2D g2 = img2.createGraphics();
        g2.drawImage(img, 0, 0, w, h, sx1, sy1, sx2, sy2, null);
        return drawImage(img2, dx1, dx2, null);
    }

    /**
     * Draws part of an image (defined by the source rectangle 
     * <code>(sx1, sy1, sx2, sy2)</code>) into the destination rectangle
     * <code>(dx1, dy1, dx2, dy2)</code>.  The destination rectangle is first
     * cleared by filling it with the specified <code>bgcolor</code>. Note that
     * the <code>observer</code> is ignored. 
     * 
     * @param img  the image.
     * @param dx1  the x-coordinate for the top left of the destination.
     * @param dy1  the y-coordinate for the top left of the destination.
     * @param dx2  the x-coordinate for the bottom right of the destination.
     * @param dy2  the y-coordinate for the bottom right of the destination.
     * @param sx1 the x-coordinate for the top left of the source.
     * @param sy1 the y-coordinate for the top left of the source.
     * @param sx2 the x-coordinate for the bottom right of the source.
     * @param sy2 the y-coordinate for the bottom right of the source.
     * @param bgcolor  the background color (<code>null</code> permitted).
     * @param observer  ignored.
     * 
     * @return {@code true} if the image is drawn. 
     */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, 
            int sx1, int sy1, int sx2, int sy2, Color bgcolor, 
            ImageObserver observer) {
        Paint saved = getPaint();
        setPaint(bgcolor);
        fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
        setPaint(saved);
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    /**
     * Does nothing.
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * Returns the script that has been generated by calls to this 
     * <code>Graphics2D</code> implementation.
     * 
     * @return The script.
     */
    public String getScript() {
        return this.sb.toString();
    }

    /**
     * Sets the attributes of the reusable {@link Rectangle2D} object that is
     * used by the {@link CanvasGraphics2D#drawRect(int, int, int, int)} and 
     * {@link CanvasGraphics2D#fillRect(int, int, int, int)} methods.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    private void setRect(int x, int y, int width, int height) {
        if (this.rect == null) {
            this.rect = new Rectangle2D.Double(x, y, width, height);
        } else {
            this.rect.setRect(x, y, width, height);
        }
    }

    /**
     * Sets the attributes of the reusable {@link RoundRectangle2D} object that
     * is used by the {@link #drawRoundRect(int, int, int, int, int, int)} and
     * {@link #fillRoundRect(int, int, int, int, int, int)} methods.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param arcWidth  the arc width.
     * @param arcHeight  the arc height.
     */
    private void setRoundRect(int x, int y, int width, int height, int arcWidth, 
            int arcHeight) {
        if (this.roundRect == null) {
            this.roundRect = new RoundRectangle2D.Double(x, y, width, height, 
                    arcWidth, arcHeight);
        } else {
            this.roundRect.setRoundRect(x, y, width, height, 
                    arcWidth, arcHeight);
        }        
    }

    /**
     * Sets the attributes of the reusable {@link Arc2D} object that is used by
     * {@link #drawArc(int, int, int, int, int, int)} and 
     * {@link #fillArc(int, int, int, int, int, int)} methods.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param startAngle  the start angle in degrees, 0 = 3 o'clock.
     * @param arcAngle  the angle (anticlockwise) in degrees.
     */
    private void setArc(int x, int y, int width, int height, int startAngle, 
            int arcAngle) {
        if (this.arc == null) {
            this.arc = new Arc2D.Double(x, y, width, height, startAngle, 
                    arcAngle, Arc2D.OPEN);
        } else {
            this.arc.setArc(x, y, width, height, startAngle, arcAngle, 
                    Arc2D.OPEN);
        }        
    }
            
    /**
     * Sets the attributes of the reusable {@link Ellipse2D} object that is 
     * used by the {@link #drawOval(int, int, int, int)} and
     * {@link #fillOval(int, int, int, int)} methods.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    private void setOval(int x, int y, int width, int height) {
        if (this.oval == null) {
            this.oval = new Ellipse2D.Double(x, y, width, height);
        } else {
            this.oval.setFrame(x, y, width, height);
        }
    }
}
