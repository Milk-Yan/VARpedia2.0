package main.java.application;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class CreateCreationSearchController {
	@FXML
	private Text _wikitTermEnquiryText;

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
		File file = new File("./bin/audio/" + term);
		if (file.isDirectory() && file.list().length>0) {
			WikiApplication.getInstance().setCurrentTerm(term);
			WikiApplication.getInstance().displayCreateCreationChooseAudioScene();
		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Audio files do not exist.", "You need to create audio files for this wikit term first.");
		}
	}

	@FXML
	private void mainMenu() {
		WikiApplication.getInstance().displayMainMenu();
	}
}
