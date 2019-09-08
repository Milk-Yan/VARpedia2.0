package main.java.application;

import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

/**
 * Controller for functionality of SearchResults.fxml
 * @author Milk
 *
 */
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

	@FXML
	private Button _editSaveBtn;
	
	@FXML
	private Button _resetCancelBtn;
	
	//private SearchTask _searchTask;
	private StringManipulator _stringManipulator = new StringManipulator();
	
	private String _originalText;
	private String _currentText;
	
	/**
	 * enum to determine type of button.
	 */
	private enum ButtonType {
		EDIT("Edit Text"), 
		SAVE("Save Text"), 
		RESET("Reset to Default Text"), 
		CANCEL("Cancel Editing");
		
		private String _message;
		
		ButtonType(String message) {
			this._message = message;
		}
		
		/**
		 * 
		 * @return Message of the button for the specified type.
		 */
		public String getMessage() {
			// string is immutable so ok to send like this
			return _message;
		}
	};
	
	/**
	 * Initialise the searchResults TextArea and also the number of lines displayed to user.
	 */
	@FXML
	public void initialize() {
		
		SearchTask searchTask = WikiApplication.getInstance().getCurrentSearchTask();
		try {
			_originalText = searchTask.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_currentText = _originalText;

		_searchResults.setText(_originalText);
		// doesn't allow the textArea to be edited
		_searchResults.setEditable(false);
		
		int lineCount = _stringManipulator.countLines(_originalText);
		
		_enquiryText.setText("How many sentences would you like to include in your creation (1-" + lineCount + ")?");
	}
	
	@FXML
	private void create() {
		
		String inputLineNumber = _lineNumberInput.getText();
		
		if (validLineNumber(inputLineNumber)) {
			
			int lineNumber = Integer.parseInt(inputLineNumber);
			String chosenText = _stringManipulator.getChosenText(_currentText, lineNumber);
			
			WikiApplication.getInstance().setChosenText(chosenText);
			
			WikiApplication.getInstance().displayNamingScene();
			
		} else {
			
			int maxLineNumber = _stringManipulator.countLines(_currentText);
			
			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid value", "Please enter an integer between 1-" + maxLineNumber);
		
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

			//counting the number of lines from the textArea as it may be edited
			if (lineNumber > 0 && lineNumber <= _stringManipulator.countLines(text)) {
				return true;
			}
		} catch(NumberFormatException e) {
			// will return false anyway
		}
		return false;
	}

	/**
	 * Edit/Save text functionality when the button is pressed.
	 */
	@FXML
	private void editSave(){
		
		// save text functionality
		if (_editSaveBtn.getText().equals(ButtonType.EDIT.getMessage())){
			
			_searchResults.setEditable(true);
			_editSaveBtn.setText(ButtonType.SAVE.getMessage());
			_resetCancelBtn.setText(ButtonType.CANCEL.getMessage());
			
		} else {
			
			// save current edit
			
			// if text is empty, it is invalid.
			if (_searchResults.getText().isEmpty()) {
				new AlertMaker(AlertType.ERROR, "Error", "Invalid input", "Text field cannot be empty");
			}
			
			//removes all edited text and freezes the text box again
			_editSaveBtn.setText(ButtonType.EDIT.getMessage());
			_resetCancelBtn.setText(ButtonType.RESET.getMessage());
			_searchResults.setEditable(false);
			
			// reformats current text
			reformatText();
			
			_searchResults.setText(_currentText);
		
		}
	}

	@FXML
	private void resetCancel() {
		
		// reset functionality
		if (_resetCancelBtn.getText().equals(ButtonType.RESET.getMessage())) {
			_searchResults.setText(_originalText);
			_currentText = _originalText;
		}
		
		// cancel edit functionality
		else {
			
			// changes button text
			_editSaveBtn.setText(ButtonType.EDIT.getMessage());
			_resetCancelBtn.setText(ButtonType.RESET.getMessage());
			
			// revert text to what was before the edit button was pressed
			_searchResults.setText(_currentText);
			_searchResults.setEditable(false);
		}
	}
	
	/**
	 * method to reformat the TextArea to what the user has edited.
	 */
	private void reformatText(){
		String newText = _searchResults.getText();
		
		// remove the previous line numbers and add new numbers
		String rawText = _stringManipulator.removeNumberedLines(newText);
		String formattedText = _stringManipulator.createNumberedText(rawText);
		
		_currentText = formattedText;
		_searchResults.setText(formattedText);

	}

}
