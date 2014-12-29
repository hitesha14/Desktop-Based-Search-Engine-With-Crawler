package crawlerlucene.google.crawler4j.multithreaded.index;

public class SearchOperationDataStructure {

	private SearchQueryInput searchQueryInput;
	private SearchResultPageProperty searchResultPageProperty;

	public SearchQueryInput getSearchQueryInput() {
		return searchQueryInput;
	}

	public void setSearchQueryInput(SearchQueryInput searchQueryInput) {
		this.searchQueryInput = searchQueryInput;
	}

	public SearchResultPageProperty getSearchResultPageProperty() {
		return searchResultPageProperty;
	}

	public void setSearchResultPageProperty(
			SearchResultPageProperty searchResultPageProperty) {
		this.searchResultPageProperty = searchResultPageProperty;
	}

	public SearchOperationDataStructure() {

	}

}
