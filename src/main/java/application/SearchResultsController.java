package main.java.application;

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
	private Button _editButton;
	
	private SearchTask _searchTask;
	
	/**
	 * Initialise the searchResults TextArea and also the number of lines displayed to user.
	 */
	@FXML
	public void initialize() {
		_searchTask = WikiApplication.getInstance().getCurrentSearchTask();
		//does display text as intended without freezing
//		_searchResults.textProperty().bind(_searchTask.messageProperty());
		_searchResults.setText(_searchTask.getMessage());
		//doesnt allow the textArea to be ediited
		_searchResults.setEditable(false);
		_enquiryText = new Text("How many sentences would you like to include in your creation (1-" + _searchTask.lineCount() + ")?");
	}
	
	@FXML
	private void create() {
		String lineNumber = _lineNumberInput.getText();
		if (validLineNumber(lineNumber)) {
			WikiApplication.getInstance().displayNamingScene(_searchTask.getChosenText(), Integer.parseInt(lineNumber));
		} else {
			new AlertMaker(AlertType.ERROR, "Error encountered", "Invalid value", "Please enter an integer between 1-"+_searchResults.getText().split("\n").length);
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

			//counting the number of lines from the textArea as it may be editted
			if (lineNumber > 0 && lineNumber <= _searchResults.getText().split("\n").length) {
				return true;
			}
		} catch(NumberFormatException e) {
			// will return false anyway
		}
		return false;
	}

	/**
	 * helper method to enable editing properties for the search text
	 */
	@FXML
	private void editProperty(){
		if (_editButton.getText().equals("Edit Search Text")){
			_searchResults.setEditable(true);
			_editButton.setText("Cancel Editing");
		} else {
			//removes all edited text and freezes the text box again
			_editButton.setText("Edit Search Text");
			_searchResults.setEditable(false);
			//undoes all edits
			while(_searchResults.isUndoable()) {
				_searchResults.undo();
			}
		}
	}

	/**
	 * method to edit the textArea to what it originally was
	 * very very jank
	 * sorts up to the hundreds, which is probably not very useful
	 * @return
	 */
	private String textContent(){
		String edit=_searchResults.getText();
		edit=edit.replaceAll("."+" "+"\n"," "+"."+"\n");
		edit=edit.replace("1. ","");
		edit =edit.replaceAll("\n"+"\\d","\n");
		edit =edit.replaceAll("\n"+"\\d","\n");
		edit =edit.replaceAll("\n"+"\\d","\n");
		edit =edit.replaceAll("\n"+"."+" ","\n");
		edit=edit.replaceAll(" "+"."+"\n","."+"\n");
		_searchTask.updateText(edit);
		return edit;

	}

}
