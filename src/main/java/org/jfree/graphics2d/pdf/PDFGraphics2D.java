/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.pdf;

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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A Graphics2D implementation that writes to PDF format.
 * 
 * Times−Roman Times−Bold Times−Italic Times−BoldItalic
 * Helvetica Helvetica−Bold Helvetica−Oblique Helvetica−BoldOblique
 * Courier Courier−Bold Courier−Oblique Courier−BoldOblique
 * Symbol ZapfDingbats 
 */
public class PDFGraphics2D extends Graphics2D {

    int width;
    
    int height;
    
    /** Rendering hints (all ignored). */
    private RenderingHints hints;
    
    private Paint paint = Color.WHITE;
    
    private Color color = Color.WHITE;
    
    private Color background = Color.WHITE;
    
    private Composite composite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 1.0f);

    private Stroke stroke = new BasicStroke(1.0f);

    private AffineTransform transform = new AffineTransform();

    private Shape clip = new Rectangle2D.Double();
    
    private Font font = new Font("SansSerif", Font.PLAIN, 12);
    
    /** A hidden image used for font metrics. */
    private BufferedImage image = new BufferedImage(10, 10, 
            BufferedImage.TYPE_INT_RGB);

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
    
    private GraphicsStream gs;
    
    public PDFGraphics2D(GraphicsStream gs, int width, int height) {
        this.width = width;
        this.height = height;
        this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.gs = gs;
        // flip the y-axis to match the Java2D convention
        this.gs.applyTransform(AffineTransform.getTranslateInstance(0.0, height));
        this.gs.applyTransform(AffineTransform.getScaleInstance(1.0, -1.0));
        
        AffineTransform textTransform = new AffineTransform();
        textTransform.translate(0.0, 1.0);
        textTransform.scale(1.0, -1.0);
        //this.gs.applyTextTransform(textTransform);
        this.gs.applyFont(getFont());
        this.gs.applyStrokeColor(getColor());
        this.gs.applyFillColor(getColor());
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
            return;
        }
        this.color = c;
        this.gs.applyStrokeColor(c);
        this.gs.applyFillColor(c);
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
        if (this.stroke.equals(s)) {
            return;
        }
        this.stroke = s;
        this.gs.applyStroke(s);
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

    @Override
    public void draw(Shape s) {
        Logger.getLogger(PDFGraphics2D.class.getName()).info("draw(Shape); ");
        if (s instanceof Line2D) {
            Line2D l = (Line2D) s;
            this.gs.drawLine(l);
        } else if (s instanceof Path2D) {
            Path2D p = (Path2D) s;
            this.gs.drawPath2D(p);
        } else {
            System.out.println(s);
            draw(new GeneralPath(s));  // fallback
        }
    }

    @Override
    public void fill(Shape s) {
        Logger.getLogger(PDFGraphics2D.class.getName()).info("fill(Shape);");
        if (s instanceof Path2D) {
            Path2D p = (Path2D) s;
            this.gs.fillPath2D(p);
        } else {
            fill(new GeneralPath(s));  // fallback
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
        this.gs.applyFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.image.createGraphics().getFontMetrics(f);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.image.createGraphics().getFontRenderContext();
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        Logger.getLogger(PDFGraphics2D.class.getName()).info("drawString(String, float, float);");
        this.gs.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y); 
    }

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

    @Override
    public final void translate(int x, int y) {
        translate((double) x, (double) y);
    }

    @Override
    public final void translate(double tx, double ty) {
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
    public final void scale(double sx, double sy) {
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
        AffineTransform prevT = this.transform;
        if (t == null) {
            this.transform = new AffineTransform();
        } else {
            this.transform = new AffineTransform(t);
        }
        //this.gs.setTransform(t);
    }

    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void setXORMode(Color c1) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public Shape getClip() {
        return this.clip;  // FIXME : should clone?
    }

    @Override
    public void setClip(Shape clip) {
        this.clip = clip;
    }

    @Override
    public Rectangle getClipBounds() {
        return this.clip.getBounds();
    }

    @Override
    public void clip(Shape s) {
        this.clip = s;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    /**
     * Draws a line from (x1, y1) to (x2, y2).
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
     * Fills a rectangle with the current paint.
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the rectangle width.
     * @param height  the rectangle height.
     */
    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (this.rect == null) {
            this.rect = new Rectangle2D.Double(x, y, width, height);
        } else {
            this.rect.setRect(x, y, width, height);
        }
        fill(this.rect);
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
        setRoundRect(x, y, width, height, arcWidth, arcHeight);
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
        setRoundRect(x, y, width, height, arcWidth, arcHeight);
        fill(this.roundRect);
    }

    private void setOval(int x, int y, int width, int height) {
        if (this.oval == null) {
            this.oval = new Ellipse2D.Double(x, y, width, height);
        } else {
            this.oval.setFrame(x, y, width, height);
        }
    }
    
    /**
     * Draws an oval framed by the rectangle (x, y, width, height).
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    @Override
    public void drawOval(int x, int y, int width, int height) {
        setOval(x, y, width, height);
        draw(this.oval);
    }

    /**
     * Fills an oval framed by the rectangle (x, y, width, height).
     * 
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param width  the width.
     * @param height  the height.
     */
    @Override
    public void fillOval(int x, int y, int width, int height) {
        setOval(x, y, width, height);
        fill(this.oval);
    }

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
    
    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, 
            int arcAngle) {
        setArc(x, y, width, height, startAngle, arcAngle);
        draw(this.arc);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, 
            int arcAngle) {
        setArc(x, y, width, height, startAngle, arcAngle);
        fill(this.arc);
    }

    /**
     * A utility method used to create a polygon for rendering.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polyline.
     * @param close  closed?
     * @return 
     */
    private GeneralPath createPolygon(int[] xPoints, int[] yPoints, 
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
     * Draws the specified multi-segment line.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polyline.
     */
    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = createPolygon(xPoints, yPoints, nPoints, false);
        draw(p);
    }

    /**
     * Draws the specified polygon.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polygon.
     */
    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = createPolygon(xPoints, yPoints, nPoints, true);
        draw(p);
    }

    /**
     * Fills the specified polygon.
     * 
     * @param xPoints  the x-points.
     * @param yPoints  the y-points.
     * @param nPoints  the number of points to use for the polygon.
     */
    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        GeneralPath p = createPolygon(xPoints, yPoints, nPoints, true);
        fill(p);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, 
            ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, 
            ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");  // TODO
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, 
            Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");  // TODO
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, 
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");  // TODO
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, 
            int sx1, int sy1, int sx2, int sy2, Color bgcolor, 
            ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");  // TODO
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

//ı//    private void writeGraphics2(ByteArrayOutputStream bos) throws UnsupportedEncodingException, IOException {
//        bos.write("5 0 obj\n".getBytes("US-ASCII"));
//        bos.write("<< /Length 883 >>\n".getBytes("US-ASCII"));
//        
//        bos.write(this.gs.getBytes());
//        
//        bos.write("endobj\n".getBytes("US-ASCII"));
//    }
//    
//    private void writeGraphics(ByteArrayOutputStream bos) throws UnsupportedEncodingException, IOException {
//        bos.write("5 0 obj\n".getBytes("US-ASCII"));
//        bos.write("<< /Length 883 >>\n".getBytes("US-ASCII"));
//        bos.write("stream\n".getBytes("US-ASCII"));
//        //69
//        bos.write("% Draw a black line segment, using the default line width.\n150 250 m\n".getBytes("US-ASCII"));
//        //10
//        bos.write("150 350 l\n".getBytes("US-ASCII"));
//        //2
//        bos.write("S\n".getBytes("US-ASCII"));
//        //72
//        bos.write("% Draw a thicker, dashed line segment.\n4 w % Set line width to 4 points\n".getBytes("US-ASCII"));
//        //56
//        bos.write("[4 6] 0 d % Set dash pattern to 4 units on, 6 units off\n".getBytes("US-ASCII"));
//        //10
//        bos.write("150 250 m\n".getBytes("US-ASCII"));
//        //12
//        bos.write("400 250 l\nS\n".getBytes("US-ASCII"));
//        //77
//        bos.write("[] 0 d % Reset dash pattern to a solid line\n1 w % Reset line width to 1 unit\n".getBytes("US-ASCII"));
//        //169
//        bos.write("% Draw a rectangle with a 1-unit red border, filled with light blue.\n1.0 0.0 0.0 RG % Red for stroke color\n0.5 0.75 1.0 rg % Light blue for fill color\n200 300 50 75 re\n".getBytes("US-ASCII"));
//        //2
//        bos.write("B\n".getBytes("US-ASCII"));
//        //74
//        bos.write("% Draw a curve filled with gray and with a colored border.\n0.5 0.1 0.2 RG\n".getBytes("US-ASCII"));
//        //6
//        bos.write("0.7 g\n".getBytes("US-ASCII"));
//        //10
//        bos.write("300 300 m\n".getBytes("US-ASCII"));
//        //26
//        bos.write("300 400 400 400 400 300 c\n".getBytes("US-ASCII"));
//        bos.write("b\nendstream\n".getBytes("US-ASCII"));
//        bos.write("endobj\n".getBytes("US-ASCII"));
//    }
    
    @Override
    public void dispose() {
        // nothing to do
    }
    
}
