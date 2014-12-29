/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.browser;

import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * This class is used to view as simple browser. It first call to get load or
 * called for starting the web browser.
 * 
 * @author sumit
 * 
 */
public class WebViewLaunchForCustomContent extends Application {

	private final SimpleBrowser smplBrowser;

	public WebViewLaunchForCustomContent() throws Exception {
		super();
		smplBrowser = new SimpleBrowser(this);
	}

	/*
	 * This is called by the super class at runtime.It deals with the displaying
	 * of the ui. on load. It handles all event and register with the
	 * initialization.
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage pStage) throws Exception {

		// create the scene
		pStage.setTitle("Text Based Serach Engine");

		final Scene scene = new Scene(smplBrowser);// new
		// Scene(VBoxBuilder.create().spacing(0).children(webMainWindow).build());
		// new Scene(brwsr, 750, 500, Color.web("#666970"));
		// VBox.setVgrow(stackPaneRoot, Priority.ALWAYS);
		// HBox.setHgrow(stackPaneRoot, Priority.ALWAYS);

		pStage.setScene(scene);

		scene.getStylesheets()
				.add(new URL(
						"file:///L:/ada%20assignments%20p503/algo%20projects%20doc/java-api/cms_technologies/BrowserToolbar.css")
						.toExternalForm());

		// scene.getStylesheets().add("overflow: scroll");
		int lengthOfParameter = getParameters().getRaw().size();

		if (lengthOfParameter < 1) {
			smplBrowser.getMainWebEngine().load("http://google.com");
		} else {
			// webEngine.load("file:///C:/Users/sumit/Downloads/cms_technologies/index.html");
			smplBrowser.getMainWebEngine().loadContent(
					getParameters().getRaw().get(0));
		}

		pStage.show();
		// Platform.exit();
	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	public void run(String... args) {
		// System.out.println(Thread.currentThread().getName());
		Application.launch(args);
	}
}

// win.setMember("app", new
// LoadSerachWithPageNumber());
// if (needDocumentationButton) {
// toolBar.getChildren().add(showPrevDoc);
// }

// Document doc = webEngine.getDocument();
// Element el=doc.getElementById("content");
// el.setNodeValue(getParameters().getRaw().get(0));
// System.out.println(getParameters().getRaw().get(0));
// webEngine.executeScript("document.getElementById('content').innerHTML = "+getParameters().getRaw().get(0));
// webEngine.executeScript("document.getElementById('content').innerHTML = "+"\"<h2><a href='#'>abc.txt</a></h2>");
// webEngine.executeScript("updateSearchResult('<h2><a href=\"#\">abc.txt</a></h2>')");
// webEngine.executeScript("updateSearchResult("+getParameters().getRaw().get(0)+")");
// webEngine.loadContent(getParameters().getRaw().get(0));
