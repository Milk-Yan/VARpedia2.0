package main.java.controllers;

import java.io.File;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.tasks.CreateCreationTask;

/**
 * Controller for CreateCreationNaming.fxml
 * @author wcho400
 *
 */
public class CreateCreationNamingController extends Controller{

	private String _name;
	private String _term;
	private ArrayList<String> _audioList;
	private ArrayList<String> _imageList;

	@FXML
	private TextField _nameInput;

	@FXML
	private Button _createBtn;

	@FXML
	private Button _mainMenuBtn;

	/**
	 * initializes parameters to be passed on to the next scene
	 * @param term
	 * @param audioList
	 * @param imageList
	 */
	public void setUp(String term, ArrayList<String> audioList, ArrayList<String> imageList) {
		_term = term;
		_audioList = audioList;
		_imageList = imageList;
	}
	
	/**
	 * button to pass name
	 * checks if name is valid
	 * if overlaps with an existing creation, ask for confirmation for overwriting
	 */
	@FXML
	private void create() {

		String s = File.separator;

		// check for correct input
		_name = _nameInput.getText();

		if (_name.isEmpty()) {

			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

		} else if (_name.contains(" ")) {

			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain spaces");
		
		} else if (new File(System.getProperty("user.dir") + "bin" + s + "creations" + s + _name + ".mp4").isFile()) {

			// check if want to overwrite
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Creation already exists",
					"Would you like to overwrite the existing file?").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				createCreation();
			}

		} else {
			createCreation();
		}
	}

	/**
	 * being task of creation creation
	 * pass's task to allow cancellation if desired
	 */
	private void createCreation() {

		// use new thread to create in bg
		CreateCreationTask task = new CreateCreationTask(_name, _term, _audioList, _imageList, _mainApp);
		_mainApp.displayLoadingCreateCreationScene(task);

		new Thread(task).start();

	}


	/**
	 * return to main menu
	 * asks for confirmation being returning
	 */
	@FXML
	private void mainMenu() {
		Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
				"Any unfinished progress will be lost").getAlert();
		if (alert.getResult() == ButtonType.OK) {
			_mainApp.displayMainMenuScene();
		}
	}
	
}
