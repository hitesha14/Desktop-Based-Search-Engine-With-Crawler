package crawlerlucene.google.crawler4j.chart;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.ReportConstants;
import crawlerlucene.google.crawler4j.multithreaded.db.HashDB;

/**
 * This class is used to generate the crawling statistics In every seconds it
 * displays through ui , what is the status of the ui page.
 * 
 * @author sumit
 * 
 */
public class ReportGenerationCrawlerStatus implements WorkerThread {

	private TimeSeriesChart timeSeriesChart;
	private HashDB hashDb;
	private boolean stop;

	private long max = 10;

	// ui components to add
	private JFrame frame;
	private JLabel maximumUrlFetched;
	private JLabel rateOfFetching;
	private JLabel dataSetSize;

	// constants

	private static final String MAX_URL_FETCH_PER_MIN_LBL = "Maximum Number of url Fetched So Far : ";
	private static final String RATE_URL_FETCH_PER_MIN_LBL = "Number of url fetching / (1000 Milli-seconds) : ";
	private static final String SIZE_OF_DATA_SETS_CRAWLED = "Data sets size gathered in Mega-bytes :  ";

	private ReportGenerationCrawlerStatus() {
		this.hashDb = null;
	}

	public ReportGenerationCrawlerStatus(String title, String xAxisLabel,
			String yAxisLabel, String chartTitle, HashDB hashDB) {
		this.timeSeriesChart = new TimeSeriesChart(title, xAxisLabel,
				yAxisLabel, chartTitle);
		this.hashDb = hashDB;
		prepareUI();

	}

	private void prepareUI() {
		this.frame = new JFrame();
		this.maximumUrlFetched = new JLabel();
		this.rateOfFetching = new JLabel();
		this.dataSetSize = new JLabel();
		//
		final JPanel contentPanel = new JPanel(new FlowLayout());
		contentPanel.add(new JLabel(" [ "));
		contentPanel.add(this.maximumUrlFetched);
		contentPanel.add(new JLabel(" || "));
		contentPanel.add(this.rateOfFetching);
		contentPanel.add(new JLabel(" || "));
		contentPanel.add(this.dataSetSize);
		contentPanel.add(new JLabel(" ] "));

		this.frame.add(contentPanel, BorderLayout.NORTH);

		this.frame.add(this.timeSeriesChart.getChartPanel(),
				BorderLayout.CENTER);

		this.frame.pack();
		this.frame.setAutoRequestFocus(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * This method is the thread main core method that allows , the statistical
	 * display of the crawling efficiency, through the # of url crawled, so far.
	 */
	@Override
	public void run() {

		this.timeSeriesChart.updateGraphWithNewYValue(0);
		// this.timeSeriesChart.startDisplay();
		this.frame.setVisible(true);
		long delta = this.max = this.hashDb.count();
		long highestDeltaSofar = 0;

		do {
			try {
				Thread.sleep(ReportConstants.FETCH_RATE_DB);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			delta = this.hashDb.count() - this.max;

			if (delta > highestDeltaSofar) {
				highestDeltaSofar = delta;
			}

			// (Math.round((double)delta*100/FETCH_RATE_DB))
			this.rateOfFetching.setText(RATE_URL_FETCH_PER_MIN_LBL + delta);
			this.maximumUrlFetched
					.setText(MAX_URL_FETCH_PER_MIN_LBL + this.max);
			this.dataSetSize.setText(SIZE_OF_DATA_SETS_CRAWLED
					+ Math.ceil((double) this.hashDb.sizeOfDatabase()
							/ (1024 * 1024)));
			this.timeSeriesChart.updateGraphWithNewYValue(delta);
			this.max = this.hashDb.count();

			// System.out.println("max="+this.max+", delat="+delta+" ,"+this.hashDb.sizeOfDatabase());
		} while (!stop);

	}

	/**
	 * This methods causes to shutdown the current thread, it allows external
	 * module, to kill this thread.
	 */
	@Override
	public void shutdown() {
		stop = true;
		Thread.currentThread().interrupt();
	}

}
