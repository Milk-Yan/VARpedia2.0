package main.java.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CreateCreationChooseAudioController extends Controller{

	@FXML
	private ListView<String> _audioList;

	@FXML
	private Button _confirmBtn;

	@FXML
	private Button _listenBtn;

	@FXML
	private Button _deleteBtn;

	@FXML 
	private Button _mainMenuBtn;

	@FXML
	private void initialize() {
		// make the ListView multiple-selection
		_audioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//_audioList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
		//	
		//});

		File folder = new File("./bin/audio/" + WikiApplication.getInstance().getCurrentTerm());
		File[] arrayOfAudioFiles = folder.listFiles((file) -> {
			if (file.getName().contains(".wav") ) {
				return true;
			} else {
				return false;
			}
		});

		// sorts the list of creations in alphabetical order.
		List<File> listOfAudioFiles = Arrays.asList(arrayOfAudioFiles);
		Collections.sort(listOfAudioFiles);

		// only gives the name of the file, and gives a number as 
		// indication of the number of current creations.
		ObservableList<String> audioList = _audioList.getSelectionModel().getSelectedItems();
		int lineNumber = 1;

		for (File file: listOfAudioFiles) {
			String name = file.getName().replace(".wav", "");
			audioList.add(lineNumber + ". " + name + "\n");
			lineNumber++;
		}

		_audioList.setItems(audioList);
	}

	@FXML
	private void confirm() {
		ObservableList<String> selectedList = _audioList.getSelectionModel().getSelectedItems();
		if (selectedList.isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "No items selected", "Please select at least one audio file.");
		} else if (selectedList.size() > 10) {
			new AlertMaker(AlertType.ERROR, "Error", "Too many items selected", "Please selected less than 10 audio files");
		} else {
			for (String audioName:selectedList) {
				// TO-DO: implement audio merging here (maybe a new task)
			}
		}
	}

	@FXML
	private void listen() {
		// instead of merging here, just play the audio one by one
		// maybe make it a listen/stop button?
	}

	@FXML
	private void delete() {
		ObservableList<String> selectedList = _audioList.getSelectionModel().getSelectedItems();
		if (selectedList.isEmpty()) {
			new AlertMaker(AlertType.ERROR,"Error", "No items to delete", "Please select the item you wish to delete");
		} else {
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Confirmation", "Deleting files", 
					"Are you sure you want to delete " + selectedList.size() + " items?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				for (String audioName:selectedList) {
					String s = File.separator;
					String fileName = audioName.replaceFirst("\\d+. ", "") + ".wav";
					File audioFile = new File(System.getProperty("user.dir") + s + "bin" + s + "audio" +
										WikiApplication.getInstance().getCurrentTerm() + s + fileName);
					audioFile.delete();
				}
			}
		}
	}

	@FXML
	private void mainMenu() {
		WikiApplication.getInstance().displayMainMenu();
	}
}
