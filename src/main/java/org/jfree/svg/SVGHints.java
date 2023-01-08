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
 */

package org.jfree.svg;

import java.awt.RenderingHints;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the rendering hints that can be used with the {@link SVGGraphics2D} 
 * class.  The supported hints are:<br>
 * <ul>
 * <li>{@link #KEY_IMAGE_HANDLING} that controls how images are handled 
 * (embedded in the SVG, or referenced externally);</li>
 * <li>{@link #KEY_IMAGE_HREF} that allows the caller to specify the image
 * href attribute for the next image;</li>
 * <li>{@link #KEY_TEXT_RENDERING} that allows configuration of the preferred 
 * value of the SVG {@code text-rendering} attribute in text elements;</li>
 * <li>{@link #KEY_ELEMENT_ID} that allows the caller to specify the element
 * ID for the next element;</li>
 * <li>{@link #KEY_BEGIN_GROUP} tells the {@code SVGGraphics2D} instance 
 * to start a new group element with attributes controlled by the hint value
 * (which may be a {@code String} for the group ID or, more generally, a
 * {@code Map} containing arbitrary attribute values).  Any other
 * {@code Graphics2D} implementation will ignore this hint;</li>
 * <li>{@link #KEY_END_GROUP} tells the {@code SVGGraphics2D} instance 
 * to end a group element.  The hint value is ignored.  The caller assumes 
 * responsibility for balancing the number of {@code KEY_BEGIN_GROUP} and 
 * {@code KEY_END_GROUP} hints.  Any other {@code Graphics2D} 
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
     * Hint value for {@code KEY_IMAGE_HANDLING} to specify that images 
     * should be embedded in the SVG output using PNG data {@code Base64} 
     * encoded.
     */
    public static final Object VALUE_IMAGE_HANDLING_EMBED 
            = "VALUE_IMAGE_HANDLING_EMBED";
    
    /**
     * Hint value for {@code KEY_IMAGE_HANDLING} to say that images should
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
     * Hint value for {@code KEY_TEXT_RENDERING} to set the 
     * {@code text-rendering} attribute in SVG text elements to 'auto'. 
     */
    public static final String VALUE_TEXT_RENDERING_AUTO = "auto";
    
    /**
     * Hint value for {@code KEY_TEXT_RENDERING} to set the 
     * {@code text-rendering} attribute in SVG text elements to 
     * 'optimizeSpeed'. 
     */
    public static final String VALUE_TEXT_RENDERING_SPEED = "optimizeSpeed";
    
    /**
     * Hint value for {@code KEY_TEXT_RENDERING} to set the 
     * {@code text-rendering} attribute in SVG text elements to 
     * 'optimizeLegibility'. 
     */
    public static final String VALUE_TEXT_RENDERING_LEGIBILITY 
            = "optimizeLegibility";
    
    /**
     * Hint value for {@code KEY_TEXT_RENDERING} to set the 
     * {@code text-rendering} attribute in SVG text elements to 
     * 'geometricPrecision'. 
     */
    public static final String VALUE_TEXT_RENDERING_PRECISION 
            = "geometricPrecision";
    
    /**
     * Hint value for {@code KEY_TEXT_RENDERING} to set the 
     * {@code text-rendering} attribute in SVG text elements to 
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
     * Hint key that informs the {@code SVGGraphics2D} that the caller 
     * would like to begin a new group element.  The hint value is either:
     *
     * <ul>
     *     <li>a {@code String} that will be used as the value of the
     *     {@code id} attribute for the group; or</li>
     *     <li>a {@code Map} instance containing arbitrary attribute values for the
     *     group (usually including an {@code id}).</li>
     * </ul>
     * After opening the new group the hint is cleared and it is the caller's
     * responsibility to close the group later using
     * {@link SVGHints#KEY_END_GROUP}.  Groups can be nested.
     * 
     * @since 1.7
     */
    public static final SVGHints.Key KEY_BEGIN_GROUP = new SVGHints.Key(4);

    /**
     * Hint key that informs the {@code SVGGraphics2D} that the caller
     * would like to close a previously opened group element.  The hint
     * value is ignored.
     * 
     * @since 1.7
     */
    public static final SVGHints.Key KEY_END_GROUP = new SVGHints.Key(5);

    /**
     * Hint key that informs the {@code SVGGraphics2D} that the caller
     * would like to add a title element to the output (with the hint value
     * being a string containing the title text).
     * 
     * @since 1.9
     */
    public static final SVGHints.Key KEY_ELEMENT_TITLE = new SVGHints.Key(6);

    /**
     * The key for the hint that controls whether strings are rendered as
     * characters or vector graphics (implemented using {@code TextLayout}).  
     * The latter will result in larger output files but avoids problems with
     * fonts not being available for the viewer.  Valid hint values are 
     * {@link #VALUE_DRAW_STRING_TYPE_STANDARD} and 
     * {@link #VALUE_DRAW_STRING_TYPE_VECTOR}.
     * 
     * @since 2.0
     */
    public static final SVGHints.Key KEY_DRAW_STRING_TYPE = new SVGHints.Key(7);
    
    /**
     * Hint value for {@code KEY_DRAW_STRING_TYPE} to specify that strings
     * should be written to the output using standard SVG text elements.
     * 
     * @since 2.0
     */
    public static final Object VALUE_DRAW_STRING_TYPE_STANDARD 
            = "VALUE_DRAW_STRING_TYPE_STANDARD";
    
    /**
     * Hint value for {@code KEY_DRAW_STRING_TYPE} to say that strings
     * should be written to the output using vector graphics primitives.
     * 
     * @since 2.0
     */
    public static final Object VALUE_DRAW_STRING_TYPE_VECTOR
            = "VALUE_DRAW_STRING_TYPE_VECTOR";
    
    /**
     * A list of keys that are treated as synonyms for KEY_BEGIN_GROUP
     * (the list does not include KEY_BEGIN_GROUP itself).
     */
    private static final List<RenderingHints.Key> beginGroupKeys;
    
    /**
     * A list of keys that are treated as synonyms for KEY_END_GROUP
     * (the list does not include KEY_END_GROUP itself).
     */
    private static final List<RenderingHints.Key> endGroupKeys;
    
    /**
     * A list of keys that are treated as synonyms for KEY_ELEMENT_TITLE
     * (the list does not include KEY_ELEMENT_TITLE itself).
     */
    private static final List<RenderingHints.Key> elementTitleKeys;
    
    static {
        beginGroupKeys = new ArrayList<>();
        endGroupKeys = new ArrayList<>();
        elementTitleKeys = new ArrayList<>();
        if (isOrsonChartsOnClasspath()) {
            beginGroupKeys.add(getOrsonChartsBeginElementKey());
            endGroupKeys.add(getOrsonChartsEndElementKey());
            elementTitleKeys.add(getOrsonChartsElementTitleKey());
        }
        if (isJFreeChartOnClasspath()) {
            beginGroupKeys.add(getJFreeChartBeginElementKey());
            endGroupKeys.add(getJFreeChartEndElementKey());
        }
    }
    
    /**
     * Creates and returns a list of keys that are synonymous with 
     * {@link #KEY_BEGIN_GROUP}.
     * 
     * @return A list (never {@code null}).
     * 
     * @since 1.8
     */
    public static List<RenderingHints.Key> getBeginGroupKeys() {
        return new ArrayList<>(beginGroupKeys);    
    }
    
    /**
     * Adds a key to the list of keys that are synonyms for 
     * {@link SVGHints#KEY_BEGIN_GROUP}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.8
     */
    public static void addBeginGroupKey(RenderingHints.Key key) {
        beginGroupKeys.add(key);
    }
    
    /**
     * Removes a key from the list of keys that are synonyms for
     * {@link SVGHints#KEY_BEGIN_GROUP}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.8
     */
    public static void removeBeginGroupKey(RenderingHints.Key key) {
        beginGroupKeys.remove(key);
    }
    
    /**
     * Clears the list of keys that are treated as synonyms for 
     * {@link SVGHints#KEY_BEGIN_GROUP}.
     * 
     * @since 1.8
     */
    public static void clearBeginGroupKeys() {
        beginGroupKeys.clear();
    }
    
    /**
     * Returns {@code true} if this key is equivalent to 
     * {@link #KEY_BEGIN_GROUP}, and {@code false} otherwise.  The purpose 
     * of this method is to allow certain keys from external packages (such as 
     * JFreeChart and Orson Charts) to use their own keys to drive the 
     * behaviour of {@code SVGHints.KEY_BEGIN_GROUP}.  This has two benefits: 
     * (1) it avoids the necessity to make JFreeSVG a direct dependency, and 
     * (2) it makes the grouping behaviour generic from the point of view of 
     * the external package, rather than SVG-specific.
     * 
     * @param key  the key ({@code null} not permitted)
     * 
     * @return A boolean.
     * 
     * @since 1.8
     */
    public static boolean isBeginGroupKey(RenderingHints.Key key) {
        return SVGHints.KEY_BEGIN_GROUP.equals(key) 
                || beginGroupKeys.contains(key);        
    }

    /**
     * Creates and returns a list of keys that are synonymous with 
     * {@link #KEY_END_GROUP}.
     * 
     * @return A list (never {@code null}).
     * 
     * @since 1.8
     */
    public static List<RenderingHints.Key> getEndGroupKeys() {
        return new ArrayList<>(endGroupKeys);    
    }
    
    /**
     * Adds a key to the list of keys that are synonyms for 
     * {@link SVGHints#KEY_END_GROUP}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.8
     */
    public static void addEndGroupKey(RenderingHints.Key key) {
        endGroupKeys.add(key);
    }
    
    /**
     * Removes a key from the list of keys that are synonyms for
     * {@link SVGHints#KEY_END_GROUP}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.8
     */
    public static void removeEndGroupKey(RenderingHints.Key key) {
        endGroupKeys.remove(key);
    }
    
    /**
     * Clears the list of keys that are treated as synonyms for 
     * {@link SVGHints#KEY_END_GROUP}.
     * 
     * @since 1.8
     */
    public static void clearEndGroupKeys() {
        endGroupKeys.clear();
    }
    
    /**
     * Returns {@code true} if this key is equivalent to 
     * {@link #KEY_END_GROUP}, and {@code false} otherwise.  The purpose 
     * of this method is to allow certain keys from external packages (such as 
     * JFreeChart and Orson Charts) to use their own keys to drive the 
     * behaviour of {@code SVGHints.KEY_END_GROUP}.  This has two benefits: 
     * (1) it avoids the necessity to make JFreeSVG a direct dependency, and 
     * (2) it makes the grouping behaviour generic from the point of view of 
     * the external package, rather than SVG-specific.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @return A boolean.
     * 
     * @since 1.8
     */
    public static boolean isEndGroupKey(RenderingHints.Key key) {
        return SVGHints.KEY_END_GROUP.equals(key) || endGroupKeys.contains(key);        
    }

    /**
     * Creates and returns a list of keys that are synonymous with 
     * {@link #KEY_ELEMENT_TITLE}.
     * 
     * @return A list (never {@code null}).
     * 
     * @since 1.9
     */
    public static List<RenderingHints.Key> getElementTitleKeys() {
        return new ArrayList<>(elementTitleKeys);    
    }
    
    /**
     * Adds a key to the list of keys that are synonyms for 
     * {@link SVGHints#KEY_ELEMENT_TITLE}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.9
     */
    public static void addElementTitleKey(RenderingHints.Key key) {
        elementTitleKeys.add(key);
    }
    
    /**
     * Removes a key from the list of keys that are synonyms for
     * {@link SVGHints#KEY_ELEMENT_TITLE}.
     * 
     * @param key  the key ({@code null} not permitted).
     * 
     * @since 1.9
     */
    public static void removeElementTitleKey(RenderingHints.Key key) {
        elementTitleKeys.remove(key);
    }
    
    /**
     * Clears the list of keys that are treated as synonyms for 
     * {@link SVGHints#KEY_ELEMENT_TITLE}.
     * 
     * @since 1.9
     */
    public static void clearElementTitleKeys() {
        elementTitleKeys.clear();
    }
    
    /**
     * Returns {@code true} if this key is equivalent to 
     * {@link #KEY_ELEMENT_TITLE}, and {@code false} otherwise.  The 
     * purpose of this method is to allow certain keys from external packages 
     * (such as JFreeChart and Orson Charts) to use their own keys to drive the 
     * behaviour of {@code SVGHints.KEY_ELEMENT_TITLE}.  This has two benefits: 
     * (1) it avoids the necessity to make JFreeSVG a direct dependency, and 
     * (2) it makes the element title behaviour generic from the point of view 
     * of the external package, rather than SVG-specific.
     * 
     * @param key  the key ({@code null} not permitted)
     * 
     * @return A boolean.
     * 
     * @since 1.9
     */
    public static boolean isElementTitleKey(RenderingHints.Key key) {
        return SVGHints.KEY_ELEMENT_TITLE.equals(key) 
                || elementTitleKeys.contains(key);        
    }

    /**
     * Returns {@code true} if Orson Charts (version 1.3 or later) is on 
     * the classpath, and {@code false} otherwise.  This method is used to
     * auto-register keys from Orson Charts that should translate to the 
     * behaviour of {@link SVGHints#KEY_BEGIN_GROUP} and 
     * {@link SVGHints#KEY_END_GROUP}.
     * <br><br>
     * The Orson Charts library can be found at
     * http://www.object-refinery.com/orsoncharts/
     * 
     * @return A boolean.
     * 
     * @since 1.8
     */
    private static boolean isOrsonChartsOnClasspath() {
        return (getOrsonChartsBeginElementKey() != null);
    }
    
    /**
     * Returns {@code true} if JFreeChart (1.0.18 or later) is on 
     * the classpath, and {@code false} otherwise.  This method is used to
     * auto-register keys from JFreeChart that should translate to the 
     * behaviour of {@link SVGHints#KEY_BEGIN_GROUP} and 
     * {@link SVGHints#KEY_END_GROUP}.
     * 
     * <p>The JFreeChart library can be found at <a href="http://www.jfree.org/jfreechart/">
     * http://www.jfree.org/jfreechart/</a>.
     * 
     * @return A boolean.
     * 
     * @since 2.0
     */
    private static boolean isJFreeChartOnClasspath() {
        return (getJFreeChartBeginElementKey() != null);
    }

    private static RenderingHints.Key fetchKey(String className, 
            String fieldName) {
        Class<?> hintsClass;
        try {
            hintsClass = Class.forName(className);
            Field f = hintsClass.getDeclaredField(fieldName);
            return (RenderingHints.Key) f.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                 IllegalAccessException e) {
            return null;
        }
    }
    
    private static RenderingHints.Key getOrsonChartsBeginElementKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_BEGIN_ELEMENT");
    }

    private static RenderingHints.Key getOrsonChartsEndElementKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_END_ELEMENT");
    }

    private static RenderingHints.Key getOrsonChartsElementTitleKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_ELEMENT_TITLE");
    }

    private static RenderingHints.Key getJFreeChartBeginElementKey() {
        return fetchKey("org.jfree.chart.ChartHints", "KEY_BEGIN_ELEMENT");
    }

    private static RenderingHints.Key getJFreeChartEndElementKey() {
        return fetchKey("org.jfree.chart.ChartHints", "KEY_END_ELEMENT");
    }

    /**
     * A key for hints used by the {@link SVGGraphics2D} class.
     */
    public static class Key extends RenderingHints.Key {

        /**
         * Creates a new instance.
         * 
         * @param privateKey  the private key. 
         */
        public Key(int privateKey) {
            super(privateKey);    
        }
    
        /**
         * Returns {@code true} if {@code val} is a value that is
         * compatible with this key, and {@code false} otherwise.
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
                case 3: // KEY_ELEMENT_ID
                case 4: // KEY_BEGIN_GROUP
                    return val == null || val instanceof String;
                case 5: // KEY_END_GROUP
                    return true; // the value is ignored
                case 6: // KEY_ELEMENT_TITLE
                    return val instanceof String;
                case 7:
                    return val == null 
                            || VALUE_DRAW_STRING_TYPE_STANDARD.equals(val)
                            || VALUE_DRAW_STRING_TYPE_VECTOR.equals(val);
                default:
                    throw new RuntimeException("Not possible!");
            }
        }
    }
    
}
