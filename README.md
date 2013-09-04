JFreeSVG
========

Version 1.1, 4 September 2013

(C)opyright 2013, by Object Refinery Limited.  All rights reserved.


Overview
--------
JFreeSVG is a graphics library for the Java(tm) platform that allows you to generate content in SVG and Javascript (HTML5 Canvas) format using the standard Java2D drawing API (Graphics2D).  JFreeSVG is light-weight, fast, and has no dependencies other than the Java runtime (1.6 or later).  The home page for the project is:

    http://www.jfree.org/jfreesvg/

Version 1.0 (then called JFreeGraphics2D) was released on 31 July 2013 and the most recent version (1.1) was released on 4 September 2013.  Changes since the initial release are listed towards the end of this file.


Getting Started
---------------
The Javadocs for the SVGGraphics2D and CanvasGraphics2D classes give examples for typical usage, and if you are already familiar with the Java2D APIs, then all you need to do is add jfreesvg-1.1.jar to your classpath and start coding.

Oracle provides tutorials for Java2D here:

       http://docs.oracle.com/javase/tutorial/2d/

There are some demonstration applications included in the org.jfree.graphics2d.demo.* package.  These applications make use of JFreeChart, so you'll find two additional jars in the 'lib' directory (jfreechart-1.0.15.jar and jcommon-1.0.20.jar).  These are required for the demo code only.


License
-------
JFreeSVG is free software under the terms of the GNU Affero General Public License version 3 (AGPLv3) or later.  The license file is included in this distribution (agpl-3.0.txt).  If you prefer not to be bound by the terms of the AGPLv3, you can purchase an alternative license from Object Refinery Limited (please e-mail info@object-refinery.com for details, or check the JFreeSVG home page).

Please note that JFreeSVG is distributed WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  Please refer to the license for details.

Other code distributed with JFreeSVG:

- the JFreeChart and JCommon libraries (required for the demos only) are licensed under the GNU Lesser General Public License (GNU LGPL), a copy of this license can be found in the lib folder (the LGPL is not the same as the AGPL).  To get the source code and other information about JFreeChart and JCommon, please visit http://www.jfree.org/jfreechart/ 


Change History
--------------

Version 1.1 (3 September 2013)
- reimplemented drawString(AttributedCharacterIterator, float, float) using TextLayout and modified drawGlyphVector() to fill rather than stroke shapes (for SVG/CanvasGraphics2D);
- removed the PDF code (to a separate project, OrsonPDF);
- fixed clipping bug in SVGGraphics2D;
- added geomDP and transformDP attributes to CanvasGraphics2D to control number of decimal places for numbers written to script; 

Version 1.0 (31 July 2013)
- Initial public release.


Contact / Questions
-------------------
If you have any questions or feedback about JFreeSVG, please send an e-mail to david.gilbert@object-refinery.com.
