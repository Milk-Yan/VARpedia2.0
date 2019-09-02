package application;



import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Main class of the WikiApplication. Extends JavaFX Application.
 * Allows user to create and manage movie creations from Wikipedia
 * entries of the user's choice. Enjoy! 
 * @author Milk
 *
 */
public class WikiApplication extends Application {

	public enum SceneType {MainMenu, Create, View, Loading, Naming};

	private Scene _currentScene;
	private Stage _primaryStage;
	private Scene _mainMenuScene; 
	private Scene _createScene;
	private Scene _viewScene;
	private Scene _loadingScene;
	private Scene _namingScene;

	private SearchTask _searchTask;
	private CreateTask _createTask;
	private String _currentName;
	private String _currentTerm;
	private String _currentText;
	private int _currentLineNumber;

	public static void main (String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		_primaryStage = primaryStage;
		primaryStage.setTitle("Wiki Application");

		createFolders();
		initialiseScenes();
		displayMainMenu();

	}

	private void displayMainMenu() {
		_currentScene = _mainMenuScene;
		update();
	}

	private void displayCreateMenu() {
		_currentScene = _createScene;
		update();
	}

	private void displayLoadingScene(Task<Void> task) {
		createLoading(task);
		_currentScene = _loadingScene;
		update();
	}

	private void displayViewScene() {
		refreshViewScene();
		_currentScene = _viewScene;
		update();
	}

	private void displayNamingScene() {
		_currentScene = _namingScene;
		update();
	}

	/**
	 * Searches for the Wikipedia term using wikit. The search itself is implemented
	 * on a new thread to allow concurrency. 
	 * @param searchTerm The term the user wants to search for.
	 */
	private void searchTerm(String searchTerm) {

		_searchTask = new SearchTask(searchTerm);

		// displays loading screen to user so they can exit to main menu whenever they want.
		displayLoadingScene(_searchTask);

		new Thread(_searchTask).start();

		_searchTask.setOnCancelled(cancelledEvent -> {
			if (_searchTask.isInvalid()) {
				new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid term", "This term cannot be found. Please try again.");
			}
			displayCreateMenu();
		});

		_searchTask.setOnSucceeded(succeededEvent -> {
			// displays results of the search
			TextArea outputText = new TextArea();
			outputText.setEditable(false);
			outputText.textProperty().bind(_searchTask.messageProperty());

			Text message = new Text("How many sentences would you like to include in your creation (1-" + _searchTask.lineCount() + ")?");
			Button btnCreate = new Button("Create");
			Button btnMainMenu = new Button("Quit to main menu");

			TextField input = new TextField();

			input.setMaxWidth(50);
			message.setWrappingWidth(250);
			message.setTextAlignment(TextAlignment.CENTER);
			outputText.setWrapText(true);

			_currentScene = createScene(outputText,message,input,btnCreate,btnMainMenu);
			update();

			// functionality of buttons
			btnMainMenu.setOnAction(event -> {displayMainMenu();});
			btnCreate.setOnAction(event -> {
				// displays naming scene
				if (isNumeric(input.getText()) && 
						Integer.parseInt(input.getText()) > 0 &&
						Integer.parseInt(input.getText()) <= _searchTask.lineCount()){

					_currentTerm = searchTerm;
					_currentLineNumber = Integer.parseInt(input.getText());
					_searchTask.setLineNumber(_currentLineNumber);
					_currentText = _searchTask.getChosenText();

					displayNamingScene();

				} else {
					new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid value", "Please enter an integer between 1-"+_searchTask.lineCount());
					input.clear();
				}

			});
		});

	}

	/**
	 * Private method to help with the creation of createTask objects.
	 */
	private void create() {

		_createTask = new CreateTask(_currentName, _currentTerm, _currentText, _currentLineNumber);
		new Thread(_createTask).start();

		_createTask.setOnRunning(runningEvent -> {
			displayLoadingScene(_createTask);
		});
		_createTask.setOnSucceeded(succeededEvent -> {
			new AlertMaker(AlertType.INFORMATION, "Completed", "Creation completed", "Press OK to exit to the main menu.");
			displayMainMenu();
		});
		_createTask.setOnCancelled(cancelledEvent -> {
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "The input is valid.");
			displayNamingScene();
		});
	}

	/**
	 * Private method to check if the input is numeric.
	 * @param text The input text, normally entered by user.
	 * @return True if numeric, false otherwise.
	 */
	private boolean isNumeric(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Private method to update the primary stage to show the 
	 * current scene.
	 */
	private void update() {
		_primaryStage.setScene(_currentScene);
		_primaryStage.show();
	}

	/**
	 * Creates a ListView of current creations. The search for
	 * current creations is implemented on a different thread to
	 * allow concurrency.
	 * @return ListView<String> of current creations.
	 */
	private ListView<String> viewCreations() {

		ViewTask viewTask = new ViewTask();
		new Thread(viewTask).start();
		try {
			return viewTask.get();
		} catch (InterruptedException | ExecutionException e) {

			return null;
		}

	}

	/**
	 * Initialises the main menu scene that is consistent throughout
	 * the session.
	 */
	private void initialiseMainMenu() {

		// Node creation
		Text welcomeMessage = new Text("Welcome to the Wiki-Speak Authoring Tool!");
		Text instructionMessage = new Text("This tool makes video creations of terms "
				+ "from Wikipedia.");
		Text enquiryMessage = new Text("Please select from the following options:");

		Button btnCreate = new Button("Create new creation");
		Button btnView = new Button("View current creations");

		// button functionality
		btnCreate.setOnAction(event -> {displayCreateMenu();});
		btnView.setOnAction(event -> {displayViewScene();});

		_mainMenuScene = createScene(welcomeMessage, instructionMessage, enquiryMessage, btnCreate, btnView);

	}

	/**
	 * Initialises the create scene that is consistent throughout
	 * the session.
	 */
	private void initialiseCreate() {

		// Node creation
		Text termEnquiryMessage = new Text("What term would you like to search for?");
		TextField txtInput = new TextField();

		Button btnSearch = new Button("Search");
		Button btnMainMenu = new Button("Quit to Main Menu");

		// Button functionality
		btnSearch.setOnAction(event -> {
			searchTerm(txtInput.getText());
			txtInput.clear();
		});

		btnMainMenu.setOnAction(event -> {
			txtInput.clear();
			displayMainMenu();
		});

		_createScene = createScene(termEnquiryMessage, txtInput, btnSearch, btnMainMenu);


	}

	/**
	 * Initialises the loading scene that is consistent throughout
	 * the session.
	 */
	private void createLoading(Task<Void> task) {

		// Node creation
		Text loadingMessage = new Text("Please wait. Now loading...");
		ProgressIndicator progressIndicator = new ProgressIndicator();
		Button btnInterruptMainMenu = new Button("Quit to Main Menu");

		// Button functionality
		btnInterruptMainMenu.setOnAction(event -> {
			if (_searchTask != null && !_searchTask.isCancelled()) {
				_searchTask.cancel();
			}
			if (_createTask != null && !_createTask.isCancelled()) {
				_createTask.destroy();
			}

			displayMainMenu();
		});

		_loadingScene = createScene(loadingMessage, progressIndicator, btnInterruptMainMenu);
	}

	private void initialiseNaming() {

		// Node creation
		Text namingMessage = new Text("Enter the name of the new creation");
		TextField nameInput = new TextField();

		Button btnName = new Button("Enter");
		Button btnBackMainMenu = new Button("Quit to Main Menu");

		// Button functionality
		btnName.setOnAction(nameEvent -> {
			String input = nameInput.getText();
			nameInput.clear();
			if (new File("./bin/creations/" + input + ".mp4").isFile()) {
				Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "File already exists",
						"Would you like to overwrite the existing file?").getAlert();
				if (alert.getResult() == ButtonType.OK) {
					
					_currentName = input;
					create();
				}
			} else if (input.isEmpty()) {
				new AlertMaker(AlertType.ERROR, "Error encountered", "Empty value",
						"The name cannot be empty");
			} else if (input.contains(" ")) {
				new AlertMaker(AlertType.ERROR, "Error encountered", "Space present", "Name cannot contain space(s)");
			} else {
				_currentName = input;
				create();
			}
		});

		btnBackMainMenu.setOnAction(mainMenuEvent -> {displayMainMenu();});

		_namingScene = createScene(namingMessage, nameInput, btnName, btnBackMainMenu);
	}


	/**
	 * Initialises the consistent scenes. These scenes are constant throughout 
	 * the application and only require initialising at startup.
	 */
	private void initialiseScenes() {
		initialiseMainMenu();
		initialiseCreate();
		initialiseNaming();
	}

	/**
	 * The view scene needs to be refreshed each time as the list of creations 
	 * changes. 
	 */
	private void refreshViewScene() {

		// Node creation
		Text message = new Text("Here is the list of current creation(s):");
		ListView<String> txtOutput = viewCreations();

		if (txtOutput.getItems().isEmpty()) {
			Text emptyMessage = new Text("There are currently no creations.");
			Button btnMainMenu = new Button("Back to main menu");
			btnMainMenu.setOnAction(event -> {
				displayMainMenu();
			});
			_viewScene = createScene(message, emptyMessage, btnMainMenu);
			
		} else {

			Button btnPlay = new Button("Play");
			Button btnDelete = new Button("Delete");
			Button btnQuitMainMenu = new Button("Quit to Main Menu");

			// Button functionality
			btnQuitMainMenu.setOnAction(event -> {displayMainMenu();});

			btnPlay.setOnAction(playEvent -> {
				String text = txtOutput.getSelectionModel().getSelectedItem();
				if (text != null) {
					String name = text.substring(3, text.length()-1);
					playVideo(name);
				}
			});

			btnDelete.setOnAction(deleteEvent -> {
				String text = txtOutput.getSelectionModel().getSelectedItem();
				if (text != null) {
					String name = text.substring(3, text.length()-1);
					Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Confirmation",
							"Would you like to delete " + name + "?").getAlert();
					if (alert.getResult() == ButtonType.OK) {
						File fileCreation = new File("./bin/creations/" + name + ".mp4");
						File fileAudio = new File("./bin/audio/" + name + ".mp4");
						File fileVideo = new File("./bin/video/" + name + ".mp4");
						fileCreation.delete();
						fileAudio.delete();
						fileVideo.delete();

						displayViewScene();
					}
				}

			});

			_viewScene = createScene(message, txtOutput, btnPlay, btnDelete, btnQuitMainMenu);
		}

	}

	/**
	 * Creates the stage for a new creation to play.
	 * @param name
	 */
	private void playVideo(String name) {

		// Node creation
		HBox playerPane = new HBox();

		Media video = new Media(Paths.get("./bin/creations/" + name + ".mp4").toUri().toString());
		MediaPlayer player = new MediaPlayer(video);
		player.setAutoPlay(true);

		MediaView viewer = new MediaView(player);

		playerPane.getChildren().add(viewer);
		playerPane.setAlignment(Pos.CENTER);

		Stage stage = new Stage();
		stage.setTitle("MediaPlayer");

		// buttonPane has play/pause, fastforward, mute, etc functionality
		Pane buttonPane = new ButtonPane(player);

		VBox vbox = new VBox(playerPane,buttonPane);
		vbox.setSpacing(20);
		Scene scene = new Scene(vbox, 500,400);
		stage.setScene(scene);
		stage.show();

		// Media functionality
		player.setOnEndOfMedia(() -> {
			player.dispose();
			stage.close();
		});

		stage.setOnCloseRequest(closeEvent -> {
			player.stop();
		});


	}

	/**
	 * Helper method to create Scene. 
	 * @param children Nodes that are to be included in the Scene, in the order that they appear.
	 * @return Created Scene.
	 */
	private Scene createScene(Node... children) {
		VBox vbox = new VBox();

		for (Node child: children) {
			if (child instanceof Button) {
				((Button) child).setMaxWidth(200);
			}
			vbox.getChildren().add(child);
		}

		vbox.setSpacing(20);
		vbox.setAlignment(Pos.CENTER);

		return new Scene(vbox, 400, 400);

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
