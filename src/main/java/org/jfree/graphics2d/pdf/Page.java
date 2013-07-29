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

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.graphics2d.GradientPaintKey;
import org.jfree.graphics2d.pdf.Function.ExponentialInterpolationFunction;
import org.jfree.graphics2d.pdf.Pattern.ShadingPattern;
import org.jfree.graphics2d.pdf.Shading.AxialShading;

/**
 * Represents a page in a {@link PDFDocument}.  Our objective is to be able
 * to write to the page using the {@link PDFGraphics2D} class (see the
 * {@link #getGraphics2D()} method).
 */
public class Page extends PDFObject {
    
    /** The pages of the document. */
    private Pages parent;
 
    /** The page bounds. */
    private Rectangle2D bounds;
    
    /** The page contents. */
    private GraphicsStream contents;
    
    /** The Graphics2D for writing to the page contents. */
    private PDFGraphics2D graphics2d;
    
    /** Page resources. */
    private Dictionary resources;
    
    /**
     * The list of font (names) used on the page.  We let the parent take
     * care of tracking the font objects.
     */
    private List<String> fontsOnPage;
    
    /**
     * A map between gradient paints and the names used to define the
     * associated pattern in the page resources.
     */
    private Map<GradientPaintKey, String> gradientPaintsOnPage;
    
    /** The pattern dictionary for this page. */
    private Dictionary patterns;
    
    /** The transform between Page and Java2D coordinates. */
    private AffineTransform j2DTransform;
    
    /**
     * Creates a new page.
     * 
     * @param number  the PDF object number.
     * @param generation  the PDF object generation number.
     * @param parent  the parent (manages the pages in the 
     *     <code>PDFDocument</code>).
     * @param bounds  the page bounds (<code>null</code> not permitted).
     */
    Page(int number, int generation, Pages parent, Rectangle2D bounds) {
        super(number, generation);
        if (bounds == null) {
            throw new IllegalArgumentException("Null 'bounds' argument.");
        }
        this.parent = parent;
        this.bounds = bounds;
        this.resources = new Dictionary();
        this.resources.put("/ProcSet", "[/PDF /Text]");
        this.fontsOnPage = new ArrayList<String>();
        int n = this.parent.getDocument().getNextNumber();
        this.contents = new GraphicsStream(n, 0, this);
        this.gradientPaintsOnPage = new HashMap<GradientPaintKey, String>();
        this.patterns = new Dictionary();
        
        this.j2DTransform = AffineTransform.getTranslateInstance(0.0, 
                bounds.getHeight());
        j2DTransform.concatenate(AffineTransform.getScaleInstance(1.0, -1.0));
        
    }

    /**
     * Returns the <code>PDFObject</code> that represents the page content.
     * 
     * @return The <code>PDFObject</code> that represents the page content.
     */
    public PDFObject getContents() {
        return this.contents;
    }
    
    /**
     * Returns the {@link PDFGraphics2D} instance for drawing to the page.
     * 
     * @return The <code>PDFGraphics2D</code> instance for drawing to the page.
     */
    public PDFGraphics2D getGraphics2D() {
        if (this.graphics2d == null) {
            this.graphics2d = new PDFGraphics2D(this.contents, 
                    (int) this.bounds.getWidth(), 
                    (int) this.bounds.getHeight());
        }
        return this.graphics2d;
    }

    /**
     * Finds the font reference corresponding to the given Java2D font, 
     * creating a new one if there isn't one already.
     * 
     * @param font  the AWT font.
     * 
     * @return The font reference.
     */
    public String findOrCreateFontReference(Font font) {
        String ref = this.parent.findOrCreateFontReference(font);
        if (!this.fontsOnPage.contains(ref)) {
            this.fontsOnPage.add(ref);
        }
        return ref;
    }
    
    private Dictionary createFontDictionary() {
        Dictionary d = new Dictionary();
        for (String name : this.fontsOnPage) {
            PDFFont f = this.parent.getFont(name);
            d.put(name, f.getReference());
        }
        return d;
    }
    
    /**
     * Returns the name of the pattern for the specified GradientPaint,
     * creating a new pattern if necessary.
     * 
     * @param gp  the gradient (<code>null</code> not permitted).
     * 
     * @return The pattern name. 
     */
    public String findOrCreateGradientPaintResource(GradientPaint gp) {
        GradientPaintKey key = new GradientPaintKey(gp);
        String patternName = this.gradientPaintsOnPage.get(key);
        if (patternName == null) {
            PDFDocument doc = this.parent.getDocument();
            Function f = new ExponentialInterpolationFunction(
                    doc.getNextNumber(), 
                    gp.getColor1().getRGBColorComponents(null), 
                    gp.getColor2().getRGBColorComponents(null));
            doc.addObject(f);
            double[] coords = new double[4];
            coords[0] = gp.getPoint1().getX();
            coords[1] = gp.getPoint1().getY();
            coords[2] = gp.getPoint2().getX();
            coords[3] = gp.getPoint2().getY();
            Shading s = new AxialShading(doc.getNextNumber(), coords, f);
            doc.addObject(s);
            Pattern p = new ShadingPattern(doc.getNextNumber(), s, 
                    this.j2DTransform);
            doc.addObject(p);
            patternName = "/P" + (this.patterns.size() + 1);
            this.patterns.put(patternName, p);
            this.gradientPaintsOnPage.put(key, patternName);
        }
        return patternName; 
    }
    
    /**
     * Returns the PDF string describing this page.  This will eventually
     * be written to the byte array for the PDF document.
     * 
     * @return The PDF string. 
     */
    @Override
    public String getObjectString() {
        Dictionary dictionary = new Dictionary("/Page");
        dictionary.put("/Parent", this.parent);
        dictionary.put("/MediaBox", this.bounds);
        dictionary.put("/Contents", this.contents);
        dictionary.put("/Resources", this.resources);
        if (!this.fontsOnPage.isEmpty()) {
            this.resources.put("/Font", createFontDictionary());
        }
        if (!this.patterns.isEmpty()) {
            this.resources.put("/Pattern", this.patterns);
        }
        String result = dictionary.toPDFString();
        this.resources.remove("/Font");
        this.resources.remove("/Pattern");
        return result;
    }
}
