/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.util;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;

/**
 * @author sumit
 * 
 */
public class ReleaseMemoryThread implements WorkerThread {

	boolean stop = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!stop) {
			System.out.println();
			System.out.flush();
			System.err.println();
			System.err.flush();
			Runtime.getRuntime().gc();

			try {
				Thread.sleep(IndexConstants.DFLT_WAIT_FOR_HASHDB_TO_POPULATE_MILLI_SEC);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crawlerlucene.google.crawler4j.multithreaded.WorkerThread#shutdown()
	 */
	@Override
	public void shutdown() {
		stop = true;
		Thread.currentThread().interrupt();

	}

}
