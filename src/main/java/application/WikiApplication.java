package main.java.application;



import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import main.java.controllers.CreateAudioNamingController;
import main.java.controllers.CreateAudioPreviewController;
import main.java.controllers.CreateAudioSearchResultsController;
import main.java.controllers.CreateCreationChooseAudioController;
import main.java.controllers.CreateCreationCreateSlideshowController;
import main.java.controllers.CreateCreationNamingController;
import main.java.controllers.LoadingCreateAudioController;
import main.java.controllers.LoadingCreateCreationController;
import main.java.controllers.LoadingSearchResultsController;
import main.java.tasks.CreateAudioTask;
import main.java.tasks.CreateCreationTask;
import main.java.tasks.SearchTermTask;


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

	// stored to use when creating creation
	private String _currentTerm;
	private String _chosenText;
	private String _currentPreviewText;

	private MediaPlayer _currentPlayer;

	private ListView<String> _currentCreations;

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
		displayMainMenu();

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

	public void displayMainMenu() {
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

	public void displayCreateAudioNamingScene(String term, String chosenText) {

		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioNaming, this);
		CreateAudioNamingController controller = (CreateAudioNamingController) sceneMaker.getController();
		controller.setUp(term, chosenText);

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
	
	public void displayCreateCreationsNaming(String term, ObservableList<String> audioList) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationNaming, this);
		CreateCreationNamingController controller = (CreateCreationNamingController) sceneMaker.getController();
		controller.setUp(term, audioList);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (VIEW)---------------------------------------
	// -----------------------------------------------------------------------------------
	
	public void displayViewScene() {

//		Task<ListView<String>> viewTask = new ViewTask();
//
//		new Thread(viewTask).start();
//
//		displayLoadingScene(viewTask);
//
//		viewTask.setOnSucceeded(succeededEvent -> {
//			try {
//				_currentCreations = viewTask.get();
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Scene viewScene = new SceneMaker(SceneType.View).getScene();
//
//			_currentScene = viewScene;
//			update();
//		});

	}





	// -----------------------------------------------------------------------------------
	// ---------------------------------GETTERS-------------------------------------------
	// -----------------------------------------------------------------------------------



	protected ListView<String> getCurrentCreations() {
		return _currentCreations;
	}



	protected String getCurrentPreviewText() {
		return _currentPreviewText;
	}

	// -----------------------------------------------------------------------------------
	// ---------------------------------SETTERS-------------------------------------------
	// -----------------------------------------------------------------------------------

	public void setChosenText(String chosenText) {
		_chosenText = chosenText;
	}



	protected String getCurrentTerm() {
		return _currentTerm;
	}

	protected void setCurrentTerm(String term) {
		_currentTerm = term;
	}

	protected String getChosenText() {
		return _chosenText;
	}





	/**
	 * Update the primary stage to show the current scene.
	 */
	private void update() {
		_primaryStage.setScene(_currentScene);
		_primaryStage.show();
	}

	// -----------------------------------------------------------------------------------
	// ---------------------------------REFACTOR------------------------------------------
	// -----------------------------------------------------------------------------------

	/**
	 * Creates the stage for a new creation to play.
	 * @param name
	 */
	protected void playVideo(String name) {

		// could potentially make a new VideoPlayer class...but not today
		createPlayer(name);
		MediaPlayer player = _currentPlayer;

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(SceneType.VideoPlayer.getAddress()));

		try {
			Parent layout = loader.load();
			Scene scene = new Scene(layout);
			Stage videoStage = new Stage();
			videoStage.setScene(scene);
			videoStage.show();

			// Media functionality
			player.setOnEndOfMedia(() -> {
				player.dispose();
				videoStage.close();
			});

			videoStage.setOnCloseRequest(closeEvent -> {
				player.stop();
			});



		} catch (IOException e) {
			new AlertMaker(AlertType.ERROR, "IOException", "Oops", "Something wrong happened when making the scene. Sorry :(");
			displayMainMenu();
		}

	}

	protected MediaPlayer getCurrentPlayer() {

		return _currentPlayer;
	}

	private void createPlayer(String name) {
		Media video = new Media(Paths.get("bin/creations/" + name + ".mp4").toUri().toString());
		MediaPlayer player = new MediaPlayer(video);
		player.setAutoPlay(true);

		_currentPlayer = player;
	}







}
