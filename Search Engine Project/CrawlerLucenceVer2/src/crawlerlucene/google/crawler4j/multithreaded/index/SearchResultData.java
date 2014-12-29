package crawlerlucene.google.crawler4j.multithreaded.index;

import java.io.Serializable;

import org.apache.lucene.search.highlight.TextFragment;

public class SearchResultData implements Serializable {
        //contet fragmennter
		private TextFragment[] fragments;
        private String filename;
        private String title;
       
		private double scoreOfDocResult;
        
        public double getScoreOfDocResult() {
			return scoreOfDocResult;
		}
		public void setScoreOfDocResult(double scoreOfDocResult) {
			this.scoreOfDocResult = scoreOfDocResult;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public TextFragment[] getFragments() {
                return fragments;
        }
        public void setFragments(TextFragment[] fragments) {
                this.fragments = fragments;
        }
        public String getFilename() {
                return filename;
        }
        public void setFilename(String filename) {
                this.filename = filename;
        }
}