package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.CreateAudioTask;

public class LoadingCreateAudioController extends Controller{

	private CreateAudioTask _task;
	
	@FXML
	private Button _mainMenuBtn;
	
	@FXML
	private void mainMenu() {
		// cancel current task before going back to main menu
		if (_task != null && !_task.isCancelled()) {
			_task.destroyProcess(); // need to destroy process before interrupting task 
			_task.cancel();
		}

		_mainApp.displayMainMenuScene();
	}
	
	public void setTask(CreateAudioTask task) {
		_task = task;
	}
}
