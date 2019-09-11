package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.CreateCreationTask;

public class LoadingCreateCreationController extends Controller{
	private CreateCreationTask _task;

	@FXML
	private Button _mainMenuBtn;

	@FXML
	private void mainMenu() {
		// cancel current task before going back to main menu
		if (_task != null) {
			_task.destroyProcess(); // need to destroy process before interrupting task 
			_task.cancel();
		}

		_mainApp.displayMainMenu();
	}

	public void setTask(CreateCreationTask task) {
		_task = task;
	}
}
