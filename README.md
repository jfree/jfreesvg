JFreeSVG
========

Version 3.4.3, 11 June 2022.

(C)opyright 2013-present, by David Gilbert.  All rights reserved.

[![Maven Central](https://img.shields.io/maven-central/v/org.jfree/jfreesvg)](https://central.sonatype.com/artifact/org.jfree/jfreesvg/versions) [![javadoc](https://javadoc.io/badge2/org.jfree/jfreesvg/javadoc.svg)](https://javadoc.io/doc/org.jfree/jfreesvg)

Overview
--------
**JFreeSVG** is a graphics library for the Java(tm) platform that allows you to generate content in SVG format using the standard Java2D drawing API (`Graphics2D`).  JFreeSVG is light-weight, fast, and has no dependencies other than the Java runtime (1.8 or later).  

![Sample from JFreeChart](https://www.jfree.org/jfreesvg/javadoc/doc-files/SVGTimeSeriesChartDemo1.svg)

The home page for the project is:

https://www.jfree.org/jfreesvg/


Getting Started
---------------
The Javadocs for the `SVGGraphics2D` class gives examples for typical usage, and if you are already familiar with the Java2D APIs, then all you need to do is add the JFreeSVG dependency and start coding.

Oracle provides tutorials for Java2D here:

http://docs.oracle.com/javase/tutorial/2d/

There are some demonstration applications in the [JFree-Demos](https://github.com/jfree/jfree-demos) project at GitHub.


Include
-------
JFreeSVG is published to the Central Repository.  You can include it in your projects with the following dependency:

    <dependency>
        <groupId>org.jfree</groupId>
        <artifactId>jfreesvg</artifactId>
        <version>3.4.3</version>
    </dependency>

This is a maintenance version of the library for developers that still require Java 8 support.  If you are using Java 11 or later, then you should use the modular version of JFreeSVG (which has a different artifact ID and version number):

    <dependency>
        <groupId>org.jfree</groupId>
        <artifactId>org.jfree.svg</artifactId>
        <version>5.0.7</version>
    </dependency>

Build
-----
You can build `JFreeSVG` from sources using Maven:

    mvn clean verify

To generate the Javadocs:

    mvn clean compile javadoc:javadoc

License
-------
JFreeSVG is free software under the terms of the GNU General Public License version 3 (GPLv3) or later.  The license file is included in this distribution (gpl-3.0.txt).

    Please note that JFreeSVG is distributed WITHOUT ANY WARRANTY; without even 
    the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
    Please refer to the license for details.

If you prefer not to be bound by the terms of the GPLv3, you can obtain an alternative license by sponsoring the JFree project:

- https://github.com/sponsors/jfree

Change History
--------------

Version 3.4.3 (11 Jun 2022)
- replace double-quotes in SVG output with single quotes
- fix double semi-colon issue ([#52](https://github.com/jfree/jfreesvg/issues/52))
- ensure image based tests run correctly on JDK 8 and JDK 17

Version 3.4.2 (7 Nov 2021)
- add support for `TextAttribute.TRACKING`

Version 3.4.1 (1 May 2021)
- fix miter limit ([#36](https://github.com/jfree/jfreesvg/issues/36) and [#38](https://github.com/jfree/jfreesvg/issues/38));
- add winding rule to path data if required ([#25](https://github.com/jfree/jfreesvg/issues/25));
- fix `drawArc()` and `fillArc()` methods to draw pie arcs;
- correction of fix for encoding of images ([#21](https://github.com/jfree/jfreesvg/issues/21)).
- use `java.util.Base64` instead of `javax.xml.bind.DatatypeConverter`.

Version 3.4 (10 February 2019)
- added attribute for font size units override;
- added automatic module name (`org.jfree.jfreesvg`);
- omit transformation if it is the identity transform;
- fixed `drawString()` method for empty string;
- fixed `drawImage()` method for `null` `ImageOp`.

Version 3.3 (7 November 2017)
- the default `DEFS` key prefix should not begin with a number;
- transferred demo code to the [JFree-Demos](https://github.com/jfree/jfree-demos) project to streamline the distribution process.

Version 3.2 (9 October 2016)
- added facility to specify `units` for `width` and `height` attributes;
- added option to suppress `width` and `height` from SVG element;
- add option to include `viewBox` and associated parameters in the SVG element;
- fixed `NullPointerException` in `drawImage(Image, AffineTransform, ImageObserver)` (bug #6);
- copy `fontMapper` in `create()` method (bug #5).


Version 3.1 (30 April 2016)
- use fractional metrics for `FontMetrics`;
- use `ellipse` element to draw/fill `Ellipse2D` instances;
- handle alpha in gradient paints.


Version 3.0 (8 June 2015)
- added handling for `BasicStroke` cap, join and miterlimit;
- added ZIP option when writing SVG to files;
- added demo for exporting Swing UIs to SVG;
- removed `CanvasGraphics2D` implementation;
- fixed handling of `PathIterator.SEG_CLOSE`;
- fixed y-coordinate bug in `drawImage()`;
- added workaround for `ClassCastException` when exporting Swing UIs on MacOSX with Nimbus L&F.


Version 2.1 (4 August 2014)
- fixed bug with element end tag for JFreeChart;
- fixed compilation error in demos.


Version 2.0 (30 July 2014)
- added configurable text-rendering and shape-rendering properties to the SVG element;
- observe `KEY_STROKE_CONTROL` rendering hints;
- fixed `create()` method so that Swing components can be rendered correctly;
- modified the font render context info to fix glyph positioning for text drawn as vector graphics;
- write colors using `rgb()` rather than `rgba()`, and write the alpha value to separate opacity attribute; 
- changed the license from AGPLv3 to GPLv3.


Version 1.9 (6 May 2014) 
- added `defsKeyPrefix` attribute to allow unique ids for DEFS when generating multiple SVG elements for use in a single HTML page;
- added support for `LinearGradientPaint` and improved existing `GradientPaint` handling;
- added `KEY_ELEMENT_TITLE` rendering hint;
- added support for arbitrary key, value pairs in the `KEY_START_GROUP` handler;
- added `zeroStrokeWidth` attribute to allow configuration of handling for `BasicStroke` with zero width (which the Java specification states should be "rendered as the thinnest possible line");
- fixed a bug in the `drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)` method.


Version 1.8 (11 April 2014)
- added additional `KEY_BEGIN_GROUP` options, plus special integration support for Orson Charts;
- added special handling for shape drawing when the `Stroke` is not an instance of `BasicStroke`;
- explicitly set encoding to UTF-8 for `SVGUtils.writeToSVG()` and `SVGUtils.writeToHTML()`.


Version 1.7 (25 February 2014)
- added `SVGHints.KEY_BEGIN_GROUP` and `SVGHints.KEY_END_GROUP` to allow grouping of SVG output;
- fix `drawString()` to include id if `SVGHints.KEY_ELEMENT_ID` is set;
- fix transparent colors issue;
- fixed minor issue with meta tag in HTML output file.


Version 1.6 (18 December 2013)
- fixed a minor packaging error in version 1.5.


Version 1.5 (18 December 2013)
- added `FontMapper` to ensure that Java logical font names map to the equivalent SVG generic font names;
- added `SVGHints.KEY_IMAGE_HREF` to allow image references to be specified;
- added `SVGHints.KEY_ELEMENT_ID` to allow an element id to be supplied for the next element to be written;
- fixed a bug for special characters in `drawString()`;
- fixed a bug with coordinate formatting in certain locales.


Version 1.4 (24 October 2013)
- `getSVGFontStyle()` now specifies font-size units as required by the SVG standard. Fixes a bug that is visible when the SVG output is rendered in FireFox (which is more strict about the standard than other browsers);
- added text-rendering attribute to the SVG text element generated by the `drawString()` method;
- added new key and values to `SVGHint` to configure the value of the text-rendering attribute.


Version 1.3 (24 September 2013)
- implemented `getDeviceConfiguration()` method;
- fixed clipping bug with transforms applied after clip is set;
- fixed bug with `setClip(null)`;
- fixed "not well-formed" parsing issue for Firefox;
- set `preserveAspectRatio` attribute for images;


Version 1.2 (13 September 2013)
- added SVG and XLINK namespaces to SVG element;
- fixed capitalisation of `linearGradient` element, to pass W3C validator;
- added support for Java2D's `RadialGradientPaint`;
- added `SVGUtils.writeToSVG()` method; 
- added new demo `SVGTimeSeriesChartDemo1.java`.


Version 1.1 (4 September 2013)
- reimplemented `drawString(AttributedCharacterIterator, float, float)` using `TextLayout` and modified `drawGlyphVector()` to fill rather than stroke shapes (for `SVG/CanvasGraphics2D`);
- removed the PDF code (to a separate project, **OrsonPDF**);
- fixed clipping bug in `SVGGraphics2D`;
- added `geomDP` and `transformDP` attributes to `CanvasGraphics2D` to control number of decimal places for numbers written to script; 


Version 1.0 (31 July 2013)
- Initial public release.
