/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.graphics2d.canvas.CanvasGraphics2D;

/**
 * A demo/test for a pie chart.
 */
public class CanvasPieChartDemo1 {
    
    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", Double.valueOf(43.2));
        dataset.setValue("Two", Double.valueOf(10.0));
        dataset.setValue("Three", Double.valueOf(27.5));
        dataset.setValue("Four", Double.valueOf(17.5));
        dataset.setValue("Five", Double.valueOf(11.0));
        dataset.setValue("Six", Double.valueOf(19.4));
        return dataset;
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return A chart.
     */
    private static JFreeChart createChart(PieDataset dataset) {

        JFreeChart chart = ChartFactory.createPieChart(
            "Pie Chart Demo 1",  // chart title
            dataset,             // data
            true,                // include legend
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setNoDataMessage("No data available");
        return chart;

    }

    public static void writeToHTML(File f, String canvasScript) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>CanvasPieChartDemo1</title>\n");
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
        File f = new File("CanvasPieChartDemo1.html");
        writeToHTML(f, g2.getScript());
    }
}
