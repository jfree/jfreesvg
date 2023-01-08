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

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jfree.svg.util.Args;

/**
 * The standard function used in JFreeSVG to create font references for the
 * SVG output.  This implementation will substitute SVG generic font names
 * for the Java logical fonts.  Methods are provided for adding more font
 * substitutions if you require them.  This function will also surround the
 * font name with single-quotes, which addresses issue #27.
 * 
 * @since 5.0
 */
public class StandardFontFunction implements Function<String, String> {
    
    /** A map of substitute font family names. */
    private final Map<String, String> alternates;
    
    /**
     * Creates a new instance with mappings for the Java logical fonts.
     */
    public StandardFontFunction() {
        this.alternates = new HashMap<>();
        this.alternates.put(Font.DIALOG, "sans-serif");
        this.alternates.put(Font.DIALOG_INPUT, "monospace");
        this.alternates.put(Font.SANS_SERIF, "sans-serif");
        this.alternates.put(Font.SERIF, "serif");
        this.alternates.put(Font.MONOSPACED, "monospace");
    }

    /**
     * Returns the (substitute) SVG font family name for the specified Java font
     * family name, or {@code null} if there is no substitute.
     * 
     * @param family  the Java font family name ({@code null} not permitted).
     * 
     * @return The substitute SVG font family name, or {@code null}. 
     */
    public String get(String family) {
        Args.nullNotPermitted(family, "family");
        return this.alternates.get(family);
    }
    
    /**
     * Adds a substitute font family name (to be used in the SVG output) for 
     * the given Java font family name.  If the specified alternate is 
     * {@code null} it has the effect of clearing any existing substitution.
     * 
     * @param family  the Java font family name ({@code null} not permitted).
     * @param substitute  the font family name to substitute in the SVG 
     *     output ({@code null} permitted).
     */
    public void put(String family, String substitute) {
        Args.nullNotPermitted(family, "family");
        this.alternates.put(family, substitute);
    }
    
    /**
     * Returns the SVG font reference for the supplied (Java) font family 
     * name.  This implementation provides substitute names for the Java
     * logical fonts.  Other Java font family names are not changed, but all
     * font family names are surrounded in single quotes for the SVG output.
     * 
     * @param family  the font family name ({@code null} not permitted).
     * 
     * @return The SVG font reference (never {@code null}).
     */
    @Override
    public String apply(String family) {
        Args.nullNotPermitted(family, "family");
        String alternate = this.alternates.get(family);
        if (alternate == null) {
            alternate = "\"" + family + "\"";
        }
        return alternate;
    }

}
