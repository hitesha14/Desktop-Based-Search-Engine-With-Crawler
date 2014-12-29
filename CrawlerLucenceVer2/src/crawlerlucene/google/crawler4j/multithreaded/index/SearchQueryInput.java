package crawlerlucene.google.crawler4j.multithreaded.index;

import org.apache.lucene.search.BooleanClause.Occur;

/**
 * This stores the user query details entered by the users.and the indexed field
 * needs to be searched used.
 * 
 * @author sumit
 * 
 */
public class SearchQueryInput {

	// these are manadatory input
	private String[] searchableField;
	private String[] serachQuery;
	private Occur[] queryFieldFlags;

	public Occur[] getQueryFieldFlags() {
		return queryFieldFlags;
	}

	public void setQueryFieldFlags(Occur[] queryFieldFlags) {
		this.queryFieldFlags = queryFieldFlags;
	}

	// these are required for pagination
	private int pageNumber;
	private int pageSize;

	// these are required for optimization
	private int maxSerachResultSize;

	// these are required for search-options details
	private SearchIndexOptions sercahIndexOption;

	public String[] getSearchableField() {
		return searchableField;
	}

	public void setSearchableField(String[] searchableField) {
		this.searchableField = searchableField;
	}

	public String[] getSerachQuery() {
		return serachQuery;
	}

	public void setSerachQuery(String[] serachQuery) {
		this.serachQuery = serachQuery;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getMaxSerachResultSize() {
		return maxSerachResultSize;
	}

	public void setMaxSerachResultSize(int maxSerachResultSize) {
		this.maxSerachResultSize = maxSerachResultSize;
	}

	public SearchIndexOptions getSercahIndexOption() {
		return sercahIndexOption;
	}

	public void setSercahIndexOption(SearchIndexOptions sercahIndexOption) {
		this.sercahIndexOption = sercahIndexOption;
	}

	// following constructor will take the important paraemeters
	public SearchQueryInput(String[] fields, String[] query,
			SearchIndexOptions searchIndexOptions) {
		this.searchableField = fields;
		this.serachQuery = query;
		this.sercahIndexOption = searchIndexOptions;
	}

}
