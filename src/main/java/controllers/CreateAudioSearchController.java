package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.java.application.AlertMaker;
import main.java.tasks.SearchTermTask;

/**
 * Controller for functionality of Create.fxml
 * @author Milk
 *
 */
public class CreateAudioSearchController extends Controller{

	@FXML
	private TextField _termInput;

	@FXML
	private Button _searchBtn;

	@FXML
	private Button _mainMenuBtn;

	@FXML
	private void search() {
		
		if (_termInput.getText().isEmpty()) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "The term cannot be empty.");
			
		} else {

			String term = _termInput.getText();

			// use a new thread to complete the search task
			SearchTermTask searchTask = new SearchTermTask(term, _mainApp);

			// show the loading scene while waiting so that the user may exit at any time.
			_mainApp.displayLoadingSearchResultsScene(searchTask);

			new Thread(searchTask).start();

		}
	}

	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
	}
}
