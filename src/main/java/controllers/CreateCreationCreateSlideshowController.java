package main.java.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CreateCreationCreateSlideshowController extends Controller{
	
	private String _term;
	private ObservableList<String> _audioList;
	
	@FXML
	private TextField _imageNumber;
	
	@FXML
	private Button _createBtn;
	
	@FXML
	private Button _mainMenuBtn;
	
	public void setUp(String term, ObservableList<String> audioList) {
		_term = term;
		_audioList = audioList;
	}
	
	@FXML
	private void create() {
		
		String imageNumberStr = _imageNumber.getText();
		int imageNumber = checkValidity(imageNumberStr);
		
		if (imageNumber != -1) {
			
			_mainApp.displayCreateCreationNamingScene(_term, _audioList, imageNumber);
			
		}
	}
	
	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
	}
	
	private int checkValidity(String numberStr) {
		
		try {
			int number = Integer.parseInt(numberStr);
			if (number > 0 && number <= 10) {
				return number;
			}
		} catch (NumberFormatException e) {
			// will return -1 anyway
		}
		return -1;
	}
}
