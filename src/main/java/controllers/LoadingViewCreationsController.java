package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.tasks.ViewCreationsTask;

public class LoadingViewCreationsController extends Controller{

	private ViewCreationsTask _task;
	
	@FXML
	private Button _mainMenuBtn;
	
	@FXML
	private void mainMenu() {
		_task.cancel();
		_mainApp.displayMainMenuScene();
	}
	
	public void setTask(ViewCreationsTask task) {
		_task = task;
	}
}
