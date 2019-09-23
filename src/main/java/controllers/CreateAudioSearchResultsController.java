package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import main.java.application.AlertMaker;
import main.java.application.ButtonState;
import main.java.application.StringManipulator;

/**
 * Controller for functionality of SearchResults.fxml
 * @author Milk
 *
 */
public class CreateAudioSearchResultsController extends Controller{

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

	@FXML
	private Button _previewBtn;

	private StringManipulator _stringManipulator = new StringManipulator();

	private String _term;
	private String _sourceString;

	private String _currentTextFormatted;
	private String _currentTextNotFormatted;

	/**
	 * Initialise the searchResults TextArea and also the number of lines displayed to user.
	 */
	public void setUp(String term, String searchResults) {
		_term = term;
		_sourceString = searchResults;

		_currentTextNotFormatted = _sourceString;
		_currentTextFormatted = _stringManipulator.createNumberedText(_sourceString);

		_searchResults.setText(_currentTextFormatted);
		// doesn't allow the textArea to be edited
		_searchResults.setEditable(false);

		_searchResults.setWrapText(true);

		int lineCount = _stringManipulator.countLines(_currentTextFormatted);
		_enquiryText.setText(getEnquiryMessage(lineCount));
	}

	@FXML
	private void create() {

		// check if in edit mode
		if (_editSaveBtn.getText() == ButtonState.SAVE.getText()) {
			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid operation", "Cannot create while in editing mode.");
			return;
		}

		String inputLineNumber = _lineNumberInput.getText();

		if (validLineNumber(inputLineNumber)) {

			int lineNumber = Integer.parseInt(inputLineNumber);

			String chosenText = _stringManipulator.getChosenText(_currentTextNotFormatted, lineNumber);

			_mainApp.displayCreateAudioNamingScene(_term, chosenText);

		} else {

			int maxLineNumber = _stringManipulator.countLines(_currentTextFormatted);

			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid value", "Please enter an integer between 1-" + maxLineNumber);

		}
	}

	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
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
			if (lineNumber > 0 && lineNumber <= _stringManipulator.countLines(_currentTextFormatted)) {
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

		// edit text functionality
		if (_editSaveBtn.getText().equals(ButtonState.EDIT.getText())){

			_searchResults.setEditable(true);
			_editSaveBtn.setText(ButtonState.SAVE.getText());
			_resetCancelBtn.setText(ButtonState.CANCEL.getText());
			_searchResults.setText(_currentTextNotFormatted);

			// save text functionality
		} else {

			// if text is empty, it is invalid.
			if (_searchResults.getText().trim().isEmpty()) {
				new AlertMaker(AlertType.ERROR, "Error", "Invalid input", "Text field cannot be empty");
				return;
			}

			//removes all edited text and freezes the text box again
			_editSaveBtn.setText(ButtonState.EDIT.getText());
			_resetCancelBtn.setText(ButtonState.RESET.getText());
			_searchResults.setEditable(false);

			// reformats current text
			reformatText();

			// refresh the linecount shown to the user
			int lineCount = _stringManipulator.countLines(_currentTextFormatted);
			_enquiryText.setText(getEnquiryMessage(lineCount));

		}
	}

	@FXML
	private void resetCancel() {

		// reset functionality
		if (_resetCancelBtn.getText().equals(ButtonState.RESET.getText())) {
			_currentTextNotFormatted = _sourceString;
			_currentTextFormatted = _stringManipulator.createNumberedText(_currentTextNotFormatted);
			_searchResults.setText(_currentTextFormatted);

			int lineCount = _stringManipulator.countLines(_currentTextFormatted);
			_enquiryText.setText(getEnquiryMessage(lineCount));
		}

		// cancel edit functionality
		else {

			// changes button text
			_editSaveBtn.setText(ButtonState.EDIT.getText());
			_resetCancelBtn.setText(ButtonState.RESET.getText());

			// revert text to what was before the edit button was pressed
			_searchResults.setText(_currentTextFormatted);
			_searchResults.setEditable(false);
		}
	}

	/**
	 * method to reformat the TextArea to what the user has edited.
	 */
	private void reformatText(){
		String newText = _searchResults.getText();

		// remove the previous line numbers and add new numbers
		_currentTextNotFormatted = _stringManipulator.removeNumberedLines(newText);
		_currentTextFormatted = _stringManipulator.createNumberedText(_currentTextNotFormatted);

		_searchResults.setText(_currentTextFormatted);

	}

	private String getEnquiryMessage(int lineCount) {
		return ("How many sentences would you like to include in your creation (1-" + lineCount + ")?");
	}

	@FXML
	private void preview() {

		// check if in edit mode
		if (_editSaveBtn.getText() == ButtonState.SAVE.getText()) {
			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid operation", "Cannot preview while in editing mode.");
			return;
		}
		
		String selectedText = _searchResults.getSelectedText();
		String selectedTextNotFormatted = _stringManipulator.removeNumberedLines(selectedText);
		//count the number of spaces, hence words
		int words = new java.util.StringTokenizer(selectedTextNotFormatted," ").countTokens();

		if (words > 40) {
			new AlertMaker(AlertType.ERROR, "Error", "Text to preview is too large", "Please choose a chunck of text within 40 characters.");
		} else if (selectedTextNotFormatted.trim().isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "Text to preview is empty", "There is no text to preview.");
		} else {
			_mainApp.displayCreateAudioPreviewScene(_term, selectedTextNotFormatted, _previewBtn.getScene());
		}
	}
}



