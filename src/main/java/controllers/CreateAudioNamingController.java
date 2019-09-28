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
	private Button _createBtn;
	
	@FXML
	private Button _mainMenuBtn;
	
	private String _name;
	private String _term;
	private String _chosenText;
	private String _voice=null;
	
	public void setUp(String term, String chosenText, String voice) {
		_term = term;
		_chosenText = chosenText;
		_voice=voice;
	}
	
	@FXML
	private void enter() {
		
		String s = File.separator;
		
		// check for correct input
		_name = _nameInput.getText();
		
		if (_name.isEmpty()) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");
			
		} else if (_name.contains(" ")) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain spaces");
			
		} else if (new File(System.getProperty("user.dir") + s + "bin" + s + "audio" + s + _term + s + 
							_name + ".wav").isFile()) {
			
			// check if want to overwrite
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Audio file already exists",
										"Would you like to overwrite the existing file?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				create();
			}
			
		} else {
			create();
		}
	}
	
	@FXML
	private void create() {
		
		// use new thread to create in bg
		if (_voice==null){
			CreateAudioTask createTask = new CreateAudioTask(_name, _term, _chosenText, _mainApp);
			new Thread(createTask).start();
		} else {
			CreateAudioTask createTask = new CreateAudioTask(_name, _term, _chosenText, _mainApp, _voice);
			_voice=null;
			new Thread(createTask).start();
		}

		
	}
	
	@FXML
	private void mainMenu() {
		Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
				"Any unfinished progress will be lost").getAlert();
		if (alert.getResult() == ButtonType.OK) {
			_mainApp.displayMainMenuScene();
		}
		
	}
	

}
