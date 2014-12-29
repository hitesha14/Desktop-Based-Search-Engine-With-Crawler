/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.index.ArrayLocation;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchFormatter;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchOperationDataStructure;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchQueryInput;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchResultContainer;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchResultData;
import crawlerlucene.google.crawler4j.multithreaded.index.SearchResultPageProperty;

/**
 * @author sumit
 * 
 */
public final class IndexOperationUtil {

	private static FieldType _fieldTypeHugeData = new FieldType();

	static {
		_fieldTypeHugeData.setIndexed(true);
		_fieldTypeHugeData
				.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		_fieldTypeHugeData.setStoreTermVectorOffsets(true);
		_fieldTypeHugeData.setStored(true);
		_fieldTypeHugeData.setStoreTermVectors(true);
		_fieldTypeHugeData.setTokenized(true);

		classSelfObject = new IndexOperationUtil();
	}

	private static IndexOperationUtil classSelfObject;

	/**
	 * 
	 */
	private IndexOperationUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This is a singleton instance of the class
	 * 
	 * @return
	 */
	public static IndexOperationUtil getInstance() {

		return classSelfObject;
	}

	/**
	 * crawl the url, filter the body content using analyzer,
	 * 
	 * @param url
	 * @param analyzer
	 * @return
	 */
	public Document getDocument(String pUrl, Analyzer analyzer) {

		Metadata metadata = new Metadata();
		ContentHandler bodyConetnt = new BodyContentHandler(
				IndexConstants.MAX_SIZE_BODY_READ);
		ParseContext parseContext = new ParseContext();
		Parser parser = new AutoDetectParser();
		Document doc = null;
		URL resourceLocation = null;
		InputStream inptStream = null;

		try {
			resourceLocation = new URL(pUrl);
			inptStream = resourceLocation.openStream();
			//metadata.
			parser.parse(inptStream, bodyConetnt, metadata, parseContext);

			// get all meta-data related to the file and add field them to the
			// file details
			doc = indexMetadataOfFile(resourceLocation, metadata);
			// deal with the body content now, may be compressing required
			handleBodyContent(doc, bodyConetnt.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error occured in parsing url :" + pUrl + ":"
					+ e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error occured in parsing url :" + pUrl + ": "
					+ e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error occured in parsing data at url : " + pUrl
					+ " : " + e.getMessage());
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (NoSuchMethodError error) {
			System.err.println("cannot parse urls critical error(Api internal error while parsing for certain file such as ): "+pUrl);
		} finally {
			if (inptStream != null)
				try {
					inptStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Error occured in releasing resource : "
							+ e.getMessage());
				}
		}

		return doc;
	}

	/**
	 * Need to think of compression and is it serachable also, No compressed doc
	 * is not searchable.
	 * 
	 * @param doc
	 * @param string
	 */
	private static void handleBodyContent(Document doc, String hugeData) {
		//
		doc.add(new Field(IndexConstants.FIELD_TEXT, hugeData,
				_fieldTypeHugeData));
	}

	/**
	 * This is the method needs to modify while considering what fields to add
	 * in the index file Currently we are considering on metadata fields only
	 * and other things. Each document will have document-key [key=md5(<hashcode
	 * of the url_[file_name_of_url]>)]
	 * 
	 * @param resourceLocation
	 * @param metadata
	 * @return
	 */
	// this will create all indexing file attribute
	private static Document indexMetadataOfFile(URL resourceLocation,
			Metadata metadata) {

		Document doc = new Document();
		FieldType filedType = new FieldType();
		filedType.setIndexed(false);
		filedType.setStored(true);

		// adding file name , have to check is search-able
		doc.add(new Field(IndexConstants.FIELD_DOC_ID,
				getDocumentKey(resourceLocation), filedType));

		// add file name and it is indexable
		doc.add(new TextField(IndexConstants.FIELD_FILE, resourceLocation
				.toExternalForm(), Store.YES));

		// add file metadata information and all of them are indexable
		for (String key : metadata.names()) {
			String name = key.toLowerCase();
			String value = metadata.get(key);

			if (StringUtils.isBlank(value)) {
				continue;
			}

			// if it is key=keywords than, add one by one value for it
			if (IndexConstants.METADTA_KEYWORDS.equalsIgnoreCase(key)) {
				for (String keyword : value.split(",?(\\s)+")) {
					doc.add(new TextField(name, keyword, Store.YES));
				}
			} else if (IndexConstants.FIELD_TITLE.equalsIgnoreCase(key)) {
				doc.add(new TextField(name, value, Store.YES));
			} else {
				// System.out.println("is it important ??key=" + name +
				// ", value="
				// + value);
			}
		}// end of for

		return doc;
	}

	/**
	 * This method is using MD5 crypt to generate keys
	 * 
	 * @param resourceLocation
	 * @return
	 */
	public static String getDocumentKey(URL resourceLocation) {

		return DigestUtils.md5Hex(resourceLocation.toExternalForm().hashCode()
				+ "_" + resourceLocation.toExternalForm());
	}

	/**
	 * this will be like (field1:query1) (field2:query2)
	 * (field3:query3)...(fieldx:queryx)
	 * 
	 * @param searchQueryPhrase
	 * @param indexOptions
	 * @return
	 * @throws ParseException
	 */
	public static Query getMultiFieldParsedQueryBuilder(
			SearchQueryInput searchQueryInput) throws ParseException {
		return MultiFieldQueryParser.parse(searchQueryInput.getSerachQuery(),
				searchQueryInput.getSearchableField(), searchQueryInput
						.getSercahIndexOption().getAnalyzer());
	}

	/**
	 * FLAG(+/-) filed1:query FLAG(+/-) field2:query2 ....
	 * 
	 * @param query
	 * @param fieldName
	 * @param occur
	 * @param analyzer
	 * @return
	 * @throws ParseException
	 */
	public static Query getMultiFieldParsedQueryBuilder(String query,
			String[] fieldName, Occur[] occur, Analyzer analyzer)
			throws ParseException {
		return MultiFieldQueryParser.parse(query, fieldName, occur, analyzer);
	}

	/**
	 * 
	 * @param indxSeracher
	 * @param serachOrtnDataStrctr
	 * @param searchFormatter
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public static SearchResultContainer getSearchResult(
			IndexSearcher indxSeracher,
			SearchOperationDataStructure serachOrtnDataStrctr,
			SearchFormatter searchFormatter) throws ParseException,
			IOException, InvalidTokenOffsetsException {

		SearchResultContainer srchResultContainer = new SearchResultContainer();
		List<SearchResultData> searchResultData = new ArrayList<>();

		Query query = getMultiFieldParsedQueryBuilder(
				serachOrtnDataStrctr.getSearchQueryInput().getSerachQuery()[0],
				serachOrtnDataStrctr.getSearchQueryInput().getSearchableField(),
				serachOrtnDataStrctr.getSearchQueryInput().getQueryFieldFlags(),
				serachOrtnDataStrctr.getSearchQueryInput()
						.getSercahIndexOption().getAnalyzer());

		long start = System.currentTimeMillis();

		SearchResultPageProperty searchRsltPrprty = serachOrtnDataStrctr
				.getSearchResultPageProperty();

		// get top documents based on the
		TopDocs topDocsHits = indxSeracher.search(query, serachOrtnDataStrctr
				.getSearchQueryInput().getMaxSerachResultSize());
		if (topDocsHits.scoreDocs.length < 1) {
			return null;
		}
		ArrayLocation resultLocation = PaginatorUtil.calculateArrayLocation(
				topDocsHits.scoreDocs.length, serachOrtnDataStrctr
						.getSearchQueryInput().getPageNumber(),
				serachOrtnDataStrctr.getSearchQueryInput().getPageSize());

		SearchResultData srchResultData=null;
		// indxSeracher.getIndexReader().getTermVector(docId, fieldName);
		for (int i = resultLocation.getStart() - 1; i < resultLocation.getEnd(); i++) {
			int docId = topDocsHits.scoreDocs[i].doc;
			Document doc = indxSeracher.doc(docId);
			srchResultData=searchFormatter.formatSearchOutput(
					indxSeracher, docId, query, serachOrtnDataStrctr);
			srchResultData.setScoreOfDocResult(topDocsHits.scoreDocs[i].score);
			searchResultData.add(srchResultData);

		}
		long runtime = System.currentTimeMillis() - start;

		srchResultContainer.setUserInput(serachOrtnDataStrctr
				.getSearchQueryInput().getSerachQuery());
		srchResultContainer.setTotalHitCount(topDocsHits.scoreDocs.length);
		srchResultContainer.setExecutionTime(runtime);
		srchResultContainer.setSearchResults(searchResultData);
		srchResultContainer.setSerachResultPerPage(serachOrtnDataStrctr
				.getSearchQueryInput().getPageSize());
		return srchResultContainer;

	}

}
