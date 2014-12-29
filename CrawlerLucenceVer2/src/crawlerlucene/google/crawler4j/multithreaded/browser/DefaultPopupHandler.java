/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.browser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * 
 * This class when registered to the event handler of the class, the web view is
 * able to display the result through the web engine o anoher window, basically
 * through the pop up window.
 * 
 * @author sumit
 * 
 */
public class DefaultPopupHandler implements Callback<PopupFeatures, WebEngine> {

	private Application application;
	private double x;
	private double y;

	/**
	 * 
	 * @param applicationObj
	 */
	public DefaultPopupHandler(Application applicationObj, double x, double y) {
		if (applicationObj == null) {
			throw new IllegalArgumentException(
					"Invalid Application object is passed");
		}
		this.application = applicationObj;
		this.x = x <= 0 ? 500 : x;
		this.y = y <= 0 ? 500 : y;
	}

	/**
	 * 
	 * @param popUpFeatures
	 */
	public WebEngine call(PopupFeatures popUpFeatures) {
		Stage stage = new Stage(StageStyle.DECORATED);
		WebView wv2 = new WebView();
		stage.setScene(new Scene(wv2));
		wv2.setPrefSize(this.x, this.y);
		wv2.getEngine().locationProperty()
				.addListener(new DefaultFileDiasplayHandler(this.application));
		wv2.getEngine().setCreatePopupHandler(
				new DefaultPopupHandler(this.application, x, y));
		stage.show();
		return wv2.getEngine();
	}

}
