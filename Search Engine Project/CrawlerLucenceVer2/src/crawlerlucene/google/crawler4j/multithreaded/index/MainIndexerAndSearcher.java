/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Application;
import javafx.application.Platform;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import crawlerlucene.google.crawler4j.multithreaded.api.SimpleThreadPool;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.db.HashDB;
import crawlerlucene.google.crawler4j.multithreaded.db.ReadURLFromHashDB;

/**
 * This is the main class to begin with for indexing and searching.
 * 
 * @author sumit
 * 
 */
public class MainIndexerAndSearcher {

	private static IndexWriter globalIndxWriter;
	private static IndexSearcher globalIndexSearcher;
	private static HashDB globalHashDBInstance;
	// number threads to start with for indexing
	private static SimpleThreadPool globalThreadPool;
	private static final BlockingQueue<String> globalUrlYetToIndex = new LinkedBlockingQueue<>(
			1000);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {
			// 1 thread for reader for db, 1 thread for reporting, rest all for
			// indexing
			int numberOfThreadWorking = IndexConstants.DFLT_NUMBER_OF_INDEXER_THREAD;

			// initialize system
			initializeIndexSystem(numberOfThreadWorking);
			// I. in multithreaded fashion create index
			createIndex(numberOfThreadWorking - 2);// minus because one for
													// reporting and another for
													// future reference like for
													// searching
			// govern control to user
			askUser();

			// postCrawlling
			savingStateOfIndexing();

		} finally {
			// closing all resource
			systemShutDownProcess();
			System.exit(0);
		}

	}

	private static void systemShutDownProcess() throws IOException {

		globalThreadPool.immediateShutdown();

		boolean timeOutOccuredForExit = false;

		try {
			globalThreadPool.waitForThreadPoolTermination(
					IndexConstants.DFLT_WAITING_TIME_BEFORE_EXIT_MINUTES,
					IndexConstants.DFLT_WAITING_TIME_UNIT_MINUTE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			timeOutOccuredForExit = true;
		}
		// close resources when all threads are over doing stuff
		if (!globalThreadPool.isDeamonExecutorShutDown()
				&& timeOutOccuredForExit) {
			System.err
					.println("Cannot Shutdown Indexing system properly Time Out has occured!!");
			;
		}

		// try to release other resources
		globalIndxWriter.commit();
		globalIndxWriter.close();
		globalHashDBInstance.closeConnection();

	}

	private static void savingStateOfIndexing() {
		// dump all the status of the blocking queue to the file

	}

	private static void askUser() throws IOException {
		// just to avoid close all resources
		BufferedReader bfrdRdr = null;
		try {
			bfrdRdr = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while (true) {

				System.out.println("----------Menu---------");
				System.out.println("1. Serach Result \t[s]");
				System.out.println("2. Stop/Quit/Exit [q]");
				System.out.println("Enter a valid string [s/q] to quit:");
				input = bfrdRdr.readLine();
				input = input.trim().toLowerCase();
				switch (input) {

				case "s":
					LaunchSearch.LaunchInitialSearch();
					break;
				case "q":
					break;
				default:
					System.out
							.println("Invalid input please renter the avalid input");
					continue;
				}
				if (input.equalsIgnoreCase("q"))
					break;
			}
		} finally {
			if (bfrdRdr != null)
				bfrdRdr.close();
		}

	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static IndexSearcher getIndxSearcher() throws IOException {
		if (globalIndexSearcher != null)
			return globalIndexSearcher;
		if (globalIndxWriter == null) {
			initializeIndexWriter();
		}
		globalIndexSearcher = new IndexSearcher(DirectoryReader.open(
				globalIndxWriter, false));
		return globalIndexSearcher;
	}

	private static void createIndex(int numberOfThreadToWorkWith)
			throws IOException {

		if (numberOfThreadToWorkWith < 2) {
			throw new IllegalArgumentException(
					"At least 2 thread are required to start with indexng!!");
		}

		globalIndxWriter.deleteAll();
		// start creating indexing
		// setting up data sets

		// Thread - 2
		// populate read and write global url structure
		ReadURLFromHashDB readrFromDbAllUrl = new ReadURLFromHashDB(
				globalUrlYetToIndex, globalHashDBInstance);

		globalThreadPool.execute(readrFromDbAllUrl);
		
		for (int i = 0; i < numberOfThreadToWorkWith - 1; i++) {
			// now start indexing
			IndexCreator indxCrtr = new IndexCreator(globalIndxWriter,
					globalUrlYetToIndex);
			globalThreadPool.execute(indxCrtr);
		}
	}

	/**
	 * method to initialize important data structure required for various
	 * operation
	 * 
	 * @throws IOException
	 */
	private static void initializeIndexSystem(int maxThreadPoolSize)
			throws IOException {
		// do indexing
		// I.get FlatFileSyatem directory

		initializeIndexWriter();
		// initializing the global db instance
		globalHashDBInstance = HashDB.getConnection(
				IndexConstants.DFLT_HASH_DB_URL,
				IndexConstants.DFLT_READ_ONLY_MODE);

		// number of reader writer threads
		globalThreadPool = new SimpleThreadPool(maxThreadPoolSize);

	}

	private static void initializeIndexWriter() throws IOException {
		Directory dir = FSDirectory.open(new File(IndexConstants.INDEX_PATH));
		// 2. get index-writer object

		if (IndexWriter.isLocked(dir)) {
			IndexWriter.unlock(dir);
		}

		IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST,
				new StandardAnalyzer());

		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		iwc.setRAMBufferSizeMB(IndexConstants.RAM_BUFFER_SIZE_CACHE);
		globalIndxWriter = new IndexWriter(dir, iwc);

	}

	public static void closeIndexSearcher() throws IOException {
		if (globalIndexSearcher != null)
			globalIndexSearcher.getIndexReader().close();
			globalIndexSearcher=null;
	}
}
