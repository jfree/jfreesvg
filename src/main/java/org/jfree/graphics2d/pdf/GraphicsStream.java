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
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

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
    private StringBuilder content;
    
    /** The most recent font applied. */
    private Font font;
    
    /**
     * Creates a new instance.
     * 
     * @param number  the PDF object number.
     * @param page  the parent page (<code>null</code> not permitted).
     */
    GraphicsStream(int number, Page page) {
      super(number);
      this.page = page;
      this.content = new StringBuilder();    
    }

    /**
     * Applies a graphics transform.
     * 
     * @param t  the transform.
     */
    public void applyTransform(AffineTransform t) {
      this.content.append(t.getScaleX()).append(" ");
      this.content.append(t.getShearY()).append(" ");
      this.content.append(t.getShearX()).append(" ");
      this.content.append(t.getScaleY()).append(" ");
      this.content.append(t.getTranslateX()).append(" ");
      this.content.append(t.getTranslateY()).append(" cm\n");
    }
    
    private AffineTransform prevTransInv;
    
    /**
     * Sets the transform.
     * 
     * @param t  the transform. 
     */
    public void setTransform(AffineTransform t) {
        AffineTransform tt = new AffineTransform(t);
        try {
          AffineTransform inv = tt.createInverse();
          AffineTransform comb = null;
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
          this.content.append(t.getScaleX()).append(" ");
          this.content.append(t.getShearY()).append(" ");
          this.content.append(t.getShearX()).append(" ");
          this.content.append(t.getScaleY()).append(" ");
          this.content.append(t.getTranslateX()).append(" ");
          this.content.append(t.getTranslateY()).append(" Tm\n");
    }
    
    /**
     * Applies a stroke.
     * 
     * @param s  the stroke. 
     */
    public void applyStroke(Stroke s) {
        if (!(s instanceof BasicStroke)) {
            return;
        }
        BasicStroke bs = (BasicStroke) s;
        this.content.append(bs.getLineWidth()).append(" ").append("w\n");
        this.content.append(bs.getEndCap()).append(" J\n");
        this.content.append(bs.getLineJoin()).append(" j\n");
        float[] dashArray = bs.getDashArray();
        if (dashArray != null) {
            this.content.append(PDFUtils.toPDFArray(dashArray)).append(" 0 d\n");
        } else {
            this.content.append("[] 0 d\n");
        }
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
        this.content.append(red).append(" ").append(green).append(" ")
                .append(blue).append(" RG\n");
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
        this.content.append(red).append(" ").append(green).append(" ")
                .append(blue).append(" rg\n");
    }
    
    public void applyStrokeGradient(GradientPaint gp) {
        String patternName = this.page.findOrCreatePattern(gp);
        this.content.append("/Pattern CS\n");
        this.content.append(patternName).append(" SCN\n");
    }
    
    public void applyFillGradient(GradientPaint gp) {
        String patternName = this.page.findOrCreatePattern(gp);
        this.content.append("/Pattern cs\n");
        this.content.append(patternName).append(" scn\n");
    }
    
    /**
     * Applies the specified alpha composite.
     * 
     * @param alphaComp  the alpha composite (<code>null</code> not permitted). 
     */
    public void applyComposite(AlphaComposite alphaComp) {
        String name = this.page.findOrCreateGSDictionary(alphaComp);
        this.content.append(name).append(" gs\n");
    }
    
    /**
     * Draws the specified line.
     * 
     * @param line  the line. 
     */
    public void drawLine(Line2D line) {
        this.content.append(line.getX1()).append(" ").append(line.getY1())
                .append(" ").append("m\n");
        this.content.append(line.getX2()).append(" ").append(line.getY2())
                .append(" ").append("l\n");
        this.content.append("S\n");
    }
    
    /**
     * Draws the specified path.
     * 
     * @param path  the path. 
     */
    public void drawPath2D(Path2D path) {
        this.content.append(getPDFPath(path));
        this.content.append("S\n");
    }
    
    /**
     * Fills the specified path.
     * 
     * @param path  the path. 
     */
    public void fillPath2D(Path2D path) {
        this.content.append(getPDFPath(path));
        this.content.append("f\n");
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
        this.content.append("BT ");
        AffineTransform t = new AffineTransform(1.0, 0.0, 0.0, -1.0, 0.0, 
                y * 2); 
        applyTextTransform(t);
        this.content.append(fontRef).append(" ")
                .append(this.font.getSize()).append(" Tf ");
        this.content.append(x).append(" ").append(y).append(" Td (")
                .append(text).append(") Tj ET\n");
    }

    public void drawImage(Image img, int x, int y, int w, int h) {
        String imageRef = this.page.addImage(img);
        this.content.append("q\n");
        this.content.append(w).append(" 0 0 ").append(h).append(" ");
        this.content.append(x).append(" ").append(y).append(" cm\n");
        this.content.append(imageRef + " Do\n");
        this.content.append("Q\n");
    }
    
    /**
     * Returns a string representing the PDF content of this stream.
     * 
     * @return A string representing the PDF content of this stream. 
     */
    @Override
    public String getStreamContentString() {
        return this.content.toString();
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
                b.append(coords[0]).append(" ");
                b.append(coords[1]).append(" m\n");
                break;
            case (PathIterator.SEG_LINETO):
                b.append(coords[0]).append(" ").append(coords[1]);
                b.append(" l\n");                
                break;
            case (PathIterator.SEG_QUADTO):
                b.append(coords[0]).append(" ").append(coords[1]).append(" ")
                        .append(coords[0]).append(" ").append(coords[1])
                        .append(" ").append(coords[2]).append(" ")
                        .append(coords[3]).append(" c\n");
                break;
            case (PathIterator.SEG_CUBICTO):
                b.append(coords[0]).append(" ").append(coords[1]).append(" ")
                        .append(coords[2]).append(" ").append(coords[3])
                        .append(" ").append(coords[4]).append(" ")
                        .append(coords[5]).append(" c\n");
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
        return PDFUtils.toBytes(getStreamContentString());
    }
    
    public byte[] getFilteredStreamData() {
        return getRawStreamData();
    }
    
}
