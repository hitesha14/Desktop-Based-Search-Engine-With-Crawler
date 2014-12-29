/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.util.concurrent.BlockingQueue;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;

/**
 * This class will read from HashDB some set of urls, seed these to web
 * controller
 * 
 * @author sumit
 */
public class CrawlWebReader implements WorkerThread {

	private boolean stop = false;
	private BlockingQueue<String> urlYetToCrawl;
	private CrawlerController carwlCntrl;
	private Class<? extends WebCrawler> webCrawlerClass;

	public <T extends WebCrawler> CrawlWebReader(
			BlockingQueue<String> pUrlReadSource,
			CrawlerController pCrawlController, Class<T> pWebCrawlerClass) {
		if (pUrlReadSource == null) {
			throw new IllegalArgumentException(
					"Invalid Url Data source provided");
		}
		if (pCrawlController == null) {
			throw new IllegalArgumentException(
					"Invalid Crawl Controller data set up");
		}
		if (pWebCrawlerClass == null) {
			throw new IllegalArgumentException("Invalid Crawl WebUrl Class");
		}
		this.carwlCntrl = pCrawlController;
		this.urlYetToCrawl = pUrlReadSource;
		this.webCrawlerClass = pWebCrawlerClass;
	}

	/**
	 * It fetches from the Hash-Db to crawl the data sets to be needed to crawl
	 * for further analysis , like crawling new page stes up or indexing the
	 * pages data at the url set ups.
	 */
	@Override
	public void run() {
		while (!stop) {

			// crawl one by one url and write it to the db-hased db
			int numberOfDataUrlsToread = this.carwlCntrl.getNumberOfThread();
			String url;
			int count = 0;
			while (numberOfDataUrlsToread-- > 0) {
				url = this.urlYetToCrawl.poll();
				if (url == null) {
					break;
				}
				// System.out.println("Url for crawling::" + url);
				this.carwlCntrl.addSeedData(url);
				count++;
			}
			if (count != 0) {
				this.carwlCntrl
						.startNonBlockCrawling(IndividualWebUrlHandler.class);
				this.carwlCntrl.waitUntilFinish();
				this.carwlCntrl.shutdown();
				this.carwlCntrl = getNewCrawlController(
						this.carwlCntrl.getCrawlConfig(), this.carwlCntrl);
			}
		}
	}

	private CrawlerController getNewCrawlController(CrawlConfig olDcrawlConfig,
			CrawlerController oldCarwlCntrl) {
		CrawlConfig carwlerConfig = new CrawlConfig();

		carwlerConfig.setCrawlStorageFolder(olDcrawlConfig
				.getCrawlStorageFolder());

		carwlerConfig.setIncludeBinaryContentInCrawling(true);
		carwlerConfig.setIncludeHttpsPages(true);
		carwlerConfig.setResumableCrawling(true);
		carwlerConfig.setUserAgentString(olDcrawlConfig.getUserAgentString());
		carwlerConfig.setFollowRedirects(true);

		CrawlerController crawlController;
		try {
			crawlController = new CrawlerController(carwlerConfig,
					oldCarwlCntrl.getNumberOfThread());
			crawlController.setCustomData(oldCarwlCntrl.getCustomData());
			return crawlController;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(
					"Cannot create the instance of the object!!");
		}

	}

	@Override
	public void shutdown() {
		this.stop = true;
		// this.carwlCntrl.shutdown();
		// Thread.currentThread().interrupt();
	}
}
