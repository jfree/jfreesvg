/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.ui.RectangleInsets;

/**
 * A demo/test for a pie chart.
 */
public class SVGChartWithAnnotationsDemo1 {

    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset for the chart.
     *
     * @return A sample chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "XYDrawableAnnotationDemo1",
                null, "$ million", dataset,
                true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        DateAxis xAxis = (DateAxis) plot.getDomainAxis();
        xAxis.setLowerMargin(0.2);
        xAxis.setUpperMargin(0.2);
        xAxis.setStandardTickUnits(createStandardDateTickUnits());

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setLowerMargin(0.2);
        yAxis.setUpperMargin(0.2);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseLinesVisible(true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        renderer.setSeriesShape(1, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 5.0f, new float[] {10.0f, 5.0f}, 0.0f));
        renderer.setSeriesFillPaint(0, Color.white);
        renderer.setSeriesFillPaint(1, Color.white);
        renderer.setUseFillPaint(true);

        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setDefaultEntityRadius(6);

        renderer.addAnnotation(new XYDrawableAnnotation(
                new Month(4, 2005).getFirstMillisecond(), 600, 180, 100, 3.0,
                createPieChart()));
        renderer.addAnnotation(new XYDrawableAnnotation(
                new Month(9, 2007).getFirstMillisecond(), 1250, 120, 100, 2.0,
                createBarChart()));
        plot.setRenderer(renderer);
        return chart;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A dataset.
     */
    private static XYDataset createDataset() {
        TimeSeries series1 = new TimeSeries("Division A");
        series1.add(new Year(2005), 1520);
        series1.add(new Year(2006), 1132);
        series1.add(new Year(2007), 450);
        series1.add(new Year(2008), 620);
        TimeSeries series2 = new TimeSeries("Division B");
        series2.add(new Year(2005), 1200);
        series2.add(new Year(2006), 1300);
        series2.add(new Year(2007), 640);
        series2.add(new Year(2008), 520);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }

    private static JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Engineering", 43.2);
        dataset.setValue("Research", 13.2);
        dataset.setValue("Advertising", 20.9);
        PiePlot plot = new PiePlot(dataset);
        plot.setBackgroundPaint(null);
        plot.setOutlinePaint(null);
        plot.setBaseSectionOutlinePaint(Color.white);
        plot.setBaseSectionOutlineStroke(new BasicStroke(2.0f));
        plot.setLabelFont(new Font("Dialog", Font.PLAIN, 18));
        plot.setMaximumLabelWidth(0.25);
        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(null);
        chart.removeLegend();
        chart.setPadding(RectangleInsets.ZERO_INSETS);
        return chart;
    }

    private static JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(10.0, "R1", "Q1");
        dataset.addValue(7.0, "R1", "Q2");
        dataset.addValue(8.0, "R1", "Q3");
        dataset.addValue(4.0, "R1", "Q4");
        dataset.addValue(10.6, "R2", "Q1");
        dataset.addValue(6.1, "R2", "Q2");
        dataset.addValue(8.5, "R2", "Q3");
        dataset.addValue(4.3, "R2", "Q4");
        JFreeChart chart = ChartFactory.createBarChart("Sales 2008", null,
                null, dataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(null);
        chart.getPlot().setBackgroundPaint(new Color(200, 200, 255, 60));
        return chart;
    }


    private static TickUnitSource createStandardDateTickUnits() {
        TickUnits units = new TickUnits();
        DateFormat df = new SimpleDateFormat("yyyy");
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1,
                DateTickUnitType.YEAR, 1, df));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2,
                DateTickUnitType.YEAR, 1, df));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5,
                DateTickUnitType.YEAR, 5, df));
        return units;
    }

    public static void writeToHTML(File f, String svg) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>SVGChartWithAnnotationsDemo1</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"); 
            writer.write("</head>\n");
            writer.write("<body>\n");

            writer.write(svg + "\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SVGChartWithAnnotationsDemo1.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    public static void main(String[] args) throws IOException {
        JFreeChart chart = createChart(createDataset());
        SVGGraphics2D g2 = new SVGGraphics2D(500, 300);
        Rectangle r = new Rectangle(0, 0, 500, 300);
        chart.draw(g2, r);
        File f = new File("SVGChartWithAnnotationsDemo1.html");
        writeToHTML(f, g2.getSVG());
    }
}
