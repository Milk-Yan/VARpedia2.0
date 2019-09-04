package main.java.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class SearchResultsController {

	@FXML
	private TextArea _searchResults;
	
	@FXML
	private Text _enquiryText;
	
	@FXML
	private TextField _lineNumberInput;
	
	@FXML
	private Button _createBtn;
	
	@FXML
	private Button _mainMenuBtn;
	
	private SearchTask _searchTask;
	
	public SearchResultsController() {
		_searchTask = WikiApplication.getInstance().getCurrentSearchTask();
		_searchResults.textProperty().bind(_searchTask.messageProperty());
		_enquiryText = new Text("How many sentences would you like to include in your creation (1-" + _searchTask.lineCount() + ")?");
	}
	
	@FXML
	private void create() {
		String lineNumber = _lineNumberInput.getText();
		if (validLineNumber(lineNumber)) {
			WikiApplication.getInstance().displayNamingScene(_searchTask.getChosenText(), Integer.parseInt(lineNumber));
		} else {
			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid value", "Please enter an integer between 1-"+_searchTask.lineCount());
		}
	}
	
	@FXML
	private void mainMenu() {
		WikiApplication.getInstance().displayMainMenu();
	}
	
	/**
	 * Helper method to check if the input is numeric.
	 * @param text The input text, normally entered by user.
	 * @return True if numeric, false otherwise.
	 */
	private boolean validLineNumber(String text) {
		try {
			int lineNumber = Integer.parseInt(text);
			if (lineNumber >=0 && lineNumber <= _searchTask.lineCount()) {
				return true;
			}
		} catch(NumberFormatException e) {
			// will return false anyway
		}
		return false;
	}
}
