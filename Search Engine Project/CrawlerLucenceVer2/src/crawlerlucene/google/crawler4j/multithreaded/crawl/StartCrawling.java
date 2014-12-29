/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import crawlerlucene.google.crawler4j.chart.ReportGenerationCrawlerStatus;
import crawlerlucene.google.crawler4j.multithreaded.api.SimpleThreadPool;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.constant.ReportConstants;
import crawlerlucene.google.crawler4j.multithreaded.db.HashDB;
import crawlerlucene.google.crawler4j.multithreaded.db.ReadURLFromHashDB;
import crawlerlucene.google.crawler4j.multithreaded.db.WriteUrlToHashDB;
import crawlerlucene.google.crawler4j.multithreaded.util.FilterUrlHandler;
import crawlerlucene.google.crawler4j.multithreaded.util.ReleaseMemoryThread;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;

/**
 * This is the main class required for starting the crawl operations. It control
 * and set up all data structure required for creating the crawling process and
 * maintaining and calling other threads for execution, like reporting thread
 * and other threads crawler threads, reader and writer threads to the data base.
 * 
 * @author sumit
 */
public class StartCrawling {

	public static void main(String[] args) throws Exception {

		// setting system.out and system.err to a file
		setSystemOut(IndexConstants.NAME_OF_DEBUG_LOG_FILE_TO_OUT_PUT,
				IndexConstants.NAME_OF_ERROR_LOG_FILE_TO_OUT_PUT);
		// 1.get thread pool
		// 2.get the initial seed urls to begin crawling
		// 3.global collection for url fetched so far, for dumping to the Hash
		// Db
		// 4.WriterDB -> with n threads, it will write from above collection
		// 5.global collection to read url from hash db for crawling
		// 6.ReadDB -> will poplualte this above global collection
		// 7.create the crawler config, may be one for all crawler
		// 8.create n-threads to start crawling, maintain in a global structure,
		// pass it to the thread pool (1).
		// 9.

		int numberOfCrawlers = IndexConstants.DFLT_NUMBER_OF_WRKR_THREAD;
		SimpleThreadPool threadPool = new SimpleThreadPool(
				IndexConstants.DFLT_THREAD_MAX_POOL_SIZE + 2);// number of
																// reader writer
																// threads

		// variables shared accross threads
		BlockingQueue<String> globalUrlFetchedSoFarByCrawling = new LinkedBlockingQueue<>(
				1000);
		BlockingQueue<String> globalUrlFetchedFromDB = new LinkedBlockingQueue<>(
				1000);
		// setting crawer config

		// Thread-1 n threads
		// Thread Pool's worker threads data structure
		CrawlWebReader[] globalWorkerThreadsForCrawling = new CrawlWebReader[numberOfCrawlers];

		HashDB globalDBInstance = HashDB.getConnection(
				IndexConstants.DFLT_HASH_DB_URL,
				IndexConstants.DFLT_READ_WRITE_MODE);

		// iterate through initial url data seed and add them to HashDB
		for (String url : IndexConstants.INITIAL_URL_DATA_SEED) {
			globalDBInstance.addItem(url.hashCode() + "", url);
			globalUrlFetchedFromDB.put(url);
			System.out.println(globalDBInstance.getLastDBErrorOccured());
		}

		// Thread - 2
		// populate read and write global url structure
		ReadURLFromHashDB readrFromDbAllUrl = new ReadURLFromHashDB(
				globalUrlFetchedFromDB, globalDBInstance);

		// Thread-3
		WriteUrlToHashDB writerURLtoHashDB = new WriteUrlToHashDB(
				globalUrlFetchedSoFarByCrawling, globalDBInstance);
		// custom data shared by different crawler,take list that will hold all
		// url fetched so far, ready to write to db
		WebUrlHandlerCustomData customDataWebURL = new WebUrlHandlerCustomData(
				new FilterUrlHandler(IndexConstants.PATTREN_VALID_URL_FORMAT),
				globalUrlFetchedSoFarByCrawling);
		for (int i = 0; i < numberOfCrawlers; i++) {
			CrawlConfig carwlerConfig = new CrawlConfig();

			carwlerConfig
					.setCrawlStorageFolder(IndexConstants.ROOT_DIR_FOR_CRAWLING
							+ i);

			carwlerConfig.setIncludeBinaryContentInCrawling(true);
			carwlerConfig.setIncludeHttpsPages(true);
			carwlerConfig.setResumableCrawling(true);
			carwlerConfig.setUserAgentString("sumit.dey.com");
			carwlerConfig.setFollowRedirects(true);

			CrawlerController carwlController = new CrawlerController(
					carwlerConfig, IndexConstants.NUMBER_OF_CRAWLER_THREAD);
			carwlController.setCustomData(customDataWebURL);
			globalWorkerThreadsForCrawling[i] = new CrawlWebReader(
					globalUrlFetchedFromDB, carwlController,
					IndividualWebUrlHandler.class);

		}

		// add all threads to the executer
		threadPool.execute(readrFromDbAllUrl);

		for (int i = 0; i < numberOfCrawlers; i++) {
			threadPool.execute(globalWorkerThreadsForCrawling[i]);
		}

		threadPool.execute(writerURLtoHashDB);

		// additional threads if any
		// attach relase memory thread
		threadPool.execute(new ReleaseMemoryThread());
		threadPool.execute(new CrawlHashDBStatusUpdater(globalDBInstance,
				IndexConstants.LOCATION_OF_REPORT_OF_FILE));

		// one more thread to read from a file for seed crawling of data

		// reporting thread
		ReportGenerationCrawlerStatus rptDisplyr = new ReportGenerationCrawlerStatus(
				"Search Engine Algoritm Optimization", "# time",
				"# url crawled / [" + ReportConstants.FETCH_RATE_DB
						+ "Milli-seconds ]",
				"Crawler Data Gathering - Optimization Chart", globalDBInstance);

		threadPool.execute(rptDisplyr);

		// logic for other threads to die and waiting logic and clean up code,
		// needs to modify
		try {
			Thread.currentThread().join(Long.MAX_VALUE);
		} catch (InterruptedException ex) {
			Logger.getLogger(StartCrawling.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		// this will force crawler to shutdown
		for (int i = 0; i < numberOfCrawlers; i++) {
			globalWorkerThreadsForCrawling[i].shutdown();
		}
		threadPool.immediateShutdown();
		globalDBInstance.closeConnection();
	}

	private static void setSystemOut(String nameOfLogFileToOutPut,
			String nameOfErrorLogFileToOutPut) throws FileNotFoundException {
		System.setOut(new PrintStream(new FileOutputStream(
				nameOfLogFileToOutPut), true));
		System.setErr(new PrintStream(new FileOutputStream(
				nameOfErrorLogFileToOutPut), true));
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		System.out.flush();
		System.err.flush();
		System.out.close();
		System.err.close();
		super.finalize();

	}
}
