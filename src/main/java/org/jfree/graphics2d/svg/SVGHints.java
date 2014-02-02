/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013, 2014, by Object Refinery Limited.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreesvg/index.html
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
 * If you do not wish to be bound by the terms of the AGPL, an alternative
 * commercial license can be purchased.  For details, please see visit the
 * JFreeSVG home page:
 * 
 * http://www.jfree.org/jfreesvg
 */

package org.jfree.graphics2d.svg;

import java.awt.RenderingHints;

/**
 * Defines the rendering hints that can be used with the {@link SVGGraphics2D} 
 * class.  The supported hints are:<br>
 * <ul>
 * <li>{@link #KEY_IMAGE_HANDLING} that controls how images are handled 
 * (embedded in the SVG, or referenced externally);</li>
 * <li>{@link #KEY_IMAGE_HREF} that allows the caller to specify the image
 * href attribute for the next image;</li>
 * <li>{@link #KEY_TEXT_RENDERING} that allows configuration of the preferred 
 * value of the SVG <code>text-rendering</code> attribute in text elements;</li>
 * <li>{@link #KEY_ELEMENT_ID} that allows the caller to specify the element
 * ID for the next element;</li>
 * <li>{@link #KEY_BEGIN_GROUP} tells the <code>SVGGraphics2D</code> instance 
 * to start a new group element with an id equal to the hint value (which must 
 * be an instance of String).  Any other <code>Graphics2D</code> implementation 
 * will ignore this hint;</li>
 * <li>{@link #KEY_END_GROUP} tells the <code>SVGGraphics2D</code> instance 
 * to end a group element.  The hint value is ignored.  The caller assumes 
 * responsibility for balancing the number of <code>KEY_BEGIN_GROUP</code> and 
 * <code>KEY_END_GROUP</code> hints.  Any other <code>Graphics2D</code> 
 * implementation will ignore this hint.</li>
 * </ul>
 * 
 */
public final class SVGHints {

    private SVGHints() {
        // no need to instantiate this    
    }
    
    /**
     * The key for the hint that controls whether images are embedded in the
     * SVG or referenced externally.  Valid hint values are 
     * {@link #VALUE_IMAGE_HANDLING_EMBED} and 
     * {@link #VALUE_IMAGE_HANDLING_REFERENCE}.
     */
    public static final SVGHints.Key KEY_IMAGE_HANDLING = new SVGHints.Key(0);
    
    /**
     * Hint value for <code>KEY_IMAGE_HANDLING</code> to specify that images 
     * should be embedded in the SVG output using PNG data <code>Base64</code> 
     * encoded.
     */
    public static final Object VALUE_IMAGE_HANDLING_EMBED 
            = "VALUE_IMAGE_HANDLING_EMBED";
    
    /**
     * Hint value for <code>KEY_IMAGE_HANDLING</code> to say that images should
     * be referenced externally.
     */
    public static final Object VALUE_IMAGE_HANDLING_REFERENCE 
            = "VALUE_IMAGE_HANDLING_REFERENCE";
    
    /**
     * The key for a hint that permits configuration of the <a 
     * href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-rendering">text-rendering 
     * attribute</a> in SVG text elements
     */
    public static final SVGHints.Key KEY_TEXT_RENDERING = new SVGHints.Key(1);
     
    /**
     * Hint value for <code>KEY_TEXT_RENDERING</code> to set the 
     * <code>text-rendering</code> attribute in SVG text elements to 'auto'. 
     */
    public static final String VALUE_TEXT_RENDERING_AUTO = "auto";
    
    /**
     * Hint value for <code>KEY_TEXT_RENDERING</code> to set the 
     * <code>text-rendering</code> attribute in SVG text elements to 
     * 'optimizeSpeed'. 
     */
    public static final String VALUE_TEXT_RENDERING_SPEED = "optimizeSpeed";
    
    /**
     * Hint value for <code>KEY_TEXT_RENDERING</code> to set the 
     * <code>text-rendering</code> attribute in SVG text elements to 
     * 'optimizeLegibility'. 
     */
    public static final String VALUE_TEXT_RENDERING_LEGIBILITY 
            = "optimizeLegibility";
    
    /**
     * Hint value for <code>KEY_TEXT_RENDERING</code> to set the 
     * <code>text-rendering</code> attribute in SVG text elements to 
     * 'geometricPrecision'. 
     */
    public static final String VALUE_TEXT_RENDERING_PRECISION 
            = "geometricPrecision";
    
    /**
     * Hint value for <code>KEY_TEXT_RENDERING</code> to set the 
     * <code>text-rendering</code> attribute in SVG text elements to 
     * 'inherit'. 
     */
    public static final String VALUE_TEXT_RENDERING_INHERIT = "inherit";
    
    /**
     * Hint key to supply string to be used as the href for an image that is 
     * referenced rather than embedded.  The value associated with the key 
     * should be a string and will be used for the next image element written 
     * to the SVG output (and then the hint will be cleared).
     * 
     * @since 1.5
     */
    public static final SVGHints.Key KEY_IMAGE_HREF = new SVGHints.Key(2);
    
    /**
     * Hint key to supply an element id for the next element generated.
     * 
     * @since 1.5
     */
    public static final SVGHints.Key KEY_ELEMENT_ID = new SVGHints.Key(3);

    /**
     * Hint key that informs the <code>SVGGraphics2D</code> that the caller 
     * would like to begin a new group element.  The hint value is the id for 
     * the new group.  After opening the new group the hint is cleared and it 
     * is the caller's responsibility to close the group later using 
     * {@link SVGHints.KEY_CLOSE_GROUP}.  Groups can be nested.
     * 
     * @since 1.7
     */
    public static final SVGHints.Key KEY_BEGIN_GROUP = new SVGHints.Key(4);
    
    /**
     * Hint key that informs the <code>SVGGraphics2D</code> that the caller
     * would like to close a previously opened group element.  The hint
     * value is ignored.
     * 
     * @since 1.7
     */
    public static final SVGHints.Key KEY_END_GROUP = new SVGHints.Key(5);

    /**
     * A key for hints used by the {@link SVGGraphics2D} class.
     */
    public static class Key extends RenderingHints.Key {

        public Key(int privateKey) {
            super(privateKey);    
        }
    
        /**
         * Returns <code>true</code> if <code>val</code> is a value that is
         * compatible with this key, and <code>false</code> otherwise.
         * 
         * @param val  the value.
         * 
         * @return A boolean. 
         */
        @Override
        public boolean isCompatibleValue(Object val) {
            switch (intKey()) {
                case 0:
                    return VALUE_IMAGE_HANDLING_EMBED.equals(val)
                            || VALUE_IMAGE_HANDLING_REFERENCE.equals(val);
                case 1:
                    return VALUE_TEXT_RENDERING_AUTO.equals(val)
                            || VALUE_TEXT_RENDERING_INHERIT.equals(val)
                            || VALUE_TEXT_RENDERING_LEGIBILITY.equals(val)
                            || VALUE_TEXT_RENDERING_PRECISION.equals(val)
                            || VALUE_TEXT_RENDERING_SPEED.equals(val);
                case 2: // KEY_IMAGE:URL
                    return val == null || val instanceof String;
                case 3: // KEY_ELEMENT_ID
                    return val == null || val instanceof String;    
                case 4: // KEY_BEGIN_GROUP
                    return val == null || val instanceof String;
                case 5: // KEY_END_GROUP
                    return true; // the value is ignored
                default:
                    throw new RuntimeException("Not possible!");
            }
        }
    }
    
}
