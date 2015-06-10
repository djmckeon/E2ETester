package com.cs633.team1;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Builds a bar graph of the test results
 * @author mckeon
 *
 */
@SuppressWarnings("serial")
public class BarGraph extends JFrame {

   private JFreeChart barGraph;
   
   /**
    * Constructor - build bar graph
    * @param applicationTitle
    * @param chartTitle
    * @param successCount
    * @param failedCount
    */
   public BarGraph( String applicationTitle, String chartTitle, int successCount, int failedCount)
   {
      super(applicationTitle);
      
      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      dataset.addValue(successCount, "Success", "Results");
      dataset.addValue(failedCount, "Failure", "Results");
      
      barGraph = ChartFactory.createBarChart3D(
         chartTitle,  				// chart title
         null,			  			// x axis label
         "Count",				  	// y axis label
         dataset,	 				// data
         PlotOrientation.VERTICAL,	// orientation
         true,		  				// include legend
         false,  					// tooltips
         false);  					// urls
         
      // Add dots to the line chart
      final CategoryPlot plot = barGraph.getCategoryPlot();
      final CategoryAxis axis = plot.getDomainAxis();
      BarRenderer3D renderer = new BarRenderer3D();
      plot.setRenderer(renderer);
      renderer.setDrawBarOutline(false);

      // Define the chart panel
      ChartPanel chartPanel = new ChartPanel( barGraph );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );      
      setContentPane( chartPanel );
   }
	
}
