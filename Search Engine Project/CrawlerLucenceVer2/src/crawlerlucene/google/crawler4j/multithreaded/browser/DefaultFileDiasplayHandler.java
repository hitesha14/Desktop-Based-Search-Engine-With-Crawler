package crawlerlucene.google.crawler4j.multithreaded.browser;

import java.net.MalformedURLException;
import java.net.URL;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;
import crawlerlucene.google.crawler4j.multithreaded.util.FilterUrlHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * This class is a helper to the mini small browser. It helps in displaying all
 * the files on the Internet to be viewable by any registered application on the
 * system, that user is using currently. It mostly redirects the output to the
 * installed browser.
 * 
 * @author sumit
 * 
 */
public class DefaultFileDiasplayHandler implements ChangeListener<String> {

	Application application;

	public DefaultFileDiasplayHandler(Application applicationObj) {
		if (applicationObj == null) {
			throw new IllegalArgumentException(
					"Invalid Application object is passed");
		}
		this.application = applicationObj;
	}

	/**
	 * This method is get called through the web engine , and it should be
	 * registered to the event handler of the web view.
	 */
	@Override
	public void changed(ObservableValue<? extends String> ObservlProprtyObj,
			String oldSrc, String newSrc) {

		// is file a viewable by html, then dont do any handling, otherwise call
		// default system dependent handler
		String url = "http://";

		try {
			url = new URL(newSrc).getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return;
		}
		int i = url.lastIndexOf('.');
		int p = Math.max(url.lastIndexOf('/'), url.lastIndexOf('\\'));

		String fileExtension = i > p ? url.substring(i + 1) : "";
		// is url is kinda html then do anything , else call system dependent
		if (fileExtension.length() != 0
				&& !FilterUrlHandler.verifyFilterForPattern(
						IndexConstants.PATTERN_URL_ONLY_HTML, fileExtension)) {
			this.application.getHostServices().showDocument(newSrc);
		}

	}
}
