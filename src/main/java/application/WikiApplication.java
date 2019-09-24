package main.java.application;

import java.io.File;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

// controllers
import main.java.controllers.CreateAudioNamingController;
import main.java.controllers.CreateAudioPreviewController;
import main.java.controllers.CreateAudioSearchResultsController;
import main.java.controllers.CreateCreationChooseAudioController;
import main.java.controllers.CreateCreationCreateSlideshowController;
import main.java.controllers.CreateCreationNamingController;
import main.java.controllers.LoadingCreateAudioController;
import main.java.controllers.LoadingCreateCreationController;
import main.java.controllers.LoadingSearchResultsController;
import main.java.controllers.LoadingViewCreationsController;
import main.java.controllers.VideoPlayerController;
import main.java.controllers.ViewCreationsController;
// tasks
import main.java.tasks.CreateAudioTask;
import main.java.tasks.CreateCreationTask;
import main.java.tasks.SearchTermTask;
import main.java.tasks.ViewCreationsTask;


/**
 * Main class of the WikiApplication. Extends JavaFX Application.
 * Allows user to create and manage movie creations from Wikipedia
 * entries of the user's choice. Enjoy! 
 * @author Milk, OverCry
 *
 */
public class WikiApplication extends Application {

	private Scene _currentScene;
	private Stage _primaryStage;

	// -----------------------------------------------------------------------------------
	// -----------------------------INITIALISATION----------------------------------------
	// -----------------------------------------------------------------------------------

	public static void main (String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		_primaryStage = primaryStage;
		primaryStage.setTitle("Wiki Application");

		createFolders();
		displayMainMenuScene();

	}

	/**
	 * Initialises folders if they do not already exist.
	 */
	private void createFolders() {
		String s = File.separator;
		new File(System.getProperty("user.dir") + s + "bin" + s + "creations").mkdirs();
		new File(System.getProperty("user.dir") + s + "bin" + s + "audio").mkdirs();
	}

	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (MAIN)---------------------------------------
	// -----------------------------------------------------------------------------------

	public void displayMainMenuScene() {
		Scene mainMenuScene = new SceneMaker(SceneType.MainMenu, this).getScene();

		_currentScene = mainMenuScene;
		update();
	}

	public void displayPreviousScene(Scene previousScene) {

		_currentScene = previousScene;
		update();
	}

	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (AUDIO)--------------------------------------
	// -----------------------------------------------------------------------------------
	
	public void displayCreateAudioSearchScene() {

		_currentScene = new SceneMaker(SceneType.CreateAudioSearch, this).getScene();
		update();
	}

	public void displayLoadingSearchResultsScene(SearchTermTask task) {

		SceneMaker loadingSceneMaker = new SceneMaker(SceneType.LoadingSearchResults, this);

		// get the loading search results controller so that the search task can be
		// passed in
		LoadingSearchResultsController controller = (LoadingSearchResultsController) loadingSceneMaker.getController();
		controller.setTask(task);

		_currentScene = loadingSceneMaker.getScene();
		update();

	}

	public void displayCreateAudioSearchResultsScene(String term, String searchResults) {

		// get the loading search results controller so that the term and 
		// searchResults can be passed in
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioSearchResults, this);

		CreateAudioSearchResultsController controller = (CreateAudioSearchResultsController) sceneMaker.getController();
		controller.setUp(term, searchResults);

		_currentScene = sceneMaker.getScene();
		update();
	}

	public void displayCreateAudioNamingScene(String term, String chosenText, String voice) {

		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioNaming, this);
		CreateAudioNamingController controller = (CreateAudioNamingController) sceneMaker.getController();
		controller.setUp(term, chosenText, voice);

		_currentScene = sceneMaker.getScene();
		update();
	}

	public void displayLoadingCreateAudioScene(CreateAudioTask task) {
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingCreateAudio, this);
		LoadingCreateAudioController controller = (LoadingCreateAudioController) sceneMaker.getController();
		controller.setTask(task);

		_currentScene = sceneMaker.getScene();
		update();
	}

	public void displayCreateAudioPreviewScene(String term, String previewText, Scene searchResultsScene) {

		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioPreview, this);
		CreateAudioPreviewController controller = (CreateAudioPreviewController) sceneMaker.getController();
		controller.setUp(term, previewText, searchResultsScene);

		_currentScene = sceneMaker.getScene();
		update();
	}


	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (CREATION)-----------------------------------
	// -----------------------------------------------------------------------------------
	
	public void displayCreateCreationSearchScene() {
		
		_currentScene = new SceneMaker(SceneType.CreateCreationSearch, this).getScene();
		update();
	
	}
	
	public void displayCreateCreationChooseAudioScene(String term) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationChooseAudio, this);
		CreateCreationChooseAudioController controller = (CreateCreationChooseAudioController) sceneMaker.getController();
		controller.setUp(term);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	public void displayCreateCreationCreateSlideshowScene(String term, ObservableList<String> audioList) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationCreateSlideshow, this);
		CreateCreationCreateSlideshowController controller = (CreateCreationCreateSlideshowController) sceneMaker.getController();
		controller.setUp(term, audioList);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	public void displayLoadingCreateCreationScene(CreateCreationTask task) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingCreateCreation, this);
		LoadingCreateCreationController controller = (LoadingCreateCreationController) sceneMaker.getController();
		controller.setTask(task);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	public void displayCreateCreationNamingScene(String term, ObservableList<String> audioList, int imageNumber) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationNaming, this);
		CreateCreationNamingController controller = (CreateCreationNamingController) sceneMaker.getController();
		controller.setUp(term, audioList, imageNumber);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	public void displayLoadingViewCreationsScene(ViewCreationsTask task) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingViewCreations, this);
		LoadingViewCreationsController controller = (LoadingViewCreationsController) sceneMaker.getController();
		controller.setTask(task);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (VIEW)---------------------------------------
	// -----------------------------------------------------------------------------------
	
	public void displayViewCreationsScene() {

		SceneMaker sceneMaker = new SceneMaker(SceneType.ViewCreations, this);
		ViewCreationsController controller = (ViewCreationsController) sceneMaker.getController();
		controller.setUp();
		
		_currentScene = sceneMaker.getScene();
		update();

	}

	/**
	 * Creates a new stage and plays the video.
	 * @param name
	 */
	public void playVideo(String name) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.VideoPlayer, this);
		VideoPlayerController controller = (VideoPlayerController) sceneMaker.getController();
		controller.setUp(name);
		
		
	}

	// -----------------------------------------------------------------------------------
	// -----------------------------HELPER METHODS----------------------------------------
	// -----------------------------------------------------------------------------------

	/**
	 * Update the primary stage to show the current scene.
	 */
	private void update() {
		_primaryStage.setScene(_currentScene);
		_primaryStage.show();
	}

}
