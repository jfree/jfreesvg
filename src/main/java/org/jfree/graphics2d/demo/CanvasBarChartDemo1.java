/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.graphics2d.canvas.CanvasGraphics2D;
import static org.jfree.graphics2d.demo.CanvasPieChartDemo1.writeToHTML;
import org.jfree.ui.TextAnchor;

/**
 * A demo/test for a bar chart.
 */
public class CanvasBarChartDemo1 {
    
    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private static CategoryDataset createDataset() {
        DefaultStatisticalCategoryDataset dataset
            = new DefaultStatisticalCategoryDataset();
        dataset.add(10.0, 2.4, "Row 1", "Column 1");
        dataset.add(15.0, 4.4, "Row 1", "Column 2");
        dataset.add(13.0, 2.1, "Row 1", "Column 3");
        dataset.add(7.0, 1.3, "Row 1", "Column 4");
        dataset.add(22.0, 2.4, "Row 2", "Column 1");
        dataset.add(18.0, 4.4, "Row 2", "Column 2");
        dataset.add(28.0, 2.1, "Row 2", "Column 3");
        dataset.add(17.0, 1.3, "Row 2", "Column 4");
        return dataset;
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createLineChart(
            "Statistical Bar Chart Demo 1", // chart title
            "Type",                         // domain axis label
            "Value",                        // range axis label
            dataset,                        // data
            PlotOrientation.VERTICAL,       // orientation
            true,                           // include legend
            true,                           // tooltips
            false                           // urls
        );


        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(false);

        // customise the renderer...
        StatisticalBarRenderer renderer = new StatisticalBarRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setErrorIndicatorPaint(Color.black);
        renderer.setIncludeBaseInRange(false);
        plot.setRenderer(renderer);

        // ensure the current theme is applied to the renderer just added
        ChartUtilities.applyCurrentTheme(chart);

        renderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.yellow);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        return chart;
    }

    public static void writeToHTML(File f, String canvasScript) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>CanvasBarChartDemo1</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"); 
            writer.write("<script>\n");
            writer.write("window.onload = function() {\n");
            writer.write("var canvas = document.getElementById(\"canvas1\");\n");
            writer.write("var ctx = canvas.getContext(\"2d\");\n");
            writer.write("if (!ctx.setLineDash) {\n");
            writer.write("ctx.setLineDash = function() {};\n");
            writer.write("}\n");
            writer.write(canvasScript + "\n");
            writer.write("}\n");
            writer.write("</script>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");

            writer.write("<canvas id=\"canvas1\" width=\"600\" height=\"400\"></canvas>");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CanvasPieChartDemo1.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    public static void main(String[] args) throws IOException {
        JFreeChart chart = createChart(createDataset());
        CanvasGraphics2D g2 = new CanvasGraphics2D("id");
        Rectangle r = new Rectangle(0, 0, 600, 400);
        chart.draw(g2, r);
        File f = new File("CanvasBarChartDemo1.html");
        writeToHTML(f, g2.getScript());
    }
}
