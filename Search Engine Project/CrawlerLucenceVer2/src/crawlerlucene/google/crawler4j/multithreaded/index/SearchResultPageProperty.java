package crawlerlucene.google.crawler4j.multithreaded.index;

public class SearchResultPageProperty {

	
	private int pageSize;
	private int maxFragmentNumber;
	
	private int fragmentSeprator;
	
	public int getFragmentSeprator() {
		return fragmentSeprator;
	}

	public void setFragmentSeprator(int fragmentSeprator) {
		this.fragmentSeprator = fragmentSeprator;
	}

	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getMaxFragmentNumber() {
		return maxFragmentNumber;
	}

	public void setMaxFragmentNumber(int maxfragmentNumber) {
		this.maxFragmentNumber = maxfragmentNumber;
	}

		
	
	public SearchResultPageProperty() {
		// TODO Auto-generated constructor stub
	}

}
