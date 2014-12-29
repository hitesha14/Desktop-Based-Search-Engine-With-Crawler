/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.constant;

import java.util.concurrent.TimeUnit;

import kyotocabinet.DB;

/**
 * This contains all the constants required by the execution of the indexing and
 * search engine. It also contains some configurable parameters too.Like file
 * location for generating the database setup and all.
 * 
 * @author sumit
 */
public class IndexConstants {

	public static final String SHUTDOWN_REQ = "shutdown-thread";

	public static final String URI_LOCATION_FIELD = "url_location";
	public static final String URI_FULL_CONTENT = "raw_content";
	public static final String METADTA_KEYWORDS = "keywords";
	public static final String FIELD_FILE = "file";
	public static final String FIELD_DOC_ID = "document_id";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_ABSTRACT_TEXT = "text_leading";
	public static final String FIELD_TEXT = "text";

	public static String[] FILEDS_NAME_SEARCHABLE = new String[] {
			IndexConstants.METADTA_KEYWORDS, IndexConstants.FIELD_TEXT };

	// optimization parameter
	// crawling parameter
	public static final int HITS_PER_PAGE_DEFAULT = 20;
	public static final int DEFAULT_SERACH_REPEAT = 5;
	public static final int BUFFER_SEARCH_PAGE_SIZE_DFLT = 5;
	public static final int MAX_SIZE_OF_BODY_CONTENT_IDEXING = 10;
	public static final int RAM_BUFFER_SIZE_CACHE = 20 * 1024;
	public static final int MAX_SIZE_BODY_READ = 1024 * 1024 * 16;// 16 mb size
	public static final int MAX_SIZE_INDEX_WRITER_ALLOWED = 1024 * 1024;

	// thread related data sets
	public static final int DFLT_NUMBER_OF_WRKR_THREAD = 10;// <=DFLT_THREAD_MAX_POOL_SIZE
	public static final int NUMBER_OF_CRAWLER_THREAD = 5;// <=DFLT_NUMBER_OF_WRKR_THREAD
	public static final int DFLT_THREAD_MAX_POOL_SIZE = DFLT_NUMBER_OF_WRKR_THREAD + 5;// for
																						// reader,writer
																						// (2)
																						// report
																						// ,release
																						// Memory
	public static final int DFLT_THREAD_CORE_POOL_SIZE = 5;
	public static final long DFLT_IDEAL_THREAD_MAX_WAITING_TIME_BEFORE_DIE_IN_POOL_DAYS = 30;
	public static final TimeUnit DFLT_TIME_UNIT_DAYS = TimeUnit.DAYS;
	// 1 thread for reader for db, 1 thread for reporting, rest all for indexing
	public static final int DFLT_NUMBER_OF_INDEXER_THREAD = 8;
	public static final long DFLT_WAITING_TIME_BEFORE_EXIT_MINUTES = 2;
	public static final TimeUnit DFLT_WAITING_TIME_UNIT_MINUTE = TimeUnit.MINUTES;
	// avoiding race condition parameter
	public static final int DFLT_SLEEP_TIME_1_MILLI_SECOND = 1000;
	public static final int DFLT_SLEEP_TIME_IN_MILLI_SECS = 1000 * 10;// 10
																		// seconds
	public static final int DFLT_WAIT_FOR_HASHDB_TO_POPULATE_MILLI_SEC = 1000 * 60;// 1
																					// minumte

	// db parameter
	public static final int DFLT_READ_WRITE_MODE = DB.OWRITER | DB.OREADER
			| DB.OCREATE | DB.ONOLOCK | DB.OAUTOSYNC | DB.OAUTOTRAN;
	public static final int DFLT_READ_ONLY_MODE = DB.OREADER | DB.OAUTOSYNC;
	public static final int DFLT_READ_APPEND_OLNY = DB.OREADER | DB.OAUTOSYNC
			| DB.MADD;
	public static final int DFLT_READ_WRITE_MODE_CREATE_ALWAYS_FRESH_DB = DB.OWRITER
			| DB.OREADER | DB.OCREATE | DB.ONOLOCK | DB.OAUTOTRAN;

	public static final String DFLT_HASH_DB_URL = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\HASH-DB\\casket.kch";
	// path parameter flat file system
	public static final String INDEX_PATH = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\lucene\\indexed.txt";
	public static final String ROOT_DIR_FOR_CRAWLING = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\carwled";
	// Filtering parameter
	// modify allowed extensions for crawlig: binary data is not allowed and zip
	// file and executable script ,shell,exe,bat, anythings like that
	// http://technet.microsoft.com/en-us/library/jj219530(v=office.15).aspx
	public static final String PATTERN_URL_EXT_ALLOWED_MOD = "\\.((ht(m|a|w|x)l?)|((jh|mh)?tml)|(as(p|c)?x?)|(ppt(m|x)?)|(pp(s|a)?)|(doc(m|x)?)|(od(c|p|s|t)?)|jsp|pdf|txt|java|c|h|hpp|php|rtf|c\\+\\+|c\\#|net|xml|eml|nws)";
	public static final String PATTERN_URL_WITHOUT_EXTENSION = "\\/[a-zA-Z0-9\\_\\s\\/\\-]+";
	public static final String PATTERN_URL_ONLY_HTML = "(ht(m|a|w|x)l?)";
	public static final String IMAGE_URL_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
	// don't touch below one
	public static final String PATTREN_URL_EXT_ALOWED = ".*(("
			+ PATTERN_URL_EXT_ALLOWED_MOD + ")|"
			+ PATTERN_URL_WITHOUT_EXTENSION + ")";
	public static final String PATTREN_VALID_URL_FORMAT = "^(ht|f)tp(s?)\\:\\/\\/(([a-zA-Z0-9\\-\\._]+(\\.[a-zA-Z0-9\\-\\._]+)+)|localhost)(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\+&amp;%\\$#_]*)?([\\d\\w\\.\\/\\%\\+\\-\\=\\&amp;\\?\\:\\\\&quot;\\'\\,\\|\\~\\;]*)$";
	// otiginal -url
	// :^(ht|f)tp(s?)\:\/\/(([a-zA-Z0-9\-\._]+(\.[a-zA-Z0-9\-\._]+)+)|localhost)(\/?)([a-zA-Z0-9\-\.\?\,\'\/\\\+&amp;%\$#_]*)?([\d\w\.\/\%\+\-\=\&amp;\?\:\\\&quot;\'\,\|\~\;]*)$

	public static final String[] INITIAL_URL_DATA_SEED = {
			"http://www.nytimes.com/?WT.z_jog=1",
			"http://homes.soic.indiana.edu/", "http://www.seobythesea.com/",
			"https://www.yahoo.com/", "http://en.wikipedia.org/wiki/Main_Page" };// ,"http://demo.borland.com/Testsite/stadyn_largepagewithimages.html","http://forums.mozillazine.org/viewtopic.php?f=7&t=219814","http://www.integralbook.com/wp-content/uploads/2012/03/GURDJIEFF-G.I.-Life-is-real-only-then-when-I-am.pdf"};

	// log file destination this should be moved later
	public static final String NAME_OF_DEBUG_LOG_FILE_TO_OUT_PUT = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\Logger\\debug_log.txt";
	public static final String NAME_OF_ERROR_LOG_FILE_TO_OUT_PUT = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\Logger\\err_log.txt";
	public static final String LOCATION_OF_REPORT_OF_FILE = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\Report\\report-status.txt";

	public static final String SEED_DATA_FILE_LOCATION = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\Seed-Data\\robots.txt";
	public static final String LAST_RUN_STATUS_DATA_FILE_LOATION = "L:\\ada assignments p503\\algo projects doc\\carwled-data-google\\setup-data\\setup-data.txt";
}
