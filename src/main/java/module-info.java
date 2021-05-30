/**
 * JFreeSVG is a Java module that enables the generation of Scalable Vector Graphics (SVG) output via the
 * standard Java2D API.  For example, the following chart is created with JFreeChart and rendered with JFreeSVG:
 * <br><br>
 * <img src="../doc-files/FlowPlotDemo2.svg" alt="FlowPlotDemo2.svg">
 * <br><br>
 * The project is hosted at GitHub: <a href="https://github.com/jfree/jfreesvg">https://github.com/jfree/jfreesvg</a>.
 * @see <a href="https://jfree.github.io/jfreechart-and-opencsv/">https://jfree.github.io/jfreechart-and-opencsv/</a>
 */
module org.jfree.svg {
    requires java.desktop;
    requires java.logging;
    exports org.jfree.svg;
}
