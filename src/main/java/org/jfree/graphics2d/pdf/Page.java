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

import org.jfree.graphics2d.pdf.shading.Shading;
import org.jfree.graphics2d.pdf.filter.FlateFilter;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.graphics2d.Args;
import org.jfree.graphics2d.GradientPaintKey;
import org.jfree.graphics2d.pdf.Function.ExponentialInterpolationFunction;
import org.jfree.graphics2d.pdf.Pattern.ShadingPattern;
import org.jfree.graphics2d.pdf.shading.Shading.AxialShading;

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
    
    /** The ExtGState dictionary for the page. */
    private Dictionary graphicsStates;
    
    /** The transform between Page and Java2D coordinates. */
    private AffineTransform j2DTransform;

    private Dictionary xObjects = new Dictionary();

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
        this.fontsOnPage = new ArrayList<String>();
        int n = this.parent.getDocument().getNextNumber();
        this.contents = new GraphicsStream(n, this);
        //this.contents.addFilter(new FlateFilter());
        this.gradientPaintsOnPage = new HashMap<GradientPaintKey, String>();
        this.patterns = new Dictionary();
        this.graphicsStates = new Dictionary();
        
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
     * Returns the name of the pattern for the specified 
     * <code>GradientPaint</code>, reusing an existing pattern if possible, 
     * otherwise creating a new pattern if necessary.
     * 
     * @param gp  the gradient (<code>null</code> not permitted).
     * 
     * @return The pattern name. 
     */
    public String findOrCreatePattern(GradientPaint gp) {
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
    
    private Map<AlphaComposite, String> alphaDictionaries = new HashMap<AlphaComposite, String>();
    
    /**
     * Returns the name of the Graphics State Dictionary that can be used
     * for the specified alpha composite - if there is no existing dictionary
     * then a new one is created.
     * 
     * @param alphaComp  the alpha composite (<code>null</code> not permitted).
     * 
     * @return The graphics state dictionary reference. 
     */
    public String findOrCreateGSDictionary(AlphaComposite alphaComp) {
        Args.nullNotPermitted(alphaComp, "alphaComp");
        String name = this.alphaDictionaries.get(alphaComp);
        if (name == null) {
            PDFDocument pdfDoc = this.parent.getDocument();
            GraphicsStateDictionary gsd = new GraphicsStateDictionary(
                    pdfDoc.getNextNumber());
            gsd.setNonStrokeAlpha(alphaComp.getAlpha());
            gsd.setStrokeAlpha(alphaComp.getAlpha());
            pdfDoc.addObject(gsd);
            name = "/GS" + (this.graphicsStates.size() + 1);
            this.graphicsStates.put(name, gsd);
            this.alphaDictionaries.put(alphaComp, name);
        }
        return name;
    }
    
    /**
     * Adds an image to the page.  This creates the required PDF object, 
     * as well as adding a reference in the <code>xObjects</code> resources.
     * You should not call this method directly, it exists for the use of the
     * {@link PDFGraphics2D#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)} 
     * method.
     * 
     * @param img  the image (<code>null</code> not permitted).
     * 
     * @return The image reference name.
     */
    public String addImage(Image img) {
        Args.nullNotPermitted(img, "img");
        PDFDocument pdfDoc = this.parent.getDocument();
        PDFImage image = new PDFImage(pdfDoc.getNextNumber(), img);
        //image.addFilter(new FlateFilter());
        
        pdfDoc.addObject(image);
        String reference = "/Image" + this.xObjects.size();
        this.xObjects.put(reference, image);
        return reference;
    }
    
    @Override
    public byte[] getObjectBytes() {
        return createDictionary().toPDFBytes();
    }

    private Dictionary createDictionary() {
        Dictionary dictionary = new Dictionary("/Page");
        dictionary.put("/Parent", this.parent);
        dictionary.put("/MediaBox", this.bounds);
        dictionary.put("/Contents", this.contents);
        Dictionary resources = new Dictionary();
        resources.put("/ProcSet", "[/PDF /Text /ImageB /ImageC /ImageI]");
        if (!this.xObjects.isEmpty()) {
            resources.put("/XObject", this.xObjects);
        }
        if (!this.fontsOnPage.isEmpty()) {
            resources.put("/Font", createFontDictionary());
        }
        if (!this.patterns.isEmpty()) {
            resources.put("/Pattern", this.patterns);
        }
        if (!this.graphicsStates.isEmpty()) {
            resources.put("/ExtGState", this.graphicsStates);
        }        
        dictionary.put("/Resources", resources);
        return dictionary;
    }

}
