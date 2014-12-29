/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * This class is required for maintaining the set up details and configuration
 * details required for crawling, like number of threads required for executing
 * the crawl, the number of urls that it can crawl and crawler config details.
 * 
 * @author sumit
 */
public class CrawlerController {

	private CrawlController crawlCntrlr;
	private int numberOfThread = IndexConstants.DFLT_NUMBER_OF_WRKR_THREAD;
	private CrawlConfig crawlConfig;

	public CrawlController getCrawlCntrlr() {
		return crawlCntrlr;
	}

	public void setCrawlCntrlr(CrawlController crawlCntrlr) {
		this.crawlCntrlr = crawlCntrlr;
	}

	public int getNumberOfThread() {
		return numberOfThread;
	}

	public void setNumberOfThread(int numberOfThread) {
		this.numberOfThread = numberOfThread;
	}

	public CrawlConfig getCrawlConfig() {
		return crawlConfig;
	}

	public void setCrawlConfig(CrawlConfig crawlConfig) {
		this.crawlConfig = crawlConfig;
	}

	public CrawlerController(CrawlConfig pConfig, int pNumberWorkerThread)
			throws Exception {

		PageFetcher pageFetcher = new PageFetcher(pConfig);
		RobotstxtConfig rbtsTConfig = new RobotstxtConfig();
		rbtsTConfig.setUserAgentName(pConfig.getUserAgentString());
		RobotstxtServer rbtsTxSrvr = new RobotstxtServer(rbtsTConfig,
				pageFetcher);
		this.crawlConfig = pConfig;

		this.numberOfThread = pNumberWorkerThread;
		if (pNumberWorkerThread > 0) {
			this.numberOfThread = pNumberWorkerThread;
		}
		crawlCntrlr = new CrawlController(pConfig, pageFetcher, rbtsTxSrvr);
	}

	public <T extends WebCrawler> void startNonBlockCrawling(Class<T> calss) {
		crawlCntrlr.startNonBlocking(calss, this.numberOfThread);

	}

	public void addSeedData(String pUrl) {
		this.crawlCntrlr.addSeed(pUrl);
	}

	public void shutdown() {
		this.crawlCntrlr.shutdown();

	}

	public void setCustomData(Object obj) {
		this.crawlCntrlr.setCustomData(obj);
	}

	public Object getCustomData() {
		return this.crawlCntrlr.getCustomData();
	}

	public void waitUntilFinish() {
		this.crawlCntrlr.waitUntilFinish();
	}
}
