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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * A Graphics2D implementation that writes out SVG.
 */
public class SVGGraphics2D extends Graphics2D {
    
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
    
    /**
     * Creates a new instance.
     */
    public SVGGraphics2D(int width, int height) {
        this.sb = new StringBuilder("<svg width=\"").append(width).append("\" height=\"").append(height).append("\">\n");
        this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
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
            this.sb.append("<line x1=\"").append(line.getX1()).append("\" y1=\"").append(line.getY1()).append("\" x2=\"").append(line.getX2()).append("\" y2=\"").append(line.getY2()).append("\"");
            this.sb.append(" style=\"").append(strokeStyle()).append("\"/>");
        } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            this.sb.append("<rect x=\"").append(r.getX()).append("\" y=\"").append(r.getY()).append("\" width=\"").append(r.getWidth()).append("\" height=\"").append(r.getHeight()).append("\"");
            this.sb.append(" style=\"").append(strokeStyle()).append("; fill: none").append("\"/>");
        } else if (s instanceof Path2D) {
            Path2D path = (Path2D) s;
            this.sb.append("<g style=\"").append(strokeStyle()).append("; fill: none").append("\">");
            this.sb.append("<path ").append(getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        } else {
            System.out.println("draw(" + s + ")");
        }
    }

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
                b.append("Q ").append(coords[0]).append(" ").append(coords[1]).append(" ").append(coords[2]).append(" ").append(coords[3]);
                break;
            case (PathIterator.SEG_CUBICTO):
                b.append("C ").append(coords[0]).append(" ").append(coords[1]).append(" ").append(coords[2]).append(" ").append(coords[3]).append(" ").append(coords[4]).append(" ").append(coords[5]);
                break;
            case (PathIterator.SEG_CLOSE):
                if (closePt != null) {
                    b.append("M ").append(closePt[0]).append(" ").append(closePt[1]);
                }
                break;
            default:
                break;
            }
            iterator.next();
        }  
        return b.append("\"").toString();
    }
    
    private float getAlpha() {
       float alpha = 1.0f;
       if (this.composite instanceof AlphaComposite) {
           AlphaComposite ac = (AlphaComposite) this.composite;
           alpha = ac.getAlpha();
       }
       return alpha;
    }
    
    private String getSVGColor() {
        String result = "black;";
        if (this.paint instanceof Color) {
            Color c = (Color) this.paint;
            result = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
        }
        return result;
    }
    
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
            this.sb.append("<rect x=\"").append(r.getX()).append("\" y=\"").append(r.getY()).append("\" width=\"").append(r.getWidth()).append("\" height=\"").append(r.getHeight()).append("\"");
            this.sb.append(" style=\"").append(getSVGFillStyle()).append("\"/>");        
        } else if (s instanceof Path2D) {
            Path2D path = (Path2D) s;
            this.sb.append("<g style=\"").append(getSVGFillStyle()).append("; stroke: none").append("\">");
            this.sb.append("<path ").append(getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        }  else {
            System.err.println("*fill(" + s + ")");
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
        throw new UnsupportedOperationException("Not supported yet."); //TODO
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
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void transform(AffineTransform t) {
        this.transform.concatenate(t);
        System.out.println("transform(" + t + ")");
    }

    @Override
    public AffineTransform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
        System.out.println("transform(" + transform + ")");
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
        throw new UnsupportedOperationException("Not supported yet."); //TODO
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

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO
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
        StringBuilder builder = new StringBuilder(sb);
        builder.append("</svg>");
        return builder.toString();
    }
    
}
