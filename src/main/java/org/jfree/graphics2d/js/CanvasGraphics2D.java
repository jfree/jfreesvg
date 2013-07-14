/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.js;

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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
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

/**
 * A Graphics2D implementation that writes out Javascript code that will
 * draw to an HTML5 Canvas.
 */
public class CanvasGraphics2D extends Graphics2D {

    /** The canvas ID. */
    private String canvasID;
    
    /** The buffer for all the Javascript output. */
    private StringBuilder sb;
    
    /** Rendering hints (all ignored). */
    private RenderingHints hints;
    
    private Shape clip = new Rectangle2D.Double();
    
    private Paint paint = Color.BLACK;
    
    private Color color = Color.BLACK;
    
    private Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    
    private Stroke stroke = new BasicStroke(1.0f);
    
    private Font font = new Font("SansSerif", Font.PLAIN, 12);
    
    private AffineTransform transform = new AffineTransform();

    /** The background color, presently ignored. */
    private Color background = Color.BLACK;

    /** A hidden image used for font metrics. */
    private BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);;
    
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
     * Creates a new instance.
     * 
     * @param canvasID  the canvas ID. 
     */
    public CanvasGraphics2D(String canvasID) {
        this.canvasID = canvasID;
        this.sb = new StringBuilder();
        this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

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

    @Override
    public Paint getPaint() {
        return this.paint;
    }

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
        if (paint instanceof Color) {
            setColor((Color) paint);
        } else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) paint;
            Point2D p1 = gp.getPoint1();
            Point2D p2 = gp.getPoint2();
            this.sb.append("var g = ctx.createLinearGradient(").append(p1.getX()).append(",").append(p1.getY()).append(",").append(p2.getX()).append(",").append(p2.getY()).append(");");
            this.sb.append("g.addColorStop(0,'").append(toCSSColorValue(gp.getColor1())).append("');");
            this.sb.append("g.addColorStop(1,'").append(toCSSColorValue(gp.getColor2())).append("');");
            this.sb.append("ctx.fillStyle=g;");
        } else {
            System.err.println("setPaint(" + paint + ")");
        }
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color c) {
        if (this.color.equals(c)) {
            return;  // nothing to do
        }
        this.color = c;
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
     * Returns the background color.  The default value is Color.BLACK.
     * This is used by the {@link #clearRect(int, int, int, int)} method.
     * 
     * @return The background color. 
     * 
     * @see #setBackground(java.awt.Color) 
     */
    @Override
    public Color getBackground() {
        return this.background;
    }

    /**
     * Sets the background color (for now, this is ignored).
     * 
     * @param color  the color (<code>null</code> not permitted).
     */
    @Override
    public void setBackground(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Null 'color' argument.");
        }
        this.background = color;
    }

    /**
     * Returns the current composite.
     * 
     * @return The current composite.
     */
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    /**
     * Sets the composite (only AlphaComposite is handled).
     * 
     * @param comp  the composite.
     */
    @Override
    public void setComposite(Composite comp) {
        this.composite = comp;
        if (comp instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite) comp;
            sb.append("ctx.globalAlpha=").append(ac.getAlpha()).append(";");
            sb.append("ctx.globalCompositeOperation=\"").append(toJSCompositeRuleName(ac.getRule())).append("\";");
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
                throw new IllegalArgumentException("Unknown/unhandled 'rule' " + rule);
        }
    }

    @Override
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Sets the stroke (only BasicStroke is handled at present).
     * 
     * @param s  the stroke.
     */
    @Override
    public void setStroke(Stroke s) {
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
     * @param hintKey  the hint key.
     * 
     * @return The current value for the specified hint.
     */
    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.hints.get(hintKey);
    }

    /**
     * Sets the value for a hint.  Note that all hints are currently
     * ignored in this implementation.
     * 
     * @param hintKey  the hint key.
     * @param hintValue  the hint value.
     */
    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.hints.put(hintKey, hintValue);
    }

    /**
     * Returns the rendering hints.
     * 
     * @return The rendering hints. 
     */
    @Override
    public RenderingHints getRenderingHints() {
        return this.hints;  // FIXME: should we return a copy?
    }

    /**
     * Sets the rendering hints.
     * 
     * @param hints  the new set of hints.
     */
    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        this.hints.clear();
        this.hints.putAll(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        this.hints.putAll(hints);
    }

    /**
     * Draws the specified shape.  There is direct handling for Line2D,
     * Rectangle2D and Path2D.  All other shapes are drawn as a GeneralPath.
     * 
     * @param s  the shape (<code>null</code> not permitted). 
     */
    @Override
    public void draw(Shape s) {
        if (s instanceof Line2D || s instanceof Rectangle2D || s instanceof Path2D) {
            shapeToPath(s);
            sb.append("ctx.stroke();");
        } else {
            System.err.println("*draw(" + s + ")");
            draw(new GeneralPath(s));          
        }
    }

    @Override
    public void fill(Shape s) {
        if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            sb.append("ctx.fillRect(").append(r.getX()).append(",").append(r.getY()).append(",").append(r.getWidth()).append(",").append(r.getHeight()).append(");");
        } else if (s instanceof GeneralPath) {
            shapeToPath(s);
            sb.append("ctx.fill();");
        } else {
            System.err.println("*fill(" + s + ")");
            fill(new GeneralPath(s));
        }
    }

    private void shapeToPath(Shape s) {
        if (s instanceof Line2D) {
            Line2D line = (Line2D) s;
            sb.append("ctx.beginPath();");
            sb.append("ctx.moveTo(").append(line.getX1()).append(",").append(line.getY1()).append(");");
            sb.append("ctx.lineTo(").append(line.getX2()).append(",").append(line.getY2()).append(");");
            sb.append("ctx.closePath();");
        } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            sb.append("ctx.beginPath();");
            sb.append("ctx.rect(").append(r.getX()).append(",").append(r.getY()).append(",").append(r.getWidth()).append(",").append(r.getHeight()).append(");");
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
                    sb.append("ctx.moveTo(").append(coords[0]).append(",").append(coords[1]).append(");");
                    break;
                case (PathIterator.SEG_LINETO):
                    sb.append("ctx.lineTo(").append(coords[0]).append(",").append(coords[1]).append(");");
                    break;
                case (PathIterator.SEG_QUADTO):
                    sb.append("ctx.quadraticCurveTo(").append(coords[0]).append(",").append(coords[1]).append(coords[2]).append(",").append(coords[3]).append(");");
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
                        sb.append("ctx.lineTo(").append(closePt[0]).append(",").append(closePt[1]).append(");");
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
    
    @Override
    public Font getFont() {
        return this.font;
    }

    @Override
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (this.font.equals(font)) {
            return;
        }
        this.font = font;
        this.sb.append("ctx.font=\"").append(font.getSize()).append("px ").append(font.getFontName()).append("\";");
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.image.createGraphics().getFontMetrics(f);
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        sb.append("ctx.save();");
        if (this.paint instanceof Color) {
            this.sb.append("ctx.fillStyle=\"").append(toCSSColorValue((Color) this.paint)).append("\";");
        } else {
            setPaint(this.paint);
        }
        sb.append("ctx.fillText(\"").append(str).append("\",").append(x).append(",").append(y).append(");");
        sb.append("ctx.restore();");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y); 
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        StringBuilder builder = new StringBuilder();
        int count = iterator.getEndIndex() - iterator.getBeginIndex();
        char c = iterator.first();
        for (int i = 0; i < count; i++) {
            builder.append(c);
            c = iterator.next();
        }
        drawString(builder.toString(), x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void translate(int x, int y) {
        translate((double) x, (double) y);
    }

    @Override
    public void translate(double tx, double ty) {
        AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
        transform(t);
    }

    @Override
    public void rotate(double theta) {
        AffineTransform t = AffineTransform.getRotateInstance(theta);
        transform(t);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void scale(double sx, double sy) {
        AffineTransform t = AffineTransform.getScaleInstance(sx, sy);
        transform(t);
    }

    @Override
    public void shear(double shx, double shy) {
        AffineTransform t = AffineTransform.getShearInstance(shx, shy);
        transform(t);
    }

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
        t.concatenate(this.transform);
        setTransform(t);
    }

    @Override
    public AffineTransform getTransform() {
        return this.transform;
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

    @Override
    public void clip(Shape s) {
        this.clip = s;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.image.createGraphics().getFontRenderContext();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void setXORMode(Color c1) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public Rectangle getClipBounds() {
        return this.clip.getBounds();
    }

    @Override
    public Shape getClip() {
        return this.clip;  // FIXME : should clone?
    }

    @Override
    public void setClip(Shape clip) {
        System.err.println("setClip)" + clip + ")");
        this.clip = clip;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Float(x1, y1, x2, y2));
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle2D.Float(x, y, width, height));
    }

    /**
     * Fills the specified rectangle with the current background color.
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
        Paint saved = getPaint();
        setPaint(getBackground());
        fillRect(x, y, width, height);
        setPaint(saved);
    }

    /**
     * Fills a rectangle with rounded corners.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param arcWidth  the arc-width.
     * @param arcHeight  the arc-height.
     */
    @Override
    public void drawRoundRect(int x, int y, int width, int height, 
            int arcWidth, int arcHeight) {
        if (this.roundRect == null) {
            this.roundRect = new RoundRectangle2D.Double(x, y, width, height, 
                    arcWidth, arcHeight);
        } else {
            this.roundRect.setRoundRect(x, y, width, height, 
                    arcWidth, arcHeight);
        }
        draw(this.roundRect);
    }

    /**
     * Fills a rectangle with rounded corners.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     * @param arcWidth  the arc-width.
     * @param arcHeight  the arc-height.
     */
    @Override
    public void fillRoundRect(int x, int y, int width, int height, 
            int arcWidth, int arcHeight) {
        if (this.roundRect == null) {
            this.roundRect = new RoundRectangle2D.Double(x, y, width, height, 
                    arcWidth, arcHeight);
        } else {
            this.roundRect.setRoundRect(x, y, width, height, 
                    arcWidth, arcHeight);
        }
        fill(this.roundRect);
    }
    
    @Override
    public void drawOval(int x, int y, int width, int height) {
        if (this.oval == null) {
            this.oval = new Ellipse2D.Double(x, y, width, height);
        } else {
            this.oval.setFrame(x, y, width, height);
        }
        draw(this.oval);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
       if (this.oval == null) {
            this.oval = new Ellipse2D.Double(x, y, width, height);
        } else {
            this.oval.setFrame(x, y, width, height);
        }
        fill(this.oval);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param xPoints
     * @param yPoints
     * @param nPoints 
     */
    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param xform
     * @param obs
     * @return 
     */
    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param op
     * @param x
     * @param y 
     */
    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param xform 
     */
    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param xform 
     */
    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported (but no exception is thrown).
     * 
     * @param img
     * @param x
     * @param y
     * @param observer
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        System.err.println("drawImage(Image, int, int, ImageObserver");
        return false;
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
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        System.err.println("drawImage(Image, int, int, int, int, ImageObserver");
        return false;
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param x
     * @param y
     * @param bgcolor
     * @param observer
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bgcolor
     * @param observer
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param observer
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    /**
     * Not yet supported.
     * 
     * @param img
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param bgcolor
     * @param observer
     * @return 
     */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
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
     * Graphics2D implementation.
     * 
     * @return The script.
     */
    public String getScript() {
        return this.sb.toString();
    }
    
}
