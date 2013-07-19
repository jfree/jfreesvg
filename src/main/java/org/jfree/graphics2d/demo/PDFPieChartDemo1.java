/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.graphics2d.pdf.PDFDocument;
import org.jfree.graphics2d.pdf.PDFGraphics2D;
import org.jfree.graphics2d.pdf.Page;

/**
 * A demo/test for a pie chart.
 */
public class PDFPieChartDemo1 {
    
    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", new Double(43.2));
        dataset.setValue("Two", new Double(10.0));
        dataset.setValue("Three", new Double(27.5));
        dataset.setValue("Four", new Double(17.5));
        dataset.setValue("Five", new Double(11.0));
        dataset.setValue("Six", new Double(19.4));
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

    public static void main(String[] args) throws IOException {
        JFreeChart chart = createChart(createDataset());
        PDFDocument pdfDoc = new PDFDocument();
        Page page = pdfDoc.createPage(new Rectangle(612, 468));
        PDFGraphics2D g2 = page.getGraphics2D();
        chart.draw(g2, new Rectangle(0, 0, 612, 468));
        File f = new File("PDFPieChartDemo1.pdf");
        pdfDoc.writeToFile(f);
    }
}
