package crawlerlucene.google.crawler4j.multithreaded.index.util;

import java.io.IOException;

import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.spans.SpanScorer;

import crawlerlucene.google.crawler4j.multithreaded.index.SearchOperationDataStructure;

public class HTMLHighlighterUtil {

	/**
	 * Generates contextual fragments.
	 * 
	 * @param termPosVector
	 *            - Term Position Vector for fieldName
	 * @param query
	 *            - query object created from user's input
	 * @param fieldName
	 *            - name of the field containing the text to be fragmented
	 * @param fieldContents
	 *            - contents of fieldName
	 * @param fragmentNumber
	 *            - max number of sentence fragments to return
	 * @param fragmentSize
	 *            - the max number of characters for each fragment
	 * @return array of fragments from fieldContents with terms used in query in
	 *         <b> </b> tags
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */

	public static TextFragment[] getFragmentsWithHighlightedTerms(Terms termPosVector,
			Query query, String fieldName, String fieldContents,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws IOException, InvalidTokenOffsetsException {

		TokenStream stream = TokenSources.getTokenStream(termPosVector);
		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(),
				new QueryScorer(query));
		highlighter.setEncoder(new SimpleHTMLEncoder());
		// this is a good practice, for optimization purpose
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);

//		System.out.println(highlighter.getBestFragments(stream,
//				fieldContents, searchOprtndataStrctr
//				.getSearchResultPageProperty().getMaxFragmentNumber(),".....")); 

		return highlighter.getBestTextFragments(stream,
				fieldContents,true, searchOprtndataStrctr
				.getSearchResultPageProperty().getMaxFragmentNumber());
	}

	/**
	 * Generates contextual fragments. Assumes term vectors not stored in the
	 * index.
	 * 
	 * @param analyzer
	 *            - analyzer used for both indexing and searching
	 * @param query
	 *            - query object created from user's input
	 * @param fieldName
	 *            - name of the field in the lucene doc containing the text to
	 *            be fragmented
	 * @param fieldContents
	 *            - contents of fieldName
	 * @param fragmentNumber
	 *            - max number of sentence fragments to return
	 * @param fragmentSize
	 *            - the max number of characters for each fragment
	 * @return array of fragments from fieldContents with terms used in query in
	 *         <b> </b> tags
	 * @throws IOException
	 */
	public static TextFragment[] getFragmentsWithHighlightedTerms(Document doc,
			Query query, String fieldName,
			SearchOperationDataStructure searchOprtndataStrctr)
			throws IOException, InvalidTokenOffsetsException {

		TokenStream stream = TokenSources.getTokenStream(doc, fieldName,
				searchOprtndataStrctr.getSearchQueryInput()
						.getSercahIndexOption().getAnalyzer());

		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(),
				new QueryScorer(query));
		highlighter.setEncoder(new SimpleHTMLEncoder());
		// this is a good practice, for optimization purpose
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		System.out.println(highlighter.getBestFragments(stream, doc
				.get(fieldName), searchOprtndataStrctr
				.getSearchResultPageProperty().getMaxFragmentNumber(), "...."));

		return highlighter.getBestTextFragments(stream, doc.get(fieldName),
				false, searchOprtndataStrctr.getSearchResultPageProperty()
						.getMaxFragmentNumber());
	}
}