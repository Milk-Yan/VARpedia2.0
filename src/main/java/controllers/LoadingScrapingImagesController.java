package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import main.java.tasks.ScrapeImagesTask;

public class LoadingScrapingImagesController extends Controller{
	
	private ScrapeImagesTask _task;

	@FXML
	private Text _message;
	
	@FXML
	private Button _mainMenuBtn;

	@FXML
	private void mainMenu() {
		// cancel current task before going back to main menu
		if (_task != null) {
			_task.cancel();
		}

		_mainApp.displayMainMenuScene();
	}

	public void setTask(ScrapeImagesTask task, String term) {
		_task = task;
		_message.setText("Getting images for " + term + ". Please wait...");
	}
}
