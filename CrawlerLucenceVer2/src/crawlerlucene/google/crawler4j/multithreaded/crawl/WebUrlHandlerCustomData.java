/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import java.util.concurrent.BlockingQueue;

import crawlerlucene.google.crawler4j.multithreaded.util.FilterUrlHandler;

/**
 * This is used to maintain the data set up for the execution of the carwling
 * threads. like the instance of the filer criteria and all the list of visited
 * urls.
 * 
 * @author sumit
 */
public class WebUrlHandlerCustomData {

	FilterUrlHandler filterUrlHandler;
	BlockingQueue<String> listOfUrlVisited;

	public WebUrlHandlerCustomData(FilterUrlHandler filterLogic,
			BlockingQueue<String> glistOfUrlVisited) {
		if (filterLogic == null || glistOfUrlVisited == null) {
			throw new IllegalArgumentException(
					"Invaid parameter passed to method!!");
		}
		this.filterUrlHandler = filterLogic;
		this.listOfUrlVisited = glistOfUrlVisited;
	}
}
