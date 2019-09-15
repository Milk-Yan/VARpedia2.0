package main.java.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;

public class CreateCreationSearchController extends Controller{

	@FXML
	private TextField _wikitTerm;

	@FXML
	private Button _enterBtn;

	@FXML
	private Button _mainMenuBtn;

	@FXML
	private void enter() {
		String term = _wikitTerm.getText();
		
		// check if audio files exists
		File file = new File(System.getProperty("user.dir")+File.separator+"bin"+File.separator+"audio"+File.separator+term);
		
		if (file.isDirectory() && file.list().length>0) {
			
			_mainApp.displayCreateCreationChooseAudioScene(term);
			
		} else {
			
			new AlertMaker(AlertType.ERROR, "Error", "Audio files do not exist.", "You need to create audio files for this wikit term first.");
		
		}
	}

	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
	}
}
