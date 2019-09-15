package main.java.controllers;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.tasks.CreateCreationTask;

public class CreateCreationNamingController extends Controller{

	private String _name;
	private String _term;
	private ObservableList<String> _audioList;
	private int _wantedImageNumber;

	@FXML
	private TextField _nameInput;

	@FXML
	private Button _createBtn;

	@FXML
	private Button _mainMenuBtn;

	public void setUp(String term, ObservableList<String> audioList, int imageNumber) {
		_term = term;
		_audioList = audioList;
		_wantedImageNumber = imageNumber;
	}
	
	@FXML
	private void create() {

		String s = File.separator;

		// check for correct input
		_name = _nameInput.getText();

		if (_name.isEmpty()) {

			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

		} else if (_name.contains(" ")) {

			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain spaces");

		} else if (new File(System.getProperty("user.dir") + "bin" + s + "creations" + s + _name + ".mp4").isFile()) {

			// check if want to overwrite
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Creation already exists",
					"Would you like to overwrite the existing file?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				createCreation();
			}

		} else {
			createCreation();
		}
	}

	private void createCreation() {

		// use new thread to create in bg
		CreateCreationTask task = new CreateCreationTask(_name, _term, _audioList, _wantedImageNumber, _mainApp, _createBtn.getScene());
		_mainApp.displayLoadingCreateCreationScene(task);

		new Thread(task).start();

	}


	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
	}

}
