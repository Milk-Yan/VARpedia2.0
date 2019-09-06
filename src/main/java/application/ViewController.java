package main.java.application;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

/**
 * Controller for functionality of View.fxml
 * @author Milk
 *
 */
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
	@FXML
	private void initialize() {
		
		// could possibly move the viewtask in here but too lazy as of now.
		ObservableList<String> creationList = FXCollections.observableArrayList();
		for (String creation:WikiApplication.getInstance().getCurrentCreations().getItems()) {
			creationList.add(creation);
		}
		_listOfCreations.setItems(creationList);
	}

	@FXML
	private void play() {
		
		String selectionName = _listOfCreations.getSelectionModel().getSelectedItem();
		
		
		if (!(selectionName == null)) {
			String videoName = selectionName.replaceFirst("\\d+\\. ", "").replace("\n", "");
			WikiApplication.getInstance().playVideo(videoName);
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
				File fileCreation = new File("./bin/creations/" + videoName + ".mp4");
				File fileAudio = new File("./bin/audio/" + videoName + ".mp4");
				File fileVideo = new File("./bin/video/" + videoName + ".mp4");
				fileCreation.delete();
				fileAudio.delete();
				fileVideo.delete();

				WikiApplication.getInstance().displayViewScene();
			}
		}
	}

	@FXML
	private void mainMenu() {

		WikiApplication.getInstance().displayMainMenu();

	}
}
