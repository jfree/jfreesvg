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

package org.jfree.graphics2d.pdf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import org.jfree.graphics2d.Args;

/**
 * A <code>Stream</code> that contains graphics for the PDF document that
 * can be generated via the {@link PDFGraphics2D} class.  The {@link Page}
 * class will create a <code>GraphicsStream</code> instance to represent its
 * content.  You don't normally interact directly with this class.
 */
public class GraphicsStream extends Stream {

    /** 
     * The page the graphics stream belongs to.  We need this reference to
     * our "parent" so that we can access fonts in the document.
     */
    private Page page;
    
    /** The stream content. */
    private ByteArrayOutputStream content;
    
    /** The most recent font applied. */
    private Font font;
    
    private AffineTransform prevTransInv;
    
    /**
     * The decimal formatter for coordinates of geometrical shapes.
     */
    private DecimalFormat geometryFormat = new DecimalFormat("0.##");
    
    /**
     * Creates a new instance.
     * 
     * @param number  the PDF object number.
     * @param page  the parent page (<code>null</code> not permitted).
     */
    GraphicsStream(int number, Page page) {
        super(number);
        this.page = page;
        this.content = new ByteArrayOutputStream();
        this.font = new Font("Dialog", Font.PLAIN, 12);
    }

    /**
     * Applies a graphics transform.
     * 
     * @param t  the transform.
     */
    public void applyTransform(AffineTransform t) {
        StringBuilder b = new StringBuilder();
        b.append(t.getScaleX()).append(" ");
        b.append(t.getShearY()).append(" ");
        b.append(t.getShearX()).append(" ");
        b.append(t.getScaleY()).append(" ");
        b.append(t.getTranslateX()).append(" ");
        b.append(t.getTranslateY()).append(" cm\n");
        addContent(b.toString());
    }
    
    private void addContent(String s) {
        try {
            this.content.write(PDFUtils.toBytes(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the transform.
     * 
     * @param t  the transform. 
     */
    public void setTransform(AffineTransform t) {
        AffineTransform tt = new AffineTransform(t);
        try {
          AffineTransform inv = tt.createInverse();
          AffineTransform comb;
          if (this.prevTransInv != null) {
            comb = new AffineTransform(this.prevTransInv);
            comb.concatenate(tt);
          } else {
            comb = tt;
          }
          this.prevTransInv = inv;
          applyTransform(comb);
        } catch (NoninvertibleTransformException e) {
          // do nothing
        }
    }

    /**
     * Applies a text transform.
     * 
     * @param t  the transform.
     */
    public void applyTextTransform(AffineTransform t) {
        StringBuilder b = new StringBuilder();
        b.append(t.getScaleX()).append(" ");
        b.append(t.getShearY()).append(" ");
        b.append(t.getShearX()).append(" ");
        b.append(t.getScaleY()).append(" ");
        b.append(t.getTranslateX()).append(" ");
        b.append(t.getTranslateY()).append(" Tm\n");
        addContent(b.toString());
    }
    
    public void applyClip(Shape clip) {
        StringBuilder b = new StringBuilder();
        Path2D p = new Path2D.Double(clip);
        b.append(getPDFPath(p));
        b.append("W n\n");
        addContent(b.toString());
    }
    
    /**
     * Applies a stroke.  If the stroke is not an instance of 
     * <code>BasicStroke</code> this method will do nothing.
     * 
     * @param s  the stroke. 
     */
    public void applyStroke(Stroke s) {
        if (!(s instanceof BasicStroke)) {
            return;
        }
        BasicStroke bs = (BasicStroke) s;
        StringBuilder b = new StringBuilder();
        b.append(bs.getLineWidth()).append(" ").append("w\n");
        b.append(bs.getEndCap()).append(" J\n");
        b.append(bs.getLineJoin()).append(" j\n");
        float[] dashArray = bs.getDashArray();
        if (dashArray != null) {
            b.append(PDFUtils.toPDFArray(dashArray)).append(" 0 d\n");
        } else {
            b.append("[] 0 d\n");
        }
        addContent(b.toString());
    }
    
    /**
     * Applies a color for stroking.
     * 
     * @param c  the color. 
     */
    public void applyStrokeColor(Color c) {
        float red = c.getRed() / 255f;
        float green = c.getGreen() / 255f;
        float blue = c.getBlue() / 255f;
        StringBuilder b = new StringBuilder();
        b.append(red).append(" ").append(green).append(" ").append(blue)
                .append(" RG\n");
        addContent(b.toString());
    }
    
    /**
     * Applies a color for filling.
     * 
     * @param c  the color. 
     */
    public void applyFillColor(Color c) {
        float red = c.getRed() / 255f;
        float green = c.getGreen() / 255f;
        float blue = c.getBlue() / 255f;
        StringBuilder b = new StringBuilder();
        b.append(red).append(" ").append(green).append(" ").append(blue)
                .append(" rg\n");
        addContent(b.toString());
    }
    
    /**
     * Applies a <code>GradientPaint</code> for stroking.
     * 
     * @param gp  the gradient paint (<code>null</code> not permitted). 
     */
    public void applyStrokeGradient(GradientPaint gp) {
        // delegate arg checking
        String patternName = this.page.findOrCreatePattern(gp);
        StringBuilder b = new StringBuilder("/Pattern CS\n");
        b.append(patternName).append(" SCN\n");
        addContent(b.toString());
    }
    
    /**
     * Applies a <code>GradientPaint</code> for filling.
     * 
     * @param gp  the gradient paint (<code>null</code> not permitted). 
     */
    public void applyFillGradient(GradientPaint gp) {
        // delegate arg checking
        Args.nullNotPermitted(gp, "gp");
        String patternName = this.page.findOrCreatePattern(gp);
        StringBuilder b = new StringBuilder("/Pattern cs\n");
        b.append(patternName).append(" scn\n");
        addContent(b.toString());
    }
    
    /**
     * Applies the specified alpha composite.
     * 
     * @param alphaComp  the alpha composite (<code>null</code> not permitted). 
     */
    public void applyComposite(AlphaComposite alphaComp) {
        String name = this.page.findOrCreateGSDictionary(alphaComp);
        StringBuilder b = new StringBuilder();
        b.append(name).append(" gs\n");
        addContent(b.toString());
    }
    
    private String geomDP(double d) {
        if (this.geometryFormat != null) {
            return geometryFormat.format(d);            
        } else {
            return String.valueOf(d);
        }
    }

    /**
     * Draws the specified line.
     * 
     * @param line  the line. 
     */
    public void drawLine(Line2D line) {
        StringBuilder b = new StringBuilder();
        b.append(geomDP(line.getX1())).append(" ").append(geomDP(line.getY1()))
                .append(" ").append("m\n");
        b.append(geomDP(line.getX2())).append(" ").append(geomDP(line.getY2()))
                .append(" ").append("l\n");
        b.append("S\n");
        addContent(b.toString());
    }
    
    /**
     * Draws the specified path.
     * 
     * @param path  the path. 
     */
    public void drawPath2D(Path2D path) {
        StringBuilder b = new StringBuilder();
        b.append(getPDFPath(path)).append("S\n");
        addContent(b.toString());
    }
    
    /**
     * Fills the specified path.
     * 
     * @param path  the path. 
     */
    public void fillPath2D(Path2D path) {
        StringBuilder b = new StringBuilder();
        b.append(getPDFPath(path)).append("f\n");
        addContent(b.toString());
    }
    
    /**
     * Applies the specified font (in fact, no change is made to the stream
     * until the next call to drawString()). 
     * 
     * @param font  the font.
     */
    public void applyFont(Font font) {
        this.font = font;
    }
    
    /**
     * Draws a string at the specified location.
     * 
     * @param text  the text.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public void drawString(String text, float x, float y) {
        // we need to get the reference for the current font (creating a 
        // new font object if there isn't already one)
        String fontRef = this.page.findOrCreateFontReference(this.font);
        StringBuilder b = new StringBuilder("BT ");
        addContent(b.toString());
        AffineTransform t = new AffineTransform(1.0, 0.0, 0.0, -1.0, 0.0, 
                y * 2); 
        applyTextTransform(t);
        b = new StringBuilder();
        b.append(fontRef).append(" ").append(this.font.getSize())
                .append(" Tf ");
        b.append(geomDP(x)).append(" ").append(geomDP(y)).append(" Td (")
                .append(text).append(") Tj ET\n");
        addContent(b.toString());
    }

    /**
     * Draws the specified image into the rectangle <code>(x, y, w, h)</code>.
     * 
     * @param img  the image.
     * @param x  the x-coordinate of the destination.
     * @param y  the y-coordinate of the destination.
     * @param w  the width of the destination.
     * @param h  the height of the destination.
     */
    public void drawImage(Image img, int x, int y, int w, int h) {
        String imageRef = this.page.addImage(img);
        StringBuilder b = new StringBuilder();
        b.append("q\n");
        b.append(geomDP(w)).append(" 0 0 ").append(geomDP(h)).append(" ");
        b.append(geomDP(x)).append(" ").append(geomDP(y)).append(" cm\n");
        b.append(imageRef).append(" Do\n");
        b.append("Q\n");
        addContent(b.toString());
    }

    /**
     * A utility method to convert a <code>Path2D</code> instance to a PDF 
     * path string.
     * 
     * @param path  the path.
     * 
     * @return The string. 
     */
    private String getPDFPath(Path2D path) {
        StringBuilder b = new StringBuilder();   
        float[] coords = new float[6];
        PathIterator iterator = path.getPathIterator(null);
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coords);
            switch (type) {
            case (PathIterator.SEG_MOVETO):
                b.append(geomDP(coords[0])).append(" ");
                b.append(geomDP(coords[1])).append(" m\n");
                break;
            case (PathIterator.SEG_LINETO):
                b.append(geomDP(coords[0])).append(" ");
                b.append(geomDP(coords[1]));
                b.append(" l\n");                
                break;
            case (PathIterator.SEG_QUADTO):
                b.append(geomDP(coords[0])).append(" ");
                b.append(geomDP(coords[1])).append(" ");
                b.append(geomDP(coords[0])).append(" ");
                b.append(geomDP(coords[1])).append(" ");
                b.append(geomDP(coords[2])).append(" ");
                b.append(geomDP(coords[3])).append(" c\n");
                break;
            case (PathIterator.SEG_CUBICTO):
                b.append(geomDP(coords[0])).append(" ");
                b.append(geomDP(coords[1])).append(" ");
                b.append(geomDP(coords[2])).append(" ");
                b.append(geomDP(coords[3])).append(" ");
                b.append(geomDP(coords[4])).append(" ");
                b.append(geomDP(coords[5])).append(" c\n");
                break;
            case (PathIterator.SEG_CLOSE):
                b.append("h\n");
                break;
            default:
                break;
            }
            iterator.next();
        }
        return b.toString();
    }

    @Override
    public byte[] getRawStreamData() {
        return this.content.toByteArray();
    }

}
