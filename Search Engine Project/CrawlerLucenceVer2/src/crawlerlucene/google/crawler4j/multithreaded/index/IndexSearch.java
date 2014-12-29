/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.index.util.IndexOperationUtil;

/**
 * This class is used to search the index created on the flat file system for
 * helping search.
 * 
 * @author sumit
 * 
 */
public class IndexSearch {

	/**
	 * 
	 */
	public IndexSearch() {
		// TODO Auto-generated constructor stub
	}

	public SearchResultContainer displaySerachResult(
			IndexSearcher indexSearcher, String[] querys)
			throws ParseException, IOException, InvalidTokenOffsetsException {
		SearchOperationDataStructure serachOpertaioDataStructure = new SearchOperationDataStructure();
		serachOpertaioDataStructure
				.setSearchResultPageProperty(new SearchResultPageProperty());
		serachOpertaioDataStructure.setSearchQueryInput(new SearchQueryInput(
				IndexConstants.FILEDS_NAME_SEARCHABLE, querys,
				new SearchIndexOptions()));

		serachOpertaioDataStructure.getSearchResultPageProperty()
				.setMaxFragmentNumber(5);

		serachOpertaioDataStructure.getSearchResultPageProperty().setPageSize(
				10);

		serachOpertaioDataStructure.getSearchQueryInput()
				.setMaxSerachResultSize(1000);
		serachOpertaioDataStructure.getSearchQueryInput().setPageNumber(1);
		// these things should be consistent with
		// serachDataStructure.getSearchResultPageProperty().setPageSize(10);
		serachOpertaioDataStructure.getSearchQueryInput().setPageSize(
				serachOpertaioDataStructure.getSearchResultPageProperty()
						.getPageSize());
		serachOpertaioDataStructure.getSearchQueryInput()
				.getSercahIndexOption().setAnalyzer(new StandardAnalyzer());
		serachOpertaioDataStructure.getSearchQueryInput().setQueryFieldFlags(
				new Occur[] { Occur.SHOULD, Occur.SHOULD });
		return IndexOperationUtil.getSearchResult(indexSearcher,
				serachOpertaioDataStructure, new DefaultSerachFormatter());

	}

}
