package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for functionality of Main.fxml
 * @author Milk
 *
 */
public class MainMenuController extends Controller{
	
	@FXML
	private Button _createAudioBtn;
	
	@FXML
	private Button _createCreatioBtn;
	@FXML
	private Button _viewCreationsBtn;
	
	@FXML
	private void createAudio() {
		_mainApp.displayCreateAudioSearchScene();
	}
	
	@FXML
	private void createCreation() {
		_mainApp.displayCreateCreationSearchScene();
	}
	
	@FXML
	private void viewCreations() {
		_mainApp.displayViewScene();
	}
}
