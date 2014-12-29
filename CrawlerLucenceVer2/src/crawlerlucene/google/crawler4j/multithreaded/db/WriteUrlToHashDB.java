/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.db;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;

/**
 * This is writer to the hash db, it takes the data from the hash db, it writes
 * the data to the hash table implementation i/o efficient.
 * 
 * @author sumit
 */
public class WriteUrlToHashDB implements WorkerThread {

	private BlockingQueue<String> dbURLHolderData;
	private HashDB dbInstance;
	private boolean stop;

	public WriteUrlToHashDB(BlockingQueue<String> pUrlSavingLocation,
			HashDB pDBInstance) {
		this.dbURLHolderData = pUrlSavingLocation;
		this.dbInstance = pDBInstance;
	}

	@Override
	public void shutdown() {
		stop = true;
		// Thread.currentThread().interrupt();
		try {
			this.dbURLHolderData.put(IndexConstants.SHUTDOWN_REQ);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				// System.out.println(dbURLHolderData);
				// shutting down-hook
				String url = this.dbURLHolderData.take();
				if (stop && url.equalsIgnoreCase(IndexConstants.SHUTDOWN_REQ)) {
					break;
				}
				this.dbInstance.addItem(url.hashCode() + "", url);
				// System.out.println("adding item"+this.dbInstance.getLastDBErrorOccured());
			} catch (InterruptedException ex) {
				Logger.getLogger(WriteUrlToHashDB.class.getName()).log(
						Level.SEVERE, null, ex);
				ex.printStackTrace();
				System.err.println("Interrupted Exception Occured");
			}
		}
	}

}
