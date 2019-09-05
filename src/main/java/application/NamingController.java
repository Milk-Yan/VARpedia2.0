package main.java.application;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class NamingController {

	@FXML
	private Text _enquiryText;
	
	@FXML
	private TextField _nameInput;
	
	@FXML
	private Button _createButton;
	
	@FXML
	private Button _mainMenuBtn;
	
	private String _name;
	private WikiApplication _application = WikiApplication.getInstance();
	
	@FXML
	private void enter() {
		
		// check for correct input
		_name = _nameInput.getText();
		
		if (_name.isEmpty()) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");
			
		} else if (_name.contains(" ")) {
			
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain spaces");
			
		} else if (new File("./bin/creations/" + _name + ".mp4").isFile()) {
			
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "File already exists",
										"Would you like to overwrite the existing file?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				create();
			}
			
		} else {
			create();
		}
	}
	
	private void create() {
		
		CreateTask createTask = new CreateTask(_name, _application.getCurrentTerm(), _application.getCurrentText(), _application.getCurrentLineNumber());
		new Thread(createTask).start();

		createTask.setOnRunning(runningEvent -> {
			_application.displayLoadingScene(createTask);
		});
		
		createTask.setOnSucceeded(succeededEvent -> {
			new AlertMaker(AlertType.INFORMATION, "Completed", "Creation completed", "Press OK to exit to the main menu.");
			_application.displayMainMenu();
		});
		
		createTask.setOnCancelled(cancelledEvent -> {
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "The input is valid.");
			_application.displayMainMenu();
		});
	}
	
	@FXML
	private void mainMenu() {
		_application.displayMainMenu();
	}
}
