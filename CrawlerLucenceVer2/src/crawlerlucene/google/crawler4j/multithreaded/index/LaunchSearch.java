/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.IOException;
import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.TextFragment;

import crawlerlucene.google.crawler4j.multithreaded.browser.WebViewLaunchForCustomContent;
import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.index.util.IndexOperationUtil;

/**
 * This is used as intermediate class for the search result display and
 * formatting the search result and pagination.
 * 
 * @author sumit
 * 
 */
public class LaunchSearch {

	public static void LaunchInitialSearch() {
		Application.launch(WebViewLaunchForCustomContent.class,
				getSearchResultHTMLContent(null, true));

	}

	public static String search(String[] querys, int pageNumber)
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
				.setMaxSerachResultSize(Integer.MAX_VALUE);

		if (pageNumber <= 0)
			pageNumber = 1;

		serachOpertaioDataStructure.getSearchQueryInput().setPageNumber(
				pageNumber);
		// these things should be consistent with
		// serachDataStructure.getSearchResultPageProperty().setPageSize(10);
		serachOpertaioDataStructure.getSearchQueryInput().setPageSize(
				serachOpertaioDataStructure.getSearchResultPageProperty()
						.getPageSize());
		serachOpertaioDataStructure.getSearchQueryInput()
				.getSercahIndexOption().setAnalyzer(new StandardAnalyzer());
		serachOpertaioDataStructure.getSearchQueryInput().setQueryFieldFlags(
				new Occur[] { Occur.SHOULD, Occur.SHOULD });
		SearchResultContainer srchResultContainer = IndexOperationUtil
				.getSearchResult(MainIndexerAndSearcher.getIndxSearcher(),
						serachOpertaioDataStructure,
						new DefaultSerachFormatter());

		return getSearchResultHTMLContent(srchResultContainer, false);
	}

	private static String getSearchResultHTMLContent(
			SearchResultContainer srchResultContainer, boolean firstTimeLoad) {
		StringBuffer sb = new StringBuffer();

		// dont touch this text please
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
		sb.append("<title>text Based Search engine</title><meta name=\"Description\" content=\"\" /><meta name=\"Keywords\" content=\"\" /><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" media=\"screen\" />");
		sb.append("</head><body><div id=\"wrap\">  <div id=\"header\"> <h1><a href=\"#\">Text Based Search Engine</a></h1><h2>Search Statistics </h2></div>");

		if (srchResultContainer != null) {
			sb.append("<div id=\"menu\">");
			sb.append("<a href=\"#\"> Execution Time "
					+ srchResultContainer.getExecutionTime()
					+ " Milliseconds</a>");
			sb.append("<a href=\"#\"> Number Of hits : "
					+ srchResultContainer.getTotalHitCount() + " </a>");
			sb.append("<a href=\"#\"> User Query: "
					+ Arrays.asList(srchResultContainer.getUserInput())
					+ " </a>");
			sb.append("</div>");

			// search content
			sb.append("<div id=\"content\">");

			for (SearchResultData tmpSrchData : srchResultContainer
					.getSearchResults()) {
				sb.append("<h2><a href=\"" + tmpSrchData.getFilename() + "\">"
						+ tmpSrchData.getTitle() + "</a></h2>");
				sb.append("<p>");
				double avgScore = 0;
				int size = tmpSrchData.getFragments().length;
				for (TextFragment txtFragment : tmpSrchData.getFragments()) {
					avgScore = avgScore + txtFragment.getScore();
					sb.append(txtFragment.toString().trim() + "...");
				}
				sb.append("</p>");
				sb.append("<p>&nbsp;</p>");
				sb.append("<p class=\"meta\"> score :"
						+ tmpSrchData.getScoreOfDocResult() + " </p>");
				sb.append("<p>&nbsp;</p>");
			}
			sb.append("</div>");
			// end of search content
			// pagination content
			sb.append("<div id=\"divider\">");
			int multiple = srchResultContainer.getTotalHitCount()
					/ srchResultContainer.getSerachResultPerPage();
			int remainder = srchResultContainer.getTotalHitCount()
					% srchResultContainer.getSerachResultPerPage();
			for (int i = 1; i <= (multiple == 0 ? 1
					: (remainder == 0 ? multiple : multiple + 1)); i++) {
				sb.append("&raquo; <a href=\"#\" onclick=\"app.setPageNumber("
						+ i + ")\">Page " + (i) + "</a> &raquo;");
			}
			sb.append("</div>");
		} else {
			if (!firstTimeLoad) {
				sb.append("<div id=\"menu\"> Execution Time : 0.000 </div>");
				sb.append("<div id=\"content\">");
				sb.append("<h3> No result fetched</h3>");
				sb.append("</div>");
			} else {
				sb.append("<div id=\"menu\"> </div>");
				sb.append("<div id=\"content\">");
				sb.append("<h3>Please Enter search text at below search text box</h3>");
				sb.append("</div>");
			}
		}

		// pagination end
		sb.append("</body>");
		sb.append("</html>");

		// System.out.println(sb.toString());
		return sb.toString();
	}

}
