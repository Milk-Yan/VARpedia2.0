package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.ViewAudioTask;
import main.java.tasks.ViewCreationsTask;

public class LoadingViewCreationsController extends Controller{

	private ViewCreationsTask _creationTask;
	private ViewAudioTask _audioTask;
	
	@FXML
	private Button _mainMenuBtn;
	
	@FXML
	private void mainMenu() {
		_creationTask.cancel();
		_audioTask.cancel();
		_mainApp.displayMainMenuScene();
	}
	
	public void setTask(ViewCreationsTask creationTask, ViewAudioTask audioTask) {
		_creationTask = creationTask;
		_audioTask = audioTask;
	}
}
