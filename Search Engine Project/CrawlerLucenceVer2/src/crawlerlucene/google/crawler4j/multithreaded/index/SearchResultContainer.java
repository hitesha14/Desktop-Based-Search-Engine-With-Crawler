package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @author sumit
 * 
 */
public class SearchResultContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5830132934985308649L;

	private List<SearchResultData> searchResults;

	private int totalHitCount;
	private String[] userInput;
	private long executionTime;

	private int serachrResultPerPage;

	public int getSerachResultPerPage() {
		return serachrResultPerPage;
	}

	public void setSerachResultPerPage(int serachresultPerPage) {
		this.serachrResultPerPage = serachresultPerPage;
	}

	public SearchResultContainer() {

	}

	public List<SearchResultData> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(List<SearchResultData> searchResults) {
		this.searchResults = searchResults;
	}

	public String[] getUserInput() {
		return userInput;
	}

	public void setUserInput(String[] userInput) {
		this.userInput = userInput;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public int getTotalHitCount() {
		return totalHitCount;
	}

	public void setTotalHitCount(int totalHitCount) {
		this.totalHitCount = totalHitCount;
	}
}