/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.browser;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import crawlerlucene.google.crawler4j.multithreaded.index.LaunchSearch;
import crawlerlucene.google.crawler4j.multithreaded.index.MainIndexerAndSearcher;

/**
 * 
 * This class is the simplistic browser , that can be used to display / view the
 * web page, and may be other supported format of the file.
 * 
 * @author sumit
 * 
 */
public class SimpleBrowser extends Region {

	public class LoadSerachWithPageNumber {
		private int pageNumber;

		public void setPageNumber(int pageNumber) {
			if (this.pageNumber != pageNumber) {
				System.out.println(pageNumber);
				String textSearch = SimpleBrowser.this.searhTextField.getText()
						.trim();
				fetchSerachResult(pageNumber, textSearch);
				this.pageNumber = pageNumber;
			}

		}

	}

	/**
	 * Controller of the web browser, with each button and text region
	 * facilities.
	 */
	private HBox toolBar = new HBox();
	private final Button searchButton = new Button("Search");
	private final TextField searhTextField = new TextField("");
	private final Button getLatestSearch = new Button("Refresh Search");
	private WebView mainWindow = new WebView();
	private WebEngine mainWebEngine = mainWindow.getEngine();

	private Application application;

	public WebView getMainWindow() {
		return mainWindow;
	}

	public WebEngine getMainWebEngine() {
		return mainWebEngine;
	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		VBox.setVgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	public SimpleBrowser(Application application) throws Exception {
		super();
		this.application = application;
		// getChildren().add(mainWindow);
		initializeUI();
	}

	private void initializeUI() throws Exception {
		// apply the styles
		getStyleClass().add("browser");

		mainWindow.setCache(true);
		mainWindow.setFocusTraversable(true);
		mainWindow.setManaged(true);
		mainWebEngine.setJavaScriptEnabled(true);

		mainWebEngine
				.setUserStyleSheetLocation(new URL(
						"file:///L:/ada assignments p503/algo projects doc/java-api/cms_technologies/style.css")
						.toExternalForm());

		// create the toolbar
		toolBar = new HBox();
		toolBar.setAlignment(Pos.CENTER);
		toolBar.getStyleClass().add("browser-toolbar");
		toolBar.getChildren().addAll(searhTextField, searchButton,
				getLatestSearch);
		toolBar.getChildren().add(createSpacer());
		// add components
		getChildren().add(toolBar);
		getChildren().add(mainWindow);

		// register all event handlers, for pop up displaying the result on the
		// clicking of the button.
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				System.out.println("serched text entered: "
						+ searhTextField.getText());
				fetchSerachResult(1, searhTextField.getText());

			}
		});

		getLatestSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				System.out.println("serched text entered: "
						+ searhTextField.getText());
				try {
					MainIndexerAndSearcher.closeIndexSearcher();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fetchSerachResult(1, searhTextField.getText());

			}
		});
		final LoadSerachWithPageNumber refreshSearchResultObj = new LoadSerachWithPageNumber();

		getMainWebEngine().getLoadWorker().stateProperty()
				.addListener(new ChangeListener<State>() {
					@Override
					public void changed(ObservableValue<? extends State> ov,
							State oldState, State newState) {
						// toolBar.getChildren().remove(showPrevDoc);
						if (newState == State.SUCCEEDED) {
							JSObject win = (JSObject) getMainWebEngine()
									.executeScript("window");
							win.setMember("app", refreshSearchResultObj);

						}
					}
				});

		getMainWebEngine().locationProperty().addListener(
				new DefaultFileDiasplayHandler(this.application));
		getMainWebEngine().setCreatePopupHandler(
				new DefaultPopupHandler(this.application, 0, 0));
	}

	public void loadUrl(String url) {
		mainWebEngine.load(url);
	}

	public void loadWithContent(String htmlContent) {
		mainWebEngine.loadContent(htmlContent);
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		double tbHeight = toolBar.prefHeight(w);
		layoutInArea(mainWindow, 0, 0, w, h - tbHeight, 0, HPos.CENTER,
				VPos.CENTER);
		layoutInArea(toolBar, 0, h - tbHeight, w, tbHeight, 0, HPos.CENTER,
				VPos.CENTER);
	}

	/**
	 * This method helps in fetching the result on the clicking of the web page
	 * button.
	 * 
	 * @param pageNumber
	 * @param textSearch
	 */
	private void fetchSerachResult(int pageNumber, String textSearch) {
		if (textSearch.trim().length() != 0) {
			try {
				SimpleBrowser.this.loadWithContent(LaunchSearch.search(
						new String[] { textSearch }, pageNumber));
			} catch (ParseException | IOException
					| InvalidTokenOffsetsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err
						.println("Cannot process the serach result for the rquest[qury , pageNumber]]::["
								+ textSearch + "," + pageNumber + "]");
			}
		}
	}

}
