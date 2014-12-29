/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import kyotocabinet.Cursor;
import kyotocabinet.DB;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;

/**
 * This is the api to be invoked and use by other threads. It has all details
 * about the database , hash . It uses the implementation of i/o efficients of
 * the hash db.
 * 
 * @author sumit
 */
public class HashDB {

	private DB hashDB = null;
	private final String dbURL;
	private int mode = IndexConstants.DFLT_READ_WRITE_MODE;
	private boolean isOpen;
	private static final ConcurrentMap<String, HashDB> ALL_HASH_DB_INSTANCE = new ConcurrentHashMap<>();

	private HashDB(String pDBURL, int pMode) {
		this.hashDB = new DB(DB.GEXCEPTIONAL);
		this.dbURL = pDBURL;
		if (pMode > 0) {
			this.mode = pMode;
		}
		ALL_HASH_DB_INSTANCE.putIfAbsent(getKey(pDBURL, pMode), this);
	}

	// default mode is read and write
	private HashDB open() {
		if (!isOpen) {
			isOpen = true;
			hashDB.open(this.dbURL, this.mode);

			System.out.println(this.getLastDBErrorOccured() + "--"
					+ this.hashDB.path());
		}
		return this;
	}

	public static HashDB getConnection(String pDBURL, int pMode) {
		if (pDBURL == null) {
			throw new IllegalArgumentException("Invalid URL passed");
		}
		if (ALL_HASH_DB_INSTANCE.containsKey(getKey(pDBURL, pMode))) {
			return ALL_HASH_DB_INSTANCE.get(getKey(pDBURL, pMode));
		}
		return new HashDB(pDBURL, pMode).open();
	}

	private static String getKey(String pDBURL, int pMode) {

		return pDBURL + "_" + pMode;
	}

	public Cursor getIterator() {
		return this.hashDB.cursor();
	}

	public boolean addItem(String key, String value) {
		return this.hashDB.add(key, value);
	}

	public String getItem(String key) {
		return this.hashDB.get(key);
	}

	public boolean reset() {
		return this.hashDB.clear();
	}

	public boolean closeConnection() {
		// remove entry from hashmap
		ALL_HASH_DB_INSTANCE.remove(getKey(this.dbURL, this.mode));
		this.isOpen = false;
		return this.hashDB.close();
	}

	public boolean beginTransaction(boolean physicalSynhronization) {
		return this.hashDB.begin_transaction(physicalSynhronization);
	}

	// true to commit transaction or false for abort
	public boolean endTransaction(boolean commit) {
		return this.hashDB.end_transaction(commit);
	}

	public Error getLastDBErrorOccured() {
		return new Error(this.hashDB.error());
	}

	// number of records
	public long count() {

		return this.hashDB.count();
	}

	// size of database in bytes, -1 on failure
	public long sizeOfDatabase() {
		return this.hashDB.size();
	}

	public boolean update(String key, String oldValue, String newValue) {
		return this.hashDB.cas(key, oldValue, newValue);
	}

	// true if exists or false if fails
	public boolean containsKey(String key) {
		return this.hashDB.get(key) != null;
	}

	// return value for key and remove
	public String delete(String pKey) {
		return this.hashDB.seize(pKey);
	}

	// true-this will remove if key found and false-fails
	public boolean removeOnly(String pKey) {
		return this.hashDB.remove(pKey);
	}

	// If no record corresponds to the key, a new record is created. If the
	// corresponding record exists, the given value is appended at the end of
	// the existing value.
	public boolean append(String pKey, String pvalue) {
		return this.hashDB.append(pKey, pvalue);
	}

	public Map<String, String> getStatus() {
		return this.hashDB.status();
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable ex) {
			Logger.getLogger(HashDB.class.getName())
					.log(Level.SEVERE, null, ex);
		} finally {
			// remove the item form the map
			ALL_HASH_DB_INSTANCE.remove(getKey(this.dbURL, this.mode));
			// relese the db
			this.hashDB.close();
		}
	}

	@Override
	public int hashCode() {
		return this.dbURL.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof HashDB) {
			return ((HashDB) object).hashCode() == this.hashCode()
					&& ((HashDB) object).mode == this.mode;
		}
		return false;
	}

}
