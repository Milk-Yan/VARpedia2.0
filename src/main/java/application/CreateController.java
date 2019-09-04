package main.java.application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class CreateController {

	@FXML
	private Text _enquiryText;

	@FXML
	private TextField _termInput;

	@FXML
	private Button _searchBtn;

	@FXML
	private Button _mainMenuBtn;

	@FXML
	private void search() {
		if (_termInput.getText().isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "The term cannot be empty.");
		} else {

			String term = _termInput.getText();

			SearchTask searchTask = new SearchTask(term);
			WikiApplication.getInstance().displayLoadingScene(searchTask);

			new Thread(searchTask).start();

			searchTask.setOnCancelled(cancelledEvent -> {
				if (searchTask.isInvalid()) {
						new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid term", "This term cannot be found. Please try again.");
				}
					
				WikiApplication.getInstance().displayCreateScene();
			});
			
			searchTask.setOnSucceeded(succeededEvent -> {
				WikiApplication.getInstance().displaySearchResultsScene(term);
			});



		}
	}

	@FXML
	private void mainMenu() {
		WikiApplication.getInstance().displayMainMenu();
	}
}
