package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.CreateAudioTask;

/**
 * Controller for LoadingCreateAudio.fxml
 * @author wcho400
 *
 */
public class LoadingCreateAudioController extends Controller{

	private CreateAudioTask _task;
	
	@FXML
	private Button _mainMenuBtn;
	
	/**
	 * button to return to main menu if pressed
	 */
	@FXML
	private void mainMenu() {
		// cancel current task before going back to main menu
		if (_task != null && !_task.isCancelled()) {
			_task.destroyProcess(); // need to destroy process before interrupting task 
			_task.cancel();
		}

		_mainApp.displayMainMenuScene();
	}
	
	/**
	 * set tasks to be canceled when the user returns to menu before completion
	 * @param task
	 */
	public void setTask(CreateAudioTask task) {
		_task = task;
	}
}
