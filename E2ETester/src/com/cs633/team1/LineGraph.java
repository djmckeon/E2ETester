package com.cs633.team1;

import java.text.DecimalFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Builds a line graph of the test results
 * @author mckeon
 *
 */
@SuppressWarnings("serial")
public class LineGraph extends JFrame {

   private XYSeries dataSeries = new XYSeries("Response");
   
   /**
    * Constructor - define the graph layout
    * @param applicationTitle
    * @param chartTitle
    */
   public LineGraph( String applicationTitle , String chartTitle )
   {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createXYLineChart(
         chartTitle,  				// chart title
         "Test Number",  			// x axis label
         "Response Time (ms)",  	// y axis label
         createDataset(),  			// data
         PlotOrientation.VERTICAL,	// orientation
         false,  					// include legend
         false,  					// tooltips
         false);  					// urls
         
      // Add dots to the line chart
      XYPlot plot = lineChart.getXYPlot();
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
      plot.setRenderer(renderer);
      ((NumberAxis)plot.getDomainAxis()).setNumberFormatOverride(new DecimalFormat("0"));  // Display whole numbers on x axis

      // X-axis - only include numbers for the items that are being plotted.  Do not autofill the numbers in between.
      NumberAxis domain = (NumberAxis) plot.getDomainAxis();
      domain.setTickUnit(new NumberTickUnit(1));
      
      // Define the chart panel
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );      
      setContentPane( chartPanel );
   }

   /**
    * Add a set of values to the dataset (x-coordinate, y-coordinate)
    * @param xValue
    * @param yValue
    */
   public void addToDataset(int xValue, int yValue) {
	   dataSeries.add(xValue, yValue);
   }

   /**
    * Create the dataset to be graphed
    * @return XYDataset
    */
   private XYDataset createDataset( )
   {
       final XYSeriesCollection dataset = new XYSeriesCollection();
       dataset.addSeries(dataSeries);
       dataset.setAutoWidth(false);
       return dataset;
   }
	
}
