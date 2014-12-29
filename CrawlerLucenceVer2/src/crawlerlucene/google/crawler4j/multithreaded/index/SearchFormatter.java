/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

/**
 * This is marker class, the implementation used for formatting the search
 * result raw object.
 * 
 * @author sumit
 * 
 */
public interface SearchFormatter {

	public SearchResultData formatSearchOutput(Document doc, Query query,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws ParseException, IOException, InvalidTokenOffsetsException;

	public SearchResultData formatSearchOutput(IndexSearcher indxSearcher,
			int documentId, Query query,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws ParseException, IOException, InvalidTokenOffsetsException;
}
