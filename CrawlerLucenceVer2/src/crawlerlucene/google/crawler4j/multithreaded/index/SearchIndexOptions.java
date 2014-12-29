/*
 * Copyright 2014 Ana Maria Mihalceanu.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crawlerlucene.google.crawler4j.multithreaded.index;

import org.apache.lucene.analysis.Analyzer;

/**
 * This class contains options for searching and indexing within this project.
 * 
 * @author AnaMihalceanu
 */
public class SearchIndexOptions {

	private Analyzer analyzer;
	private Float boost;
	private Boolean score;
	private Boolean defaultStopWords;

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Float getBoost() {
		return boost;
	}

	public void setBoost(Float boost) {
		this.boost = boost;
	}

	public Boolean getScore() {
		return score;
	}

	public void setScore(Boolean score) {
		this.score = score;
	}

	public Boolean isDefaultStopWords() {
		return defaultStopWords;
	}

	public void setDefaultStopWords(Boolean defaultStopWords) {
		this.defaultStopWords = defaultStopWords;
	}

	@Override
	public String toString() {
		return "SearchIndexOptions [analyzer=" + analyzer + ", boost=" + boost
				+ ", score=" + score + ", defaultStopWords=" + defaultStopWords
				+ "]";
	}

}
