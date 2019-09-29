package main.java.application;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllers.CreateAudioChooseTextController;
// controllers
import main.java.controllers.CreateAudioNamingController;
import main.java.controllers.CreateCreationChooseAudioController;
import main.java.controllers.CreateCreationChooseImagesController;
import main.java.controllers.CreateCreationNamingController;
import main.java.controllers.LoadingCreateAudioController;
import main.java.controllers.LoadingCreateCreationController;
import main.java.controllers.LoadingScrapingImagesController;
import main.java.controllers.LoadingSearchResultsController;
import main.java.controllers.LoadingViewCreationsController;
import main.java.controllers.VideoPlayerController;
import main.java.controllers.ViewCreationsController;
// tasks
import main.java.tasks.CreateAudioTask;
import main.java.tasks.CreateCreationTask;
import main.java.tasks.GetImagesTask;
import main.java.tasks.SearchTermTask;
import main.java.tasks.ViewAudioTask;
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
	private Scene _audioWikitScene;
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

	/**
	 * Set the current scene back to the main menu
	 */
	public void displayMainMenuScene() {
		cleanUpTempFiles();
		Scene mainMenuScene = new SceneMaker(SceneType.MainMenu, this).getScene();

		_currentScene = mainMenuScene;
		update();
	}

	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (AUDIO)--------------------------------------
	// -----------------------------------------------------------------------------------
	
	/**
	 * Change the scene to search for a wikit term
	 */
	public void displayCreateAudioSearchScene() {

		_currentScene = new SceneMaker(SceneType.CreateAudioSearch, this).getScene();
		update();
	}

	/**
	 * Change the search scene to show a loading animation
	 * @param task
	 */
	public void displayLoadingSearchResultsScene(SearchTermTask task) {

		SceneMaker loadingSceneMaker = new SceneMaker(SceneType.LoadingSearchResults, this);

		// get the loading search results controller so that the search task can be
		// passed in
		LoadingSearchResultsController controller = (LoadingSearchResultsController) loadingSceneMaker.getController();
		controller.setTask(task);

		_currentScene = loadingSceneMaker.getScene();
		update();
	}

	/**
	 * Set scene to show the displays of search results as well as editing editing scene
	 * @param term
	 * @param searchResults
	 */
	public void displayCreateAudioChooseTextScene(String term, String searchResults) {

		// get the loading search results controller so that the term and 
		// searchResults can be passed in
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioChooseText, this);

		CreateAudioChooseTextController controller = (CreateAudioChooseTextController) sceneMaker.getController();
		controller.setUp(term, searchResults);

		_currentScene = sceneMaker.getScene();
		//test might not work
		
		_audioWikitScene=_currentScene;
		update();
		
	}

	/**
	 * Set scene to allow the audio creation to be named
	 * @param term
	 * @param chosenText
	 * @param voice
	 */
	public void displayCreateAudioNamingScene(String term, String chosenText, String voice) {

		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateAudioNaming, this);
		CreateAudioNamingController controller = (CreateAudioNamingController) sceneMaker.getController();
		controller.setUp(term, chosenText, voice);

		_currentScene = sceneMaker.getScene();
		update();
	}

	/**
	 * Display loading scene to show the user the audio is being made
	 * @param task
	 */
	public void displayLoadingCreateAudioScene(CreateAudioTask task) {
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingCreateAudio, this);
		LoadingCreateAudioController controller = (LoadingCreateAudioController) sceneMaker.getController();
		controller.setTask(task);

		_currentScene = sceneMaker.getScene();
		update();
	}


	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (CREATION)-----------------------------------
	// -----------------------------------------------------------------------------------
	
	/**
	 * Set scene to show a list of existing wikit searches
	 */
	public void displayCreateCreationSearchScene() {

		_currentScene = new SceneMaker(SceneType.CreateCreationSearch, this).getScene();
		update();
	
	}
	
	/**
	 * Set scene to show all existing audio files of the wikit search
	 * @param term
	 */
	public void displayCreateCreationChooseAudioScene(String term) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationChooseAudio, this);
		CreateCreationChooseAudioController controller = (CreateCreationChooseAudioController) sceneMaker.getController();
		controller.setUp(term);
		
		_currentScene = sceneMaker.getScene();
		update();
	}

	/**
	 * Display loading animation to show photos are being loaded
	 * @param term
	 * @param task
	 */
	public void displayLoadingScrapingImagesScene(String term, GetImagesTask task) {
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingScrapingImages, this);
		LoadingScrapingImagesController controller = (LoadingScrapingImagesController) sceneMaker.getController();
		controller.setTask(task, term);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	/**
	 * display loading animation, informing user the creation is being made
	 * @param task
	 */
	public void displayLoadingCreateCreationScene(CreateCreationTask task) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingCreateCreation, this);
		LoadingCreateCreationController controller = (LoadingCreateCreationController) sceneMaker.getController();
		controller.setTask(task);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	/**
	 * Set scene to allow creation to be named
	 * @param term
	 * @param audioList
	 * @param imageList
	 */
	public void displayCreateCreationNamingScene(String term, ArrayList<String> audioList, ArrayList<String> imageList) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationNaming, this);
		CreateCreationNamingController controller = (CreateCreationNamingController) sceneMaker.getController();
		controller.setUp(term, audioList, imageList);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	/**
	 * Create loading scene to inform user all creations are being displayed
	 * @param creationTask
	 * @param audioTask
	 */
	public void displayLoadingViewCreationsScene(ViewCreationsTask creationTask, ViewAudioTask audioTask) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.LoadingViewCreations, this);
		LoadingViewCreationsController controller = (LoadingViewCreationsController) sceneMaker.getController();
		controller.setTask(creationTask, audioTask);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	/**
	 * Set seen to display images relevant to the search term
	 * @param term
	 * @param audioList
	 */
	public void displayCreateCreationChooseImagesScene(String term, ArrayList<String> audioList) {
		
		SceneMaker sceneMaker = new SceneMaker(SceneType.CreateCreationChooseImages, this);
		CreateCreationChooseImagesController controller = (CreateCreationChooseImagesController) sceneMaker.getController();
		controller.setUp(term, audioList);
		
		_currentScene = sceneMaker.getScene();
		update();
	}
	
	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY METHODS (VIEW)---------------------------------------
	// -----------------------------------------------------------------------------------
	
	/**
	 * set seen to show the creations the user has created up to this point
	 */
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
	
	/**
	 * clean up any temporary files created previously
	 */
	private void cleanUpTempFiles() {
		File tempImagesFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
									+ "tempImages");
		File tempAudioFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
									+ "tempAudio");
		File tempVideoFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
									+ "tempVideo");
		
		if (tempImagesFolder.exists()) {
			for (File imageFolder:tempImagesFolder.listFiles()) {
				for (File image:imageFolder.listFiles()) {
					image.delete();
				}
				imageFolder.delete();
			}
			tempImagesFolder.delete();
		}
		
		if (tempAudioFolder.exists()) {
			for (File audioFolder:tempAudioFolder.listFiles()) {
				for (File audio:audioFolder.listFiles()) {
					audio.delete();
				}
				audioFolder.delete();
			}
			tempAudioFolder.delete();
		}
		
		if (tempVideoFolder.exists()) {
			for (File videoFolder:tempVideoFolder.listFiles()) {
				for (File video:videoFolder.listFiles()) {
					video.delete();
				}
				videoFolder.delete();
			}
			tempVideoFolder.delete();
		}
	}

	// -----------------------------------------------------------------------------------
	// ----------------------DISPLAY PAST SCENES -----------------------------------------
	// -----------------------------------------------------------------------------------

	/**
	 * Set scene back to original wikit search display
	 */
	public void setAudioScene(){
		_currentScene = _audioWikitScene;
		update();
	}
}
