/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.util.FilterUrlHandler;

/**
 * This class is basically required or deals with the filtering of the url and
 * other data sets like tracking the web pages extracted anchor or link tags and
 * feeding them to the other users data sets.
 * 
 * @author sumit
 */
public class IndividualWebUrlHandler extends WebCrawler {

	WebUrlHandlerCustomData customDataHolder;

	@Override
	public void onStart() {
		customDataHolder = (WebUrlHandlerCustomData) getMyController()
				.getCustomData();
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		// super.shouldVisit(page, webUrl)
		// System.out.println("href::"+url.getPath());
		return FilterUrlHandler.verifyFilterForPattern(
				IndexConstants.PATTREN_URL_EXT_ALOWED, url.getPath())
				&& customDataHolder.filterUrlHandler.verifyFilter(url.getURL()
						.toLowerCase());
	}

	@Override
	public void visit(Page page) {
		ParseData parseData = page.getParseData();
		// System.out.println("Master Url : "+page.getWebURL().getURL()+" "+page.getWebURL().getPath()+" "+page.getWebURL().getAnchor());

		// we can add logic for validate content of the page , that is
		// irrelavant like illegal sites, that shuld not be crawled
		if (parseData instanceof HtmlParseData) {
			List<WebURL> outgoingUrls = ((HtmlParseData) parseData)
					.getOutgoingUrls();
			// System.out.println("All out going url::"+outgoingUrls);
			for (WebURL webURL : outgoingUrls) {
				try {
					this.customDataHolder.listOfUrlVisited.put(webURL.getURL());
					// write all these data to db also
				} catch (InterruptedException ex) {
					Logger.getLogger(IndividualWebUrlHandler.class.getName())
							.log(Level.SEVERE, null, ex);
					// check whether that url is added or not
					System.err.println("Error occured while adding url :"
							+ webURL.getURL());
				}
			}
		} else if (parseData instanceof TextParseData) {
			System.out.println("TextParseData parsing data["
					+ page.getContentType() + "] : "
					+ page.getWebURL().getURL() + "data :[\n"
					+ ((TextParseData) parseData).getTextContent() + "\n]");
		} else if (parseData instanceof ExtractedUrlAnchorPair) {
			System.out.println("ExtractedUrlAnchorPair parsing data["
					+ page.getContentType() + "] : "
					+ page.getWebURL().getURL() + "data :[\n"
					+ ((ExtractedUrlAnchorPair) parseData).getAnchor() + "\n]");
		} else if (parseData instanceof BinaryParseData) {
			// System.out.println("BinaryParseData parsing data[" + page.
			// getContentType() + "] : " + page.
			// getWebURL().getURL() + "data :[\n" + ((BinaryParseData)
			// parseData).
			// toString() + "\n]");
		} else {
			System.err.println("Unknow parsing data [IndividualWebHandler]: "
					+ parseData + "data :[\n" + parseData.toString()
					+ "] parsing data failing[" + page.getContentType()
					+ "] : " + page.getWebURL().getURL() + "\n]");
		}
	}
}
