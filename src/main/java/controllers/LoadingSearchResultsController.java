package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.SearchTermTask;

/**
 * Controller for LoadingSearchResultsController.fxml
 * @author wcho400
 *
 */
public class LoadingSearchResultsController extends Controller{

	private SearchTermTask _task;
	
	@FXML 
	private Button _mainMenuBtn;

	/**
	 * method to return to the main menu
	 * ask for confirmation before going
	 */
	@FXML
	private void mainMenu() {

		// cancel current task before going back to main menu
		if (_task != null && !_task.isCancelled()) {
			_task.cancel();
		}

		_mainApp.displayMainMenuScene();
	}

	/**
	 * method to ensure correct tasks are cancelled when prompted
	 * @param task
	 */
	public void setTask(SearchTermTask task) {
		_task = task;
	}

}
