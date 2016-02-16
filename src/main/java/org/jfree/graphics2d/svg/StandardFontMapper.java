/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-2016, by Object Refinery Limited.  All rights reserved.
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

package org.jfree.graphics2d.svg;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import org.jfree.graphics2d.Args;

/**
 * A default implementation of the {@link FontMapper} interface.  This 
 * implementation will map the Java logical fonts to equivalent SVG generic
 * fonts.  You can add your own mappings if you need to.
 * 
 * @since 1.5
 */
public class StandardFontMapper implements FontMapper {
    
    /** Storage for the alternates. */
    private Map<String, String> alternates;
    
    /**
     * Creates a new instance with mappings for the Java logical fonts.
     */
    public StandardFontMapper() {
        this.alternates = new HashMap<String, String>();
        this.alternates.put(Font.DIALOG, "sans-serif");
        this.alternates.put(Font.DIALOG_INPUT, "monospace");
        this.alternates.put(Font.SANS_SERIF, "sans-serif");
        this.alternates.put(Font.SERIF, "serif");
        this.alternates.put(Font.MONOSPACED, "monospace");
    }

    /**
     * Returns the mapped (alternate) font family name.
     * 
     * @param family  the font family ({@code null} not permitted).
     * 
     * @return The alternate font family name (possibly {@code null}). 
     */
    public String get(String family) {
        Args.nullNotPermitted(family, "family");
        return this.alternates.get(family);
    }
    
    /**
     * Adds a font family mapping (if the specified alternate is 
     * {@code null} it has the effect of clearing any existing mapping).
     * 
     * @param family  the font family name ({@code null} not permitted).
     * @param alternate  the alternate ({@code null} permitted).
     */
    public void put(String family, String alternate) {
        Args.nullNotPermitted(family, "family");
        this.alternates.put(family, alternate);
    }
    
    /**
     * Maps the specified font family name to an alternative, or else returns
     * the same family name.
     * 
     * @param family  the font family name ({@code null} not permitted).
     * 
     * @return The same font family name or an alternative (never {@code null}).
     */
    @Override
    public String mapFont(String family) {
        Args.nullNotPermitted(family, "family");
        String alternate = this.alternates.get(family);
        if (alternate != null) {
            return alternate;
        }
        return family;
    }

}
