package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.SearchTermTask;

public class LoadingSearchResultsController extends Controller{

	private SearchTermTask _task;
	
	@FXML 
	private Button _mainMenuBtn;

	@FXML
	private void mainMenu() {

		// cancel current task before going back to main menu
		if (_task != null && !_task.isCancelled()) {
			_task.cancel();
		}

		_mainApp.displayMainMenuScene();
	}

	public void setTask(SearchTermTask task) {
		_task = task;
	}

}
