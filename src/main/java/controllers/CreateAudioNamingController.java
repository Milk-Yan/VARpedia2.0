package main.java.controllers;

import java.io.File;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import main.java.application.AlertMaker;
import main.java.tasks.CreateAudioTask;

/**
 * Controller for functionality of Naming.fxml
 * @author Milk
 *
 */
public class CreateAudioNamingController extends Controller{
	
	@FXML
	private TextField _nameInput;
	
	@FXML
	private Button _createButton;
	
	@FXML
	private Button _mainMenuBtn;
	
	private String _name;
	private String _term;
	private String _chosenText;
	
	public void setUp(String term, String chosenText) {
		_term = term;
		_chosenText = chosenText;
	}
	
	@FXML
	private void enter() {
		
		// check for correct input
		_name = _nameInput.getText();
		
		if (_name.isEmpty()) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");
			
		} else if (_name.contains(" ")) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain spaces");
			
		} else if (new File("./bin/creations/" + _name + ".mp4").isFile()) {
			
			// check if want to overwrite
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "File already exists",
										"Would you like to overwrite the existing file?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				create();
			}
			
		} else {
			create();
		}
	}
	
	private void create() {
		
		// use new thread to create in bg
		CreateAudioTask createTask = new CreateAudioTask(_name, _term, _chosenText, _mainApp);
		new Thread(createTask).start();

		
	}
	
	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenu();
	}
}
