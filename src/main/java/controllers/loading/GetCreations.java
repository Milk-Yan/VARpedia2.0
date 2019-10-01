package main.java.controllers.loading;

import javafx.fxml.FXML;
import main.java.controllers.Controller;
import main.java.tasks.ViewAudioTask;
import main.java.tasks.ViewCreationsTask;

/**
 * Controller for GetCreations.fxml
 * @author wcho400
 *
 */
public class GetCreations extends Controller {

	private ViewCreationsTask _creationTask;
	private ViewAudioTask _audioTask;
	
	/**
	 * button to stop displaying creations. Returns to main menu
	 */
	@FXML
	private void mainMenuPress() {
		_creationTask.cancel();
		_audioTask.cancel();
		mainMenu();
	}
	
	/**
	 * Method to ensure correct tasks can be canceled
	 * @param creationTask
	 * @param audioTask
	 */
	public void setTask(ViewCreationsTask creationTask, ViewAudioTask audioTask) {
		_creationTask = creationTask;
		_audioTask = audioTask;
	}
}
