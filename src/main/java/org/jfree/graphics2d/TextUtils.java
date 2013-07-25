/* ============================================================================
 * JFreeGraphics2D : a vector graphics output library for the Java(tm) platform
 * ============================================================================
 * 
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
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

package org.jfree.graphics2d;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

/**
 * Text utility functions.
 */
public class TextUtils {

    /**
     * Draws a string such that the specified anchor point is aligned to the
     * given (x, y) location.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x coordinate (Java 2D).
     * @param y  the y coordinate (Java 2D).
     * @param anchor  the anchor location.
     *
     * @return The text bounds (adjusted for the text position).
     */
    public static Rectangle2D drawAlignedString(final String text,
            final Graphics2D g2, final float x, final float y,
            final TextAnchor anchor) {

        final Rectangle2D textBounds = new Rectangle2D.Double();
        final float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor,
                textBounds);
        // adjust text bounds to match string position
        textBounds.setRect(x + adjust[0], y + adjust[1] + adjust[2],
            textBounds.getWidth(), textBounds.getHeight());
        g2.drawString(text, x + adjust[0], y + adjust[1]);
        return textBounds;
    }

    /**
     * A utility method that calculates the anchor offsets for a string.
     * Normally, the (x, y) coordinate for drawing text is a point on the
     * baseline at the left of the text string.  If you add these offsets to
     * (x, y) and draw the string, then the anchor point should coincide with
     * the (x, y) point.
     *
     * @param g2  the graphics device (not <code>null</code>).
     * @param text  the text.
     * @param anchor  the anchor point.
     *
     * @return  The offsets.
     */
    private static float[] deriveTextBoundsAnchorOffsets(final Graphics2D g2,
            final String text, final TextAnchor anchor) {

        final float[] result = new float[2];
        final FontRenderContext frc = g2.getFontRenderContext();
        final Font f = g2.getFont();
        final FontMetrics fm = g2.getFontMetrics(f);
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final LineMetrics metrics = f.getLineMetrics(text, frc);
        final float ascent = metrics.getAscent();
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) -bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) -bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = -descent - leading + (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = halfAscent;

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = -metrics.getDescent() - metrics.getLeading();

        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }

    /**
     * A utility method that calculates the anchor offsets for a string.
     * Normally, the (x, y) coordinate for drawing text is a point on the
     * baseline at the left of the text string.  If you add these offsets to
     * (x, y) and draw the string, then the anchor point should coincide with
     * the (x, y) point.
     *
     * @param g2  the graphics device (not <code>null</code>).
     * @param text  the text.
     * @param anchor  the anchor point.
     * @param textBounds  the text bounds (if not <code>null</code>, this
     *                    object will be updated by this method to match the
     *                    string bounds).
     *
     * @return  The offsets.
     */
    private static float[] deriveTextBoundsAnchorOffsets(final Graphics2D g2,
            final String text, final TextAnchor anchor,
            final Rectangle2D textBounds) {

        final float[] result = new float[3];
        final FontRenderContext frc = g2.getFontRenderContext();
        final Font f = g2.getFont();
        final FontMetrics fm = g2.getFontMetrics(f);
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final LineMetrics metrics = f.getLineMetrics(text, frc);
        final float ascent = metrics.getAscent();
        result[2] = -ascent;
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) -bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) -bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = -descent - leading + (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = halfAscent;

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = -metrics.getDescent() - metrics.getLeading();

        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }
    /**
     * Returns the bounds for the specified text.
     *
     * @param text  the text (<code>null</code> permitted).
     * @param g2  the graphics context (not <code>null</code>).
     * @param fm  the font metrics (not <code>null</code>).
     *
     * @return The text bounds (<code>null</code> if the <code>text</code>
     *         argument is <code>null</code>).
     */
    public static Rectangle2D getTextBounds(final String text,
            final Graphics2D g2, final FontMetrics fm) {

        final Rectangle2D bounds;
        final double width = fm.stringWidth(text);
        final double height = fm.getHeight();
        return new Rectangle2D.Double(0.0, -fm.getAscent(), width, height);
    }
    /**
     * Draws a string that is aligned by one anchor point and rotated about
     * another anchor point.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x-coordinate for positioning the text.
     * @param y  the y-coordinate for positioning the text.
     * @param textAnchor  the text anchor.
     * @param angle  the rotation angle.
     * @param rotationX  the x-coordinate for the rotation anchor point.
     * @param rotationY  the y-coordinate for the rotation anchor point.
     */
    public static void drawRotatedString(final String text,
            final Graphics2D g2, final float x, final float y,
            final TextAnchor textAnchor, final double angle,
            final float rotationX, final float rotationY) {

        if (text == null || text.equals("")) {
            return;
        }
        final float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text,
                textAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle,
                rotationX, rotationY);
    }

    /**
     * Draws a string that is aligned by one anchor point and rotated about
     * another anchor point.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x-coordinate for positioning the text.
     * @param y  the y-coordinate for positioning the text.
     * @param textAnchor  the text anchor.
     * @param angle  the rotation angle (in radians).
     * @param rotationAnchor  the rotation anchor.
     */
    public static void drawRotatedString(final String text, final Graphics2D g2,
            final float x, final float y, final TextAnchor textAnchor,
            final double angle, final TextAnchor rotationAnchor) {

        if (text == null || text.equals("")) {
            return;
        }
        final float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text,
                textAnchor);
        final float[] rotateAdj = deriveRotationAnchorOffsets(g2, text,
                rotationAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1],
                angle, x + textAdj[0] + rotateAdj[0],
                y + textAdj[1] + rotateAdj[1]);

    }
        /**
     * A utility method that calculates the rotation anchor offsets for a
     * string.  These offsets are relative to the text starting coordinate
     * (BASELINE_LEFT).
     *
     * @param g2  the graphics device.
     * @param text  the text.
     * @param anchor  the anchor point.
     *
     * @return  The offsets.
     */
    private static float[] deriveRotationAnchorOffsets(final Graphics2D g2,
            final String text, final TextAnchor anchor) {

        final float[] result = new float[2];
        final FontRenderContext frc = g2.getFontRenderContext();
        final LineMetrics metrics = g2.getFont().getLineMetrics(text, frc);
        final FontMetrics fm = g2.getFontMetrics();
        final Rectangle2D bounds = TextUtils.getTextBounds(text, g2, fm);
        final float ascent = metrics.getAscent();
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.HALF_ASCENT_LEFT) {

            xAdj = 0.0f;

        }
        else if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = descent + leading - (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = descent + leading - (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = -halfAscent;

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = metrics.getDescent() + metrics.getLeading();

        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }
    
        /**
     * A utility method for drawing rotated text.
     * <P>
     * A common rotation is -Math.PI/2 which draws text 'vertically' (with the
     * top of the characters on the left).
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param angle  the angle of the (clockwise) rotation (in radians).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public static void drawRotatedString(final String text, final Graphics2D g2,
            final double angle, final float x, final float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    /**
     * A utility method for drawing rotated text.
     * <P>
     * A common rotation is -Math.PI/2 which draws text 'vertically' (with the
     * top of the characters on the left).
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param textX  the x-coordinate for the text (before rotation).
     * @param textY  the y-coordinate for the text (before rotation).
     * @param angle  the angle of the (clockwise) rotation (in radians).
     * @param rotateX  the point about which the text is rotated.
     * @param rotateY  the point about which the text is rotated.
     */
    public static void drawRotatedString(final String text, final Graphics2D g2,
            final float textX, final float textY, final double angle,
            final float rotateX, final float rotateY) {

        if ((text == null) || (text.equals(""))) {
            return;
        }

        final AffineTransform saved = g2.getTransform();

        // apply the rotation...
        final AffineTransform rotate = AffineTransform.getRotateInstance(
                angle, rotateX, rotateY);
        g2.transform(rotate);

        AttributedString as = new AttributedString(text, 
                g2.getFont().getAttributes());
        g2.drawString(as.getIterator(), textX, textY);
        g2.setTransform(saved);

    }
}


