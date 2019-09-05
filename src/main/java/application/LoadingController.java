package main.java.application;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class LoadingController {

	@FXML
	private Text _loadingText;
	
	@FXML 
	private Button _mainMenuBtn;
	
	@FXML
	private void mainMenu() {
		
		Task<Void> task = WikiApplication.getInstance().getCurrentTask();
		
		// cancel current tasks
		if (task != null && !task.isCancelled()) {
			if (task instanceof SearchTask || task instanceof ViewTask) {
				task.cancel();
			}
			else if (task instanceof CreateTask) {
				((CreateTask) task).destroy();
			}
		}
		
		WikiApplication.getInstance().displayMainMenu();
	}
}
