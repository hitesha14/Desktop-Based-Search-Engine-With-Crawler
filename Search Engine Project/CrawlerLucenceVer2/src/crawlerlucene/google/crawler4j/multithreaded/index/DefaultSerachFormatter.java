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
import org.apache.lucene.search.highlight.TextFragment;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.index.util.HTMLHighlighterUtil;

/**
 * This is used to format the result page search engine.
 * 
 * @author sumit
 * 
 */
public class DefaultSerachFormatter implements SearchFormatter {

	@Override
	public SearchResultData formatSearchOutput(Document doc, Query query,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws ParseException, IOException, InvalidTokenOffsetsException {

		TextFragment[] textFragment = HTMLHighlighterUtil
				.getFragmentsWithHighlightedTerms(doc, query,
						IndexConstants.FIELD_TEXT, searchOprtndataStrctr);

		SearchResultData searchResultData = new SearchResultData();
		searchResultData.setFilename(doc.get(IndexConstants.FIELD_FILE));
		searchResultData.setFragments(textFragment);
		searchResultData.setTitle(doc.get(IndexConstants.FIELD_TITLE));
		return searchResultData;
	}

	@Override
	public SearchResultData formatSearchOutput(IndexSearcher indxSearcher,
			int documentId, Query query,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws ParseException, IOException, InvalidTokenOffsetsException {

		Document doc = indxSearcher.doc(documentId);

		TextFragment[] textFragment = HTMLHighlighterUtil
				.getFragmentsWithHighlightedTerms(indxSearcher.getIndexReader()
						.getTermVector(documentId, IndexConstants.FIELD_TEXT),
						query, IndexConstants.FIELD_TEXT, doc
								.get(IndexConstants.FIELD_TEXT),
						searchOprtndataStrctr);

		SearchResultData searchResultData = new SearchResultData();
		searchResultData.setFilename(doc.get(IndexConstants.FIELD_FILE));
		searchResultData.setFragments(textFragment);
		searchResultData.setTitle(doc.get(IndexConstants.FIELD_TITLE));
		return searchResultData;
	}

}
