package crawlerlucene.google.crawler4j.chart;


import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import crawlerlucene.google.crawler4j.multithreaded.constant.ReportConstants;

public class TimeSeriesChart extends ApplicationFrame {

	private static final int N = 600;
	private static final Random random = new Random();
	private static final Shape circle_shape = new Ellipse2D.Double(-3, -3, 6, 6);
	private static final Color line_color = Color.RED;

	// time series data
	public TimeSeries timeSeries;
	private String title;

	// most recent value added
	private double lastValue = 100;

	private ChartPanel chartPanel;

	// my stylistic rendrer
	private static class MyRenderer extends XYLineAndShapeRenderer {

		private List<Color> clut;

		public MyRenderer(boolean lines, boolean shapes, int n) {
			super(lines, shapes);
			clut = new ArrayList<Color>(n);
			for (int i = 0; i < n; i++) {
				clut.add(Color.getHSBColor((float) i / n, 1, 1));
			}
		}

		@Override
		public Paint getItemFillPaint(int row, int column) {
			return clut.get((int) (column + 7 * Math.random()) % N);
		}
	}

	// "Elapsed Time (secs)", "Response Time (secs)",
	public TimeSeriesChart(String title, String xAxisLabel, String yAxisLabel,
			String chartTitle) {
		super(title);
		this.chartPanel = createPanel(xAxisLabel, yAxisLabel, chartTitle);
		this.title = title;
		// setContentPane(createPanel(xAxisLabel, yAxisLabel, chartTitle));
	}

	private ChartPanel createPanel(String xAxisLabel, String yAxisLabel,
			String chartTitle) {

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle,
				xAxisLabel, yAxisLabel, createDataset(xAxisLabel, yAxisLabel),
				true, true, false);

		// XY-PLOT Customization
		XYPlot plot = chart.getXYPlot();
		plot.setDomainAxis(new DateAxis(xAxisLabel));
		// x-axis
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(ReportConstants.TIMER_SERIES_CHART_DOMAIN_MILLISEONDS); // 60 seconds
		// y-axis
		axis = plot.getRangeAxis();
		axis.setRange(ReportConstants.DFLT_Y_AXIS_RANGE);
		axis.setAutoRange(true);

		// Rendered Creation
		MyRenderer renderer = new MyRenderer(true, true, N);

		// setting plotting rendered
		plot.setRenderer(renderer);

		renderer.setSeriesShape(0, circle_shape);
		renderer.setSeriesPaint(0, line_color);
		renderer.setUseFillPaint(true);
		renderer.setSeriesShapesFilled(0, true);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setUseOutlinePaint(true);
		renderer.setSeriesOutlinePaint(0, line_color);

		return new ChartPanel(chart);
	}

	private TimeSeriesCollection createDataset(String xAxisLabel,
			String yAxisLabel) {

		this.timeSeries = new TimeSeries("Crawler Data");
		final TimeSeriesCollection dataset = new TimeSeriesCollection(
				this.timeSeries);
		return dataset;
	}

	private double getRandomData(double x) {
		double y = 0.004 * x + .75;
		return y + random.nextGaussian() * y / 10;
	}

	//add it to frame and then display
	public void display(String title, ChartPanel chartPanel) {
		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(chartPanel);
		// f.add(this);
		f.pack();
		RefineryUtilities.centerFrameOnScreen(f);
		// f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public void updateGraphWithNewYValue(double value) {
		this.lastValue = value;
		this.timeSeries.addOrUpdate(new Millisecond(), this.lastValue);
	}

	//display the farme
	public void startDisplay() {
		this.setVisible(true);
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
	}

	public ChartPanel getChartPanel(){
		return this.chartPanel;
	}
	// testing method
	public static final void main(String[] args) throws InterruptedException {
		TimeSeriesChart timeSeriesChart = new TimeSeriesChart(
				"Search Engine Algoritm Optimization", "# time",
				"# url crawled", "Crawler Data");
		timeSeriesChart.display(timeSeriesChart.title,
				timeSeriesChart.chartPanel);

		// timeSeriesChart.setVisible(true);
		// timeSeriesChart.pack();
		// RefineryUtilities.centerFrameOnScreen(timeSeriesChart);

		double i = 2;
		boolean forward = true;
		int upperHigh = 1024;
		int lowerHigh = 2;
		while (i > lowerHigh || i < upperHigh) {
			final Millisecond now = new Millisecond();
			System.out.println("Now = " + now.toString());
			timeSeriesChart.lastValue = timeSeriesChart.lastValue
					* (0.90 + (0.2 * (timeSeriesChart.lastValue + Math.random()) / timeSeriesChart.lastValue)
							* Math.random());
			// timeSeriesChart.timeSeries.addOrUpdate(new Millisecond(),
			// timeSeriesChart.lastValue);
			if (i * 2 < upperHigh && forward) {
				i = i * 1.5 + Math.random();
			} else {
				forward = false;
			}

			if (i / 2 > 2 && !forward) {
				i = i / 1.5 + Math.random();
			} else {
				forward = true;
			}
			timeSeriesChart.updateGraphWithNewYValue(i);
			Thread.sleep(45);
		}

	}

}
