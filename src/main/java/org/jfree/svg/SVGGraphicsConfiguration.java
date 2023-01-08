/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-present, by David Gilbert.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreesvg/index.html
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
 * http://www.jfree.org/jfreesvg
 * 
 */

package org.jfree.svg;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

/**
 * A graphics configuration for the {@link SVGGraphics2D} class.
 */
public class SVGGraphicsConfiguration extends GraphicsConfiguration {

    private GraphicsDevice device;
    
    private final int width;
    private final int height;
    
    /**
     * Creates a new instance.
     * 
     * @param width  the width of the bounds.
     * @param height  the height of the bounds.
     */
    public SVGGraphicsConfiguration(int width, int height) {
      super(); 
      this.width = width;
      this.height = height;
    }
    
    /**
     * Returns the graphics device that this configuration is associated with.
     * 
     * @return The graphics device (never {@code null}).
     */
    @Override
    public GraphicsDevice getDevice() {
        if (this.device == null) {
            this.device = new SVGGraphicsDevice("JFreeSVG-GraphicsDevice", this);
        }
        return this.device;
    }

    /**
     * Returns the color model for this configuration.
     * 
     * @return The color model.
     */
    @Override
    public ColorModel getColorModel() {
        return getColorModel(Transparency.TRANSLUCENT);
    }

    /**
     * Returns the color model for the specified transparency type, or 
     * {@code null}.
     * 
     * @param transparency  the transparency type.
     * 
     * @return A color model (possibly {@code null}).
     */
    @Override
    public ColorModel getColorModel(int transparency) {
        switch (transparency) {
            case Transparency.TRANSLUCENT:
                return ColorModel.getRGBdefault();
            case Transparency.OPAQUE:
                return new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff);
            default:
                return null;
        }
    }

    /**
     * Returns the default transform.
     * 
     * @return The default transform. 
     */
    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }

    /**
     * Returns the normalizing transform.
     * 
     * @return The normalizing transform. 
     */
    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }
    
    /**
     * Returns the bounds for this configuration.
     * 
     * @return The bounds. 
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.width, this.height);
    }

    /**
     * Creates a compatible image. This override is only here to provide
     * support for Java 6 because from Java 7 onwards the super class has a
     * non-abstract implementation for this method.
     * 
     * @param width  the width.
     * @param height  the height.
     * 
     * @return A compatible image. 
     */
    @Override
    public BufferedImage createCompatibleImage(int width, int height) {
        ColorModel model = getColorModel();
        WritableRaster raster = model.createCompatibleWritableRaster(width, 
                height);
        return new BufferedImage(model, raster, model.isAlphaPremultiplied(), 
                null);
    }

    private BufferedImage img;
    private GraphicsConfiguration gc;
    
    /**
     * Returns a volatile image.  This method is a workaround for a
     * ClassCastException that occurs on MacOSX when exporting a Swing UI
     * that uses the Nimbus Look and Feel to SVG.
     * 
     * @param width  the image width.
     * @param height  the image height.
     * @param caps  the image capabilities.
     * @param transparency  the transparency.
     * 
     * @return The volatile image.
     * 
     * @throws AWTException if there is a problem creating the image.
     */
    @Override
    public VolatileImage createCompatibleVolatileImage(int width, int height, 
            ImageCapabilities caps, int transparency) throws AWTException {
        if (img == null) {
            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            gc = img.createGraphics().getDeviceConfiguration();
        }
        return gc.createCompatibleVolatileImage(width, height, caps, 
                transparency);
    }

}