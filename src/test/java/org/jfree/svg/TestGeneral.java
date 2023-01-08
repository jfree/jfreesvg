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

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.geom.*;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Some general tests that check the SVG output generated.
 */
public class TestGeneral {

    /**
     * Check that the SVG document generates as expected.
     */
    @Test
    public void checkGetSVGDocument() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        assertEquals("<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n" +
                "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>\n", g2.getSVGDocument());
    }

    /**
     * Check that the optional ID is written correctly whether it is null or non-null.
     */
    @Test
    public void checkGetSVGElementWithID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        assertEquals("<svg id='ID1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
                "<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>", g2.getSVGElement("ID1"));
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
                "<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>", g2.getSVGElement(null));
    }

    /**
     * Check that the width and height are written correctly whether or not units are specified.
     */
    @Test
    public void checkGetSVGElementWithSVGUnits() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100, SVGUnits.PX);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0px' height='100.0px'>" +
                "<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>", g2.getSVGElement(null));
    }

    /**
     * Check that a ViewBox is written correctly in the output.
     */
    @Test
    public void checkGetSVGElementWithViewBox() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        ViewBox viewBox = new ViewBox(30, 50, 100, 125);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0' viewBox='30.0 50.0 100.0 125.0'>" +
                "<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>", g2.getSVGElement(null, true, viewBox, null, null));
    }

    /**
     * Check that a ViewBox is written correctly in the output.
     */
    @Test
    public void checkGetSVGElementWithViewBoxAndPreserveAspectRatio() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        ViewBox viewBox = new ViewBox(30, 50, 100, 125);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0' viewBox='30.0 50.0 100.0 125.0' preserveAspectRatio='xMaxYMax'>" +
                "<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,0,255)'/></svg>", g2.getSVGElement(null, true, viewBox, PreserveAspectRatio.XMAX_YMAX, null));
    }

    /**
     * Check that a simple SVG document does not include a DEFS element if 
     * there is no gradient paint and no user clipping.
     */
    @Test
    public void checkDefsNotRequired() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle(10, 20, 30, 40));
        assertFalse(g2.getSVGElement().contains("<defs>"));
    }
    
    @Test
    public void checkDrawPath2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.draw(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }
    
    @Test
    public void checkDrawPath2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.draw(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g id='UNIQUE_ELEMENT_ID_1' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }
    
    @Test
    public void checkDrawPath2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.setTransform(AffineTransform.getTranslateInstance(2.0, 3.0));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.draw(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' transform='matrix(1.0,0.0,0.0,1.0,2.0,3.0)'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    /** 
     * Checks that the clip is correctly applied when drawing a Path2D.
     */
    @Test
    public void checkDrawPath2DWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("PRE");
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.clip(new Rectangle(10, 15, 20, 25));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.draw(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='PREclip-0'><path d='M10.0,15.0L30.0,15.0L30.0,40.0L10.0,40.0L10.0,15.0Z'/></clipPath></defs>" +
"<g style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' clip-path='url(#PREclip-0)'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillPath2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.fill(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g style='fill:rgb(0,0,255);stroke:none'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillPath2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.BLUE);
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.fill(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g id='UNIQUE_ELEMENT_ID_1' style='fill:rgb(0,0,255);stroke:none'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillPath2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.RED);
        g2.setTransform(AffineTransform.getScaleInstance(2.0, 3.0));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.fill(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g style='fill:rgb(255,0,0);stroke:none' transform='matrix(2.0,0.0,0.0,3.0,0.0,0.0)'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillPath2DWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEF");
        g2.setPaint(Color.RED);
        g2.clip(new Rectangle(10, 11, 12, 13));
        Path2D path = new Path2D.Double();
        path.moveTo(10.0, 20.0);
        path.lineTo(30.0, 40.0);
        g2.fill(path);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='DEFclip-0'><path d='M10.0,11.0L22.0,11.0L22.0,24.0L10.0,24.0L10.0,11.0Z'/></clipPath></defs>" +
"<g style='fill:rgb(255,0,0);stroke:none' clip-path='url(#DEFclip-0)'><path d='M10.0,20.0L30.0,40.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawLine2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Line2D line = new Line2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(line);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<line x1='10.0' y1='20.0' x2='30.0' y2='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0'/></svg>", g2.getSVGElement());
    }
    
    @Test
    public void checkDrawLine2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Line2D line = new Line2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.draw(line);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<line id='UNIQUE_ELEMENT_ID_1' x1='10.0' y1='20.0' x2='30.0' y2='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0'/></svg>", g2.getSVGElement());
    }
    
    @Test
    public void checkDrawLine2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.setTransform(AffineTransform.getScaleInstance(2.0, 3.0));
        Line2D line = new Line2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(line);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<line x1='10.0' y1='20.0' x2='30.0' y2='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0' transform='matrix(2.0,0.0,0.0,3.0,0.0,0.0)'/></svg>", g2.getSVGElement());
    }    

    @Test
    public void checkDrawLine2DWithTransformAndClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("PRE");
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.setTransform(AffineTransform.getScaleInstance(2.0, 3.0));
        g2.clip(new Rectangle(10, 11, 12, 13));
        Line2D line = new Line2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(line);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='PREclip-0'><path d='M10.0,11.0L22.0,11.0L22.0,24.0L10.0,24.0L10.0,11.0Z'/></clipPath></defs>" +
"<line x1='10.0' y1='20.0' x2='30.0' y2='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0' transform='matrix(2.0,0.0,0.0,3.0,0.0,0.0)' clip-path='url(#PREclip-0)'/></svg>", g2.getSVGElement());
    }    

    @Test
    public void checkDrawRectangle2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawRectangle2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.draw(rect);
        assertNull(g2.getRenderingHint(SVGHints.KEY_ELEMENT_ID)); // should be cleared after call
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect id='UNIQUE_ELEMENT_ID_1' x='10.0' y='20.0' width='30.0' height='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawRectangle2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.setTransform(AffineTransform.getScaleInstance(2.0, 3.0));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' transform='matrix(2.0,0.0,0.0,3.0,0.0,0.0)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawRectangle2DWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("PRE");
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.clip(new Rectangle(10, 15, 20, 25));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='PREclip-0'><path d='M10.0,15.0L30.0,15.0L30.0,40.0L10.0,40.0L10.0,15.0Z'/></clipPath></defs>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' clip-path='url(#PREclip-0)'/></svg>", g2.getSVGElement());
    }

    /**
     * Checks the output when drawing an Ellipse2D.
     */
    @Test
    public void checkDrawEllipse2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<ellipse cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawEllipse2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.draw(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<ellipse id='UNIQUE_ELEMENT_ID_1' cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawEllipse2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.setTransform(AffineTransform.getScaleInstance(2.0, 3.0));
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<ellipse cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' transform='matrix(2.0,0.0,0.0,3.0,0.0,0.0)'/></svg>", g2.getSVGElement());
    }

   @Test
    public void checkDrawEllipse2DWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEF");
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3.0f));
        g2.clip(new Rectangle(10, 15, 20, 25));        
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.draw(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='DEFclip-0'><path d='M10.0,15.0L30.0,15.0L30.0,40.0L10.0,40.0L10.0,15.0Z'/></clipPath></defs>" +
"<ellipse cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='stroke-width:2.0;stroke:rgb(0,0,0);stroke-opacity:1.0;stroke-linejoin:bevel;stroke-miterlimit:3.0;fill:none' clip-path='url(#DEFclip-0)'/></svg>", g2.getSVGElement());
    }

    /**
     * Checks the output when filling an Ellipse2D.
     */
    @Test
    public void checkFillEllipse2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.DARK_GRAY);
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><ellipse cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='fill:rgb(64,64,64)'/></svg>", g2.getSVGElement());
    }

    /**
     * Checks the output when filling an Ellipse2D and specifying
     * an element ID.
     */
    @Test
    public void checkFillEllipse2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.DARK_GRAY);
        Ellipse2D ellipse = new Ellipse2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.fill(ellipse);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><ellipse id='UNIQUE_ELEMENT_ID_1' cx='25.0' cy='40.0' rx='15.0' ry='20.0' style='fill:rgb(64,64,64)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillRectangle2D() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.GREEN);
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,255,0)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillRectangle2DWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.GREEN);
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect id='UNIQUE_ELEMENT_ID_1' x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,255,0)'/></svg>", g2.getSVGElement());
    }

    /**
     * Check the fill rectangle output with a non-default alpha value.
     */
    @Test
    public void checkFillRectangle2DWithAlpha() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.GREEN);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,255,0);fill-opacity:0.5'/></svg>", g2.getSVGElement());
    }

    /**
     * Check the fill rectangle output with a clip setting.
     */
    @Test
    public void checkFillRectangle2DWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEF");
        g2.setPaint(Color.GREEN);
        g2.clip(new Rectangle(10, 11, 12, 13));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<defs><clipPath id='DEFclip-0'><path d='M10.0,11.0L22.0,11.0L22.0,24.0L10.0,24.0L10.0,11.0Z'/></clipPath></defs>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,255,0)' clip-path='url(#DEFclip-0)'/></svg>", g2.getSVGElement());
    }

    /**
     * Check the fill rectangle output with a transform.
     */
    @Test
    public void checkFillRectangle2DWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setTransform(AffineTransform.getRotateInstance(Math.PI));
        g2.setPaint(Color.GREEN);
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:rgb(0,255,0)' transform='matrix(-1.0,0.0,-0.0,-1.0,0.0,0.0)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillRectangle2DWithGradientPaint() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEFS_PREFIX");
        g2.setPaint(new GradientPaint(1.0f, 2.0f, Color.RED, 3.0f, 4.0f, Color.GREEN));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><defs><linearGradient id='DEFS_PREFIXgp0' x1='1.0' y1='2.0' x2='3.0' y2='4.0' gradientUnits='userSpaceOnUse'><stop offset='0%' stop-color='rgb(255,0,0)'/><stop offset='100%' stop-color='rgb(0,255,0)'/></linearGradient></defs><rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:url(#DEFS_PREFIXgp0)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillRectangle2DWithLinearGradientPaint() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEFS_PREFIX");
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(50, 50);
        float[] dist = {0.0f, 0.2f, 1.0f};
        Color[] colors = {Color.RED, Color.WHITE, Color.BLUE};
        g2.setPaint(new LinearGradientPaint(start, end, dist, colors));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><defs><linearGradient id='DEFS_PREFIXlgp0' x1='0.0' y1='0.0' x2='50.0' y2='50.0' gradientUnits='userSpaceOnUse'><stop offset='0.0%' stop-color='rgb(255,0,0)'/><stop offset='20.0%' stop-color='rgb(255,255,255)'/><stop offset='100.0%' stop-color='rgb(0,0,255)'/></linearGradient></defs><rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:url(#DEFS_PREFIXlgp0)'/></svg>", g2.getSVGElement());
    }

    @Test
    public void checkFillRectangle2DWithRadialGradientPaint() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("DEFS_PREFIX");
        Point2D center = new Point2D.Float(50, 50);
        float radius = 25;
        float[] dist = {0.0f, 0.2f, 1.0f};
        Color[] colors = {Color.RED, Color.WHITE, Color.BLUE};
        g2.setPaint(new RadialGradientPaint(center, radius, dist, colors));
        Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        g2.fill(rect);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><defs><radialGradient id='DEFS_PREFIXrgp0' gradientUnits='userSpaceOnUse' cx='50.0' cy='50.0' r='25.0' fx='50.0' fy='50.0'><stop offset='0.0%' stop-color='rgb(255,0,0)'/><stop offset='20.0%' stop-color='rgb(255,255,255)'/><stop offset='100.0%' stop-color='rgb(0,0,255)'/></radialGradient></defs><rect x='10.0' y='20.0' width='30.0' height='40.0' style='fill:url(#DEFS_PREFIXrgp0)'/></svg>", g2.getSVGElement());
    }

    /**
     * Check the output for drawing a string.
     */
    @Test
    public void checkDrawString() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.GREEN);
        g2.drawString("ABC", 10, 20);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g><text x='10.0' y='20.0' style='fill: rgb(0,255,0); fill-opacity: 1.0; font-family: sans-serif; font-size: 12px;'>ABC</text></g></svg>", g2.getSVGElement());
    }    

    /** 
     * Check the output for drawing a string.
     */
    @Test
    public void checkDrawStringWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setPaint(Color.GREEN);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.drawString("ABC", 10, 20);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'>" +
"<g id='UNIQUE_ELEMENT_ID_1'><text x='10.0' y='20.0' style='fill: rgb(0,255,0); fill-opacity: 1.0; font-family: sans-serif; font-size: 12px;'>ABC</text></g></svg>", g2.getSVGElement());
    }

    private static Image createImage() {
        BufferedImage image = new BufferedImage(3, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.RED);
        g2.fillRect(0, 0, 3, 5);
        return image;
    }

    /**
     * Check the output for drawing an image.
     */
    @Test
    public void checkDrawImage() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.drawImage(createImage(), 10, 20, Color.YELLOW, null);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><g><rect x='10.0' y='20.0' width='3.0' height='5.0' style='fill:rgb(255,255,0)'/><image preserveAspectRatio='none' xlink:href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAAFCAIAAAAPE8H1AAAAEUlEQVR4XmP4z8AAQVCKIAsAhLsO8npVRuUAAAAASUVORK5CYII=' x='10.0' y='20.0' width='3.0' height='5.0'/></g></svg>", g2.getSVGElement());
    }

    /**
     * Check the output for drawing an image.
     */
    @Test
    public void checkDrawImageWithElementID() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setRenderingHint(SVGHints.KEY_ELEMENT_ID, "UNIQUE_ELEMENT_ID_1");
        g2.drawImage(createImage(), 10, 20, Color.YELLOW, null);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><g id='UNIQUE_ELEMENT_ID_1'><rect x='10.0' y='20.0' width='3.0' height='5.0' style='fill:rgb(255,255,0)'/><image preserveAspectRatio='none' xlink:href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAAFCAIAAAAPE8H1AAAAEUlEQVR4XmP4z8AAQVCKIAsAhLsO8npVRuUAAAAASUVORK5CYII=' x='10.0' y='20.0' width='3.0' height='5.0'/></g></svg>", g2.getSVGElement());
    }

    @Test
    public void checkDrawImageWithClip() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setDefsKeyPrefix("PRE");
        g2.clipRect(10, 20, 30, 40);
        g2.drawImage(createImage(), 10, 20, Color.YELLOW, null);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><defs><clipPath id='PREclip-0'><path d='M10.0,20.0L40.0,20.0L40.0,60.0L10.0,60.0L10.0,20.0Z'/></clipPath></defs><g><rect x='10.0' y='20.0' width='3.0' height='5.0' style='fill:rgb(255,255,0)' clip-path='url(#PREclip-0)'/><image preserveAspectRatio='none' xlink:href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAAFCAIAAAAPE8H1AAAAEUlEQVR4XmP4z8AAQVCKIAsAhLsO8npVRuUAAAAASUVORK5CYII=' clip-path='url(#PREclip-0)' x='10.0' y='20.0' width='3.0' height='5.0'/></g></svg>", g2.getSVGElement());
    }

    /**
     * Check the output for drawing an image with a transform applied.
     */
    @Test
    public void checkDrawImageWithTransform() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setTransform(AffineTransform.getTranslateInstance(11.1, 22.2));
        g2.drawImage(createImage(), 10, 20, Color.YELLOW, null);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><g><rect x='10.0' y='20.0' width='3.0' height='5.0' style='fill:rgb(255,255,0)' transform='matrix(1.0,0.0,0.0,1.0,11.1,22.2)'/><image preserveAspectRatio='none' xlink:href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAAFCAIAAAAPE8H1AAAAEUlEQVR4XmP4z8AAQVCKIAsAhLsO8npVRuUAAAAASUVORK5CYII=' transform='matrix(1.0,0.0,0.0,1.0,11.1,22.2)' x='10.0' y='20.0' width='3.0' height='5.0'/></g></svg>", g2.getSVGElement());
    }

    /**
     * Check the output for drawing an image.
     */
    @Test
    public void checkDrawImageWithReferenceHint() {
        SVGGraphics2D g2 = new SVGGraphics2D(200, 100);
        g2.setRenderingHint(SVGHints.KEY_IMAGE_HANDLING, SVGHints.VALUE_IMAGE_HANDLING_REFERENCE);
        g2.drawImage(createImage(), 10, 20, Color.YELLOW, null);
        assertEquals("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg' width='200.0' height='100.0'><g><rect x='10.0' y='20.0' width='3.0' height='5.0' style='fill:rgb(255,255,0)'/><image xlink:href='image-0.png' x='10.0' y='20.0' width='3.0' height='5.0'/></g></svg>", g2.getSVGElement());

    }

}
