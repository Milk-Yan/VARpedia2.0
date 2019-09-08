package main.java.application;



import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * Main class of the WikiApplication. Extends JavaFX Application.
 * Allows user to create and manage movie creations from Wikipedia
 * entries of the user's choice. Enjoy! 
 * @author Milk
 *
 */
@SuppressWarnings("rawtypes")
public class WikiApplication extends Application {
	
	private static WikiApplication _wikiApp;

	private Scene _currentScene;
	private Stage _primaryStage;
	
	// stored to use when creating creation
	private String _currentTerm;
	private String _chosenText;
	
	private MediaPlayer _currentPlayer;

	private Task _currentTask;
	private SearchTask _currentSearchTask;
	private ListView<String> _currentCreations;
	
	public WikiApplication() {
		_wikiApp = this;
	}
	
	protected static WikiApplication getInstance() {
		return _wikiApp;
	}

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
	 * method which calls the main menu to appear
	 */
	protected void displayMainMenu() {
		Scene mainMenuScene = new SceneMaker(SceneType.MainMenu).getScene();
		
		_currentScene = mainMenuScene;
		update();
	}


	protected void displayCreateScene() {

		Scene createScene = new SceneMaker(SceneType.Create).getScene();

		_currentScene = createScene;
		update();
	}

	protected void displayLoadingScene(Task task) {
		
		Scene loadingScene = new SceneMaker(SceneType.Loading).getScene();
		
		_currentScene = loadingScene;
		update();
		
	}
	
	protected Task getCurrentTask() {
		return _currentTask;
	}

	/**
	 * method which calls the view scene. called from MainController
	 */
	protected void displayViewScene() {
		
		Task<ListView<String>> viewTask = new ViewTask();
		
		new Thread(viewTask).start();
		
		displayLoadingScene(viewTask);
		
		viewTask.setOnSucceeded(succeededEvent -> {
			try {
				_currentCreations = viewTask.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Scene viewScene = new SceneMaker(SceneType.View).getScene();
			
			_currentScene = viewScene;
			update();
		});
		
	}
	
	protected ListView<String> getCurrentCreations() {
		return _currentCreations;
	}

	protected void displayNamingScene() {
		
		Scene namingScene = new SceneMaker(SceneType.Naming).getScene();
		
		_currentScene = namingScene;
		update();
	}
	
	protected void setChosenText(String chosenText) {
		_chosenText = chosenText;
	}
	

	
	protected String getCurrentTerm() {
		return _currentTerm;
	}
	
	protected String getChosenText() {
		return _chosenText;
	}
	
	protected void displaySearchResultsScene(String term) {
		
		// yeah this could probably be put in searchResultsController sometime.
		_currentTerm = term;
		
		SearchTask searchTask = new SearchTask(term);
		_currentSearchTask = searchTask;
		
		new Thread(searchTask).start();
		
		searchTask.setOnSucceeded(succeededevent -> {
			Scene searchResultsScene = new SceneMaker(SceneType.SearchResults).getScene();
			
			_currentScene = searchResultsScene;
			update();
		});
		
	}

	protected SearchTask getCurrentSearchTask() {
		return _currentSearchTask;
	}


	/**
	 * Update the primary stage to show the current scene.
	 */
	private void update() {
		_primaryStage.setScene(_currentScene);
		_primaryStage.show();
	}


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
		System.out.println(Paths.get("bin/creations/" + name + ".mp4").toUri().toString());
		MediaPlayer player = new MediaPlayer(video);
		player.setAutoPlay(true);
		
		_currentPlayer = player;
	}
	

	


	/**
	 * Initialises folders if they do not already exist.
	 */
	private void createFolders() {
		new File("./bin/creations").mkdirs();
		new File("./bin/videos").mkdirs();
		new File("./bin/audio").mkdirs();
	}


}
