/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.svg;

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
import java.util.HashMap;
import java.util.Map;

/**
 * A Graphics2D implementation that writes out SVG.
 */
public class SVGGraphics2D extends Graphics2D {
    
    private int width;
    
    private int height;
    
    /** The buffer for all the SVG output. */
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

    /** A map of all the gradients used, and the corresponding id. */
    private Map<GradientPaintKey, String> gradientPaints = new HashMap<GradientPaintKey, String>();
    
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
     * If the current paint is an instance of {@link GradientPaint}, this
     * field will contain the reference id that is used in the DEFS element
     * for that linear gradient.
     */
    private String gradientPaintRef = null;

    /**
     * Creates a new instance.
     */
    public SVGGraphics2D(int width, int height) {
        this.width = width;
        this.height = height;
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
        SVGGraphics2D copy = new SVGGraphics2D(this.width, this.height);
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
     * Returns the paint.  The default value is {@link Color#BLACK}.
     * 
     * @return The paint (never <code>null</code>). 
     */
    @Override
    public Paint getPaint() {
        return this.paint;
    }
    
    /**
     * Sets the paint.
     * 
     * @param paint  the paint (<code>null</code> not permitted). 
     */
    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
        this.gradientPaintRef = null;
        if (paint instanceof Color) {
            setColor((Color) paint);
        } else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) paint;
            GradientPaintKey key = new GradientPaintKey(gp);
            String ref = this.gradientPaints.get(key);
            if (ref == null) {
                int count = this.gradientPaints.keySet().size();
                this.gradientPaints.put(key, "gp" + count);
                this.gradientPaintRef = "gp" + count;
            } else {
                this.gradientPaintRef = ref;
            }
        }
    }

    /**
     * Returns the foreground color. 
     * 
     * @return The foreground color. 
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the foreground color.  This method exists for backwards 
     * compatibility, you should use the {@link #setPaint(java.awt.Paint)}
     * method.
     * 
     * @param c  the color. 
     */
    @Override
    public void setColor(Color c) {
        if (this.color.equals(c)) {
            return;  // nothing to do
        }
        this.color = c;
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
     * Sets the background color.  This is used by the 
     * {@link #clearRect(int, int, int, int)} method.
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
    }

    /**
     * Returns the current stroke.
     * 
     * @return The current stroke. 
     */
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
        if (s instanceof Line2D) {
            Line2D line = (Line2D) s;
            this.sb.append("<line x1=\"").append(line.getX1())
                    .append("\" y1=\"").append(line.getY1()).append("\" x2=\"")
                    .append(line.getX2()).append("\" y2=\"")
                    .append(line.getY2()).append("\"");
            this.sb.append(" style=\"").append(strokeStyle()).append("\"/>");
        } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            this.sb.append("<rect x=\"").append(r.getX()).append("\" y=\"")
                    .append(r.getY()).append("\" width=\"").append(r.getWidth())
                    .append("\" height=\"").append(r.getHeight()).append("\"");
            this.sb.append(" style=\"").append(strokeStyle())
                    .append("; fill: none").append("\"/>");
        } else if (s instanceof Path2D) {
            Path2D path = (Path2D) s;
            this.sb.append("<g style=\"").append(strokeStyle())
                    .append("; fill: none").append("\">");
            this.sb.append("<path ").append(getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        } else {
            draw(new GeneralPath(s));
        }
    }

    /**
     * Creates an SVG path string for the supplied Java2D path.
     * 
     * @param path  the path (<code>null</code> not permitted).
     * 
     * @return An SVG path string. 
     */
    private String getSVGPathData(Path2D path) {
        StringBuilder b = new StringBuilder("d=\"");
        float[] coords = new float[6];
        double[] closePt = null;
        boolean first = true;
        PathIterator iterator = path.getPathIterator(null);
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coords);
            if (!first) {
                b.append(",");
                first = false;
            }
            switch (type) {
            case (PathIterator.SEG_MOVETO):
                closePt = new double[2];
                closePt[0] = coords[0];
                closePt[1] = coords[1];
                b.append("M ").append(coords[0]).append(" ").append(coords[1]);
                break;
            case (PathIterator.SEG_LINETO):
                b.append("L ").append(coords[0]).append(" ").append(coords[1]);
                break;
            case (PathIterator.SEG_QUADTO):
                b.append("Q ").append(coords[0])
                        .append(" ").append(coords[1])
                        .append(" ").append(coords[2])
                        .append(" ").append(coords[3]);
                break;
            case (PathIterator.SEG_CUBICTO):
                b.append("C ").append(coords[0]).append(" ")
                        .append(coords[1]).append(" ")
                        .append(coords[2]).append(" ")
                        .append(coords[3]).append(" ")
                        .append(coords[4]).append(" ")
                        .append(coords[5]);
                break;
            case (PathIterator.SEG_CLOSE):
                if (closePt != null) {
                    b.append("M ").append(closePt[0]).append(" ")
                            .append(closePt[1]);
                }
                break;
            default:
                break;
            }
            iterator.next();
        }  
        return b.append("\"").toString();
    }

    /**
     * Returns the current alpha (transparency) in the range 0.0 to 1.0.
     * If the current composite is an {@link AlphaComposite} we read the alpha
     * value from there, otherwise this method returns 1.0.
     * 
     * @return The current alpha (transparency) in the range 0.0 to 1.0.
     */
    private float getAlpha() {
       float alpha = 1.0f;
       if (this.composite instanceof AlphaComposite) {
           AlphaComposite ac = (AlphaComposite) this.composite;
           alpha = ac.getAlpha();
       }
       return alpha;
    }

    /**
     * Returns an SVG color string based on the current paint.  To handle
     * GradientPaint we rely on the setPaint() method having set the 
     * gradientPaintRef attribute.
     * 
     * @return An SVG color string. 
     */
    private String getSVGColor() {
        String result = "black;";
        if (this.paint instanceof Color) {
            return getSVGColor((Color) this.paint);
        } else if (this.paint instanceof GradientPaint) {
            return "url(#" + this.gradientPaintRef + ")";
        }
        return result;
    }
    
    /**
     * Returns the SVG RGB color string for the specified color.
     * 
     * @param c  the color.
     * 
     * @return The SVG RGB color string.
     */
    private String getSVGColor(Color c) {
        StringBuilder b = new StringBuilder("rgb(");
        b.append(c.getRed()).append(",").append(c.getGreen()).append(",")
                .append(c.getBlue()).append(")");
        return b.toString();
    }
    
    /**
     * Returns a stroke style string based on the current stroke and
     * alpha settings.
     * 
     * @return A stroke style string.
     */
    private String strokeStyle() {
        float strokeWidth = 1.0f;
        float[] dashArray = new float[0];
        if (this.stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) this.stroke;
            strokeWidth = bs.getLineWidth();
            dashArray = bs.getDashArray();
        }
        StringBuilder b = new StringBuilder();
        b.append("stroke-width: ").append(strokeWidth).append(";");
        b.append("stroke: ").append(getSVGColor()).append(";");
        b.append("stroke-opacity: ").append(getAlpha()).append(";");
        if (dashArray != null && dashArray.length != 0) {
            b.append("stroke-dasharray: ");
            for (int i = 0; i < dashArray.length; i++) {
                if (i != 0) b.append(", ");
                b.append(dashArray[i]);
            }
            b.append(";");
        }
        return b.toString();
    }
    
    /**
     * Returns a fill style string based on the current paint and
     * alpha settings.
     * 
     * @return A fill style string.
     */
    private String getSVGFillStyle() {
        StringBuilder b = new StringBuilder();
        b.append("fill: ").append(getSVGColor()).append(";");
        b.append("fill-opacity: ").append(getAlpha());
        return b.toString();
    }
    
    @Override
    public void fill(Shape s) {
        if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            this.sb.append("<rect x=\"").append(r.getX()).append("\" y=\"")
                    .append(r.getY()).append("\" width=\"").append(r.getWidth())
                    .append("\" height=\"").append(r.getHeight()).append("\"");
            this.sb.append(" style=\"").append(getSVGFillStyle())
                    .append("\"/>");        
        } else if (s instanceof Path2D) {
            Path2D path = (Path2D) s;
            this.sb.append("<g style=\"").append(getSVGFillStyle())
                    .append("; stroke: none").append("\">");
            this.sb.append("<path ").append(getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        }  else {
            fill(new GeneralPath(s));
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
    }
    
    private String getSVGFontStyle() {
         StringBuilder b = new StringBuilder();
         b.append("fill: ").append(getSVGColor()).append("; ");
         b.append("font-family: ").append(this.font.getFamily()).append("; ");
         b.append("font-size: ").append(this.font.getSize()).append(";");
         if (this.font.isBold()) {
             b.append("font-weight: bold; ");
         }
         if (this.font.isItalic()) {
             b.append("font-style: italic; ");
         }
         return b.toString();
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
        this.sb.append("<g ");
        this.sb.append("transform=\"matrix(");
        this.sb.append(this.transform.getScaleX()).append(","); // m00
        this.sb.append(this.transform.getShearY()).append(","); // m10
        this.sb.append(this.transform.getShearX()).append(","); // m01
        this.sb.append(this.transform.getScaleY()).append(",");  // m11
        this.sb.append(this.transform.getTranslateX()).append(","); // m02
        this.sb.append(this.transform.getTranslateY()); // m12

        this.sb.append(")\">");
        this.sb.append("<text x=\"").append(x).append("\", y=\"").append(y).append("\"");
        this.sb.append(" style=\"").append(getSVGFontStyle()).append("\">");
        this.sb.append(str).append("</text>");
        this.sb.append("</g>");
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
        translate(x, y);
        rotate(theta);
        translate(-x, -y);
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
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
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
     * Draws a rectangle with rounded corners.
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
    public String getSVG() {
        StringBuilder svg = new StringBuilder("<svg width=\"").append(width).append("\" height=\"").append(height).append("\">\n");
        StringBuilder defs = new StringBuilder("<defs>");
        for (GradientPaintKey key : this.gradientPaints.keySet()) {
            defs.append(getLinearGradientElement(this.gradientPaints.get(key), key.getPaint()));
            defs.append("\n");
        }
        defs.append("</defs>");
        svg.append(defs);
        svg.append(sb);
        svg.append("</svg>");
        return svg.toString();
    }
    
    private String getLinearGradientElement(String id, GradientPaint paint) {
        StringBuilder b = new StringBuilder("<lineargradient id=\"").append(id).append("\" ");
        Point2D p1 = paint.getPoint1();
        Point2D p2 = paint.getPoint2();
        boolean h = p1.getX() != p2.getX();
        boolean v = p1.getY() != p2.getY();
        b.append("x1=\"").append(h ? "0%" : "50%").append("\" ");
        b.append("y1=\"").append(v ? "0%" : "50%").append("\" ");
        b.append("x2=\"").append(h ? "100%" : "50%").append("\" ");
        b.append("y2=\"").append(v ? "100%" : "50%").append("\">");
        b.append("<stop offset=\"0%\" style=\"stop-color: ").append(getSVGColor(paint.getColor1())).append(";\"/>");
        b.append("<stop offset=\"100%\" style=\"stop-color: ").append(getSVGColor(paint.getColor2())).append(";\"/>");
        return b.append("</lineargradient>").toString();
    }
    
}
