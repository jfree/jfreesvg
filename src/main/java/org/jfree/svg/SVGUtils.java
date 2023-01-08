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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.DoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.jfree.svg.util.Args;
import org.jfree.svg.util.DoubleConverter;
import org.jfree.svg.util.RyuDouble;

/**
 * Utility methods related to the {@link SVGGraphics2D} implementation.
 */
public class SVGUtils {
    
    private SVGUtils() {
        // no need to instantiate this
    }

    /**
     * Returns a new string where any special characters in the source string
     * have been encoded.
     * 
     * @param source  the source string ({@code null} not permitted).
     * 
     * @return A new string with special characters escaped for XML.
     * 
     * @since 1.5
     */
    public static String escapeForXML(String source) {
        Args.nullNotPermitted(source, "source");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<' : {
                    sb.append("&lt;");
                    break;
                } 
                case '>' : {
                    sb.append("&gt;");
                    break;
                } 
                case '&' : {
                    String next = source.substring(i, Math.min(i + 6, 
                            source.length()));
                    if (next.startsWith("&lt;") || next.startsWith("&gt;") 
                            || next.startsWith("&amp;") 
                            || next.startsWith("&apos;")
                            || next.startsWith("&quot;")) {
                        sb.append(c); 
                    } else {
                        sb.append("&amp;");
                    }
                    break;
                } 
                case '\'' : {
                    sb.append("&apos;");
                    break;
                } 
                case '\"' : {
                    sb.append("&quot;");
                    break;
                } 
                default : sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Writes a file containing the SVG element.
     * 
     * @param file  the file ({@code null} not permitted).
     * @param svgElement  the SVG element ({@code null} not permitted).
     * 
     * @throws IOException if there is an I/O problem.
     * 
     * @since 1.2
     */
    public static void writeToSVG(File file, String svgElement) 
            throws IOException {
        writeToSVG(file, svgElement, false);
    }
    
    /**
     * Writes a file containing the SVG element.
     * 
     * @param file  the file ({@code null} not permitted).
     * @param svgElement  the SVG element ({@code null} not permitted).
     * @param zip  compress the output.
     * 
     * @throws IOException if there is an I/O problem.
     * 
     * @since 3.0
     */
    public static void writeToSVG(File file, String svgElement, boolean zip) 
            throws IOException {    
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            if (zip) {
                os = new GZIPOutputStream(os);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);
            writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
            writer.write(svgElement + "\n");
            writer.flush();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } 
    }
    
    /**
     * Writes an HTML file containing an SVG element.
     * 
     * @param file  the file.
     * @param title  the title.
     * @param svgElement  the SVG element.
     * 
     * @throws IOException if there is an I/O problem.
     */
    public static void writeToHTML(File file, String title, String svgElement) 
            throws IOException {
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>" + title + "</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n"); 
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write(svgElement + "\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SVGUtils.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        } 
    }

    /**
     * Returns a string representing the specified double value.  Internally
     * this method is using the code from: https://github.com/ulfjack/ryu which
     * is optimised for speed.
     * 
     * @param d  the value.
     * 
     * @return A string representation of the double.
     * 
     * @since 5.0
     */
    public static String doubleToString(double d) {
        return RyuDouble.doubleToString(d);
    }
    
    /**
     * Returns a double-to-string function that limits the output to a 
     * specific number of decimal places (in the range 1 to 10).
     * 
     * @param dp  the decimal places (required in the range 1 to 10).
     * 
     * @return  The converter.
     * 
     * @since 5.0
     */
    public static DoubleFunction<String> createDoubleConverter(int dp) {
        return new DoubleConverter(dp);
    }
    
}
