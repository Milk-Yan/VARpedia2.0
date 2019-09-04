package main.java.application;

import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class ViewController {

	@FXML
	private Text _infoText;

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
	public ViewController() {

		ViewTask viewTask = new ViewTask();

		new Thread(viewTask).start();

		try {

			_listOfCreations = viewTask.get();

		} catch (InterruptedException | ExecutionException e) {

			new AlertMaker(AlertType.ERROR, "Error", "Error occurred", "Could not get the list of current creations.");

		}
	}

	@FXML
	private void play() {
		
		String videoName = _listOfCreations.getSelectionModel().getSelectedItem();
		
		if (videoName == null) {
			return;
		} else {
			WikiApplication.getInstance().playVideo(videoName);
		}
		
		

	}

	@FXML
	private void delete() {

	}

	@FXML
	private void mainMenu() {

		WikiApplication.getInstance().displayMainMenu();

	}
}
