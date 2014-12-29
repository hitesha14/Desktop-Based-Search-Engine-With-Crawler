/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.index.util.IndexOperationUtil;
import crawlerlucene.google.crawler4j.multithreaded.util.FilterUrlHandler;

/**
 * This used to create index. on the flat file system.
 * 
 * @author sumit
 * 
 */
public class IndexCreator implements WorkerThread {

	private IndexWriter indxWriter;

	private BlockingQueue<String> listOfURLYetToProcess;

	private boolean stop;

	/**
	 * 
	 */
	public IndexCreator(IndexWriter pWriter,
			BlockingQueue<String> pListOfUrlToIndex) {

		// validation goes here

		this.indxWriter = pWriter;
		this.listOfURLYetToProcess = pListOfUrlToIndex;

	}

	/**
	 * 
	 * This is the starter of the thread method. It create.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		IndexOperationUtil indxCrtrUtil = IndexOperationUtil.getInstance();

		while (!stop) {
			try {
				String url = this.listOfURLYetToProcess.take();
				// shutdown needed
				if (stop && IndexConstants.SHUTDOWN_REQ.equalsIgnoreCase(url)) {
					break;
				}
				//validate url for some other format like image's, we are not indexing them
				if(FilterUrlHandler.verifyFilterForPattern(IndexConstants.IMAGE_URL_PATTERN, url)){
					continue;
				}
				// we have to index it , no validation here, but we can filter
				// url to validate

				// now prepare the document and write it to the indexer, all
				// based on analyzer
				Document doc = indxCrtrUtil.getDocument(url,
						this.indxWriter.getAnalyzer());
				if (doc != null) {
					//this.indxWriter.updateDocument(new Term(IndexConstants.), doc);
					this.indxWriter.addDocument(doc,
							this.indxWriter.getAnalyzer());
				}
				if (this.indxWriter.ramBytesUsed() > IndexConstants.MAX_SIZE_INDEX_WRITER_ALLOWED) {
					this.indxWriter.commit();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err
						.println("Interrupted exception in IndexCreater Thread:  "
								+ e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err
						.println("Interrupted exception in IndexCreater Thread:  "
								+ e.getMessage());
				// set global error field occurred
			}

		}// end of while

		System.out.println("Index Creator == thread-bye [name="
				+ Thread.currentThread().getName() + "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crawlerlucene.google.crawler4j.multithreaded.WorkerThread#shutdown()
	 */
	@Override
	public void shutdown() {
		stop = true;

		try {
			this.listOfURLYetToProcess.put(IndexConstants.SHUTDOWN_REQ);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		// Thread.currentThread().interrupt();
	}

}
