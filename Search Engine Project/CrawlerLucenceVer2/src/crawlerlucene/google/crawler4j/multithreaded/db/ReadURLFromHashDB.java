/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.db;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import crawlerlucene.google.crawler4j.multithreaded.api.WorkerThread;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import kyotocabinet.Cursor;

/**
 * This is the reader of hash db. It reads from the hash-db and prepare the data
 * structure that is used by other threads or application.
 * 
 * @author sumit
 */
public class ReadURLFromHashDB implements WorkerThread {

	private BlockingQueue<String> saveDBReadURLData;
	private HashDB dbInstance;
	private boolean stop;

	public ReadURLFromHashDB(BlockingQueue<String> pUrlSavingLocation,
			HashDB pDBInstance) {
		this.saveDBReadURLData = pUrlSavingLocation;
		this.dbInstance = pDBInstance;
	}

	@Override
	public void run() {
		Cursor dbRecordIterator = dbInstance.getIterator();
		dbRecordIterator.jump();
		String key = "";
		String value;
		while (!stop) {
			// keep n reading from the data source and push into the
			// destinationOfReadFromDB
			while (true) {
				String tempKey = dbRecordIterator.get_key_str(true);

				if (tempKey == null) {
					if (key.length() != 0) {
						System.err
								.println("reached end of cursor read, resseting after key : "
										+ key);
						dbRecordIterator.disable();
						try {
							Thread.sleep(IndexConstants.DFLT_WAIT_FOR_HASHDB_TO_POPULATE_MILLI_SEC);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dbRecordIterator = this.dbInstance.getIterator();
						dbRecordIterator.jump(key);
						dbRecordIterator.step();
						System.err.println("Db error re-positioning of cursor:"
								+ dbInstance.getLastDBErrorOccured());
					}
					break;
				}
				value = dbRecordIterator.get_value_str(true);

				// System.out.println("read from db :" +
				// dbRecordIterator.get_value_str(true));
				// System.out.println(dbInstance.getLastDBErrorOccured());

				key = tempKey;
				try {
					if (value != null) {
						this.saveDBReadURLData.offer(value, 50,
								TimeUnit.SECONDS);
					}
				} catch (InterruptedException ex) {
					Logger.getLogger(ReadURLFromHashDB.class.getName()).log(
							Level.SEVERE, null, ex);
					System.err.println("Unable to add url read from db::"
							+ dbRecordIterator.get_value_str(true));
				}

			}// end of inner while loop
		}// end of outer while loop
		dbRecordIterator.disable();
	}

	public void shutdown() {
		this.stop = true;
		// Thread.currentThread().interrupt();
	}
}
