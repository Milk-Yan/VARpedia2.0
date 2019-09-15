package main.java.controllers;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.tasks.ViewCreationsTask;

/**
 * Controller for functionality of View.fxml
 * @author Milk
 *
 */
public class ViewCreationsController extends Controller{

	@FXML
	private VBox _container;
	
	@FXML
	private ListView<String> _listOfCreations;

	@FXML
	private Button _playBtn;

	@FXML
	private Button _deleteBtn;

	@FXML
	private Button _mainMenuBtn;

	/**
	 * Creates a ListView of current creations. The search for
	 * current creations is implemented on a different thread to
	 * allow concurrency.
	 */
	public void setUp() {
		
		ViewCreationsTask viewTask = new ViewCreationsTask();
		new Thread(viewTask).start();
		
		_mainApp.displayLoadingViewCreationsScene(viewTask);
		
		try {
			ObservableList<String> creationsList = viewTask.get();
			
			if (creationsList.isEmpty()) {
				_listOfCreations.setVisible(false);
				_playBtn.setVisible(false);
				_deleteBtn.setVisible(false);
				
				Text text = new Text("There are currently no creations available.");
				_container.getChildren().add(1, text);
				
			} else {
				_listOfCreations.setItems(creationsList);
			}
			
			
		} catch (InterruptedException e) {
			// probably intended, don't do anything
		} catch (ExecutionException e) {
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "Execution Exception", "Could not execute the view function");
			});
		}
		
	}

	@FXML
	private void play() {

		String selectionName = _listOfCreations.getSelectionModel().getSelectedItem();
		String videoName = selectionName.replaceFirst("\\d+\\. ", "").replace("\n", "");

		if (videoName == null) {
			new AlertMaker(AlertType.ERROR, "Error", "Wrong selection", "Selection cannot be null");
		} else {

			if (!(selectionName == null)) {
				_mainApp.playVideo(videoName);
			}

		}
	}

	@FXML
	private void delete() {
		String creationName = _listOfCreations.getSelectionModel().getSelectedItem();
		if (creationName != null) {
			String videoName = creationName.replaceFirst("\\d+\\. ", "").replace("\n", "");
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Confirmation",
					"Would you like to delete " + videoName + "?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				String s = File.separator;
				File fileCreation = new File(System.getProperty("user.dir")+s+"bin"+s+"creations"+s+videoName+".mp4");
				//File fileAudio = new File(System.getProperty("user.dir")+s+"bin"+s+"audio"+s+videoName+".mp4");
				fileCreation.delete();
				//fileAudio.delete();

				_mainApp.displayViewCreationsScene();
			}
		}
	}

	@FXML
	private void mainMenu() {

		_mainApp.displayMainMenuScene();

	}
}
