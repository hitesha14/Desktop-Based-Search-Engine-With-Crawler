package crawlerlucene.google.crawler4j.chart;
/**
 * 
 */


import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.db.HashDB;

/**
 * @author sumit
 * 
 */
public class TestReportGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashDB globalDBInstance = HashDB.getConnection(
				IndexConstants.DFLT_HASH_DB_URL,
				IndexConstants.DFLT_READ_WRITE_MODE);

		// TODO Auto-generated method stub
		ReportGenerationCrawlerStatus rptDisplyr = new ReportGenerationCrawlerStatus(
				"Search Engine Algoritm Optimization", "# time",
				"# url crawled", "Crawler Data", globalDBInstance);
		Thread t = new Thread(rptDisplyr);

		t.start();
	}

}
