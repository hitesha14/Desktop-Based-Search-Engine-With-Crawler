/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.crawl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.db.HashDB;

/**
 * This is the actual file system based reporting class, it will track how many
 * url have been recorded yet to crawl during the process of crawling. It
 * fetches the latest updates fetched so far to crawl.
 * 
 * @author sumit
 * 
 */
public class CrawlHashDBStatusUpdater implements WorkerThread {

	private HashDB hashDb;
	private File file;
	private boolean stop;
	private long count;
	private Writer bfrdWriter;

	/**
	 * 
	 */
	public CrawlHashDBStatusUpdater(HashDB hashDb, File file) {
		this.hashDb = hashDb;
		this.file = file;
		init();
	}

	public CrawlHashDBStatusUpdater(HashDB hashDb, String pFileName) {
		this.hashDb = hashDb;
		this.file = new File(pFileName);
		init();
	}

	private void init() {
		try {
			this.bfrdWriter = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException("Problem in opening file!", e);
		}

	}

	/**
	 * This method is the startup for the execution of tracking status update of
	 * the crawling data sets.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long tempCount = 0;
		long printRow = 1;
		boolean ioError = false;
		while (!stop) {
			tempCount = this.hashDb.count();
			try {
				if (tempCount == count) {
					this.bfrdWriter.write("Print-Row:" + printRow
							+ " :: No Progress:[number of records = " + count
							+ "] ");
				} else {
					this.count = tempCount;
					this.bfrdWriter.write("Print-Row:" + printRow
							+ "[TimeStamp :: " + getTimeStamp()
							+ "] :: [number of records =" + this.count
							+ "]:::[ size of db ="
							+ getSizeInMB(this.hashDb.sizeOfDatabase())
							+ " MB ] \n " + this.hashDb.getStatus().toString());
				}
				this.bfrdWriter.write("\n");// should be system dependent
				this.bfrdWriter.write("\n");
				this.bfrdWriter.flush();
				printRow++;
				Thread.sleep(IndexConstants.DFLT_SLEEP_TIME_1_MILLI_SECOND * 45);// 45
																					// seconds
			} catch (IOException ioExcp) {
				ioExcp.printStackTrace();
				ioError = true;
				throw new Error("IOException Occured", ioExcp);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Interrupt occured in printing reoprt::"
						+ e.getMessage());
			} finally {
				if (ioError) {
					try {
						this.bfrdWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new Error("Closing Error : IOException Occured",
								e);
					}
				}
			}

		}

	}

	// convert bytes to MB
	private float getSizeInMB(long sizeOfDatabase) {

		return (float) sizeOfDatabase / (long) (1024 * 1024);
	}

	private String getTimeStamp() {

		return new Date().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crawlerlucene.google.crawler4j.multithreaded.WorkerThread#shutdown()
	 */
	@Override
	public void shutdown() {
		stop = true;
		// Thread.currentThread().interrupt();

	}

}
