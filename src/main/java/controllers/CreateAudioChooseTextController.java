package main.java.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import main.java.application.AlertMaker;
import main.java.application.StringManipulator;
import main.java.tasks.PreviewAudioTask;

public class CreateAudioChooseTextController extends Controller {

	private String _term;
	private String _sourceString;
	StringManipulator _manipulator = new StringManipulator();

	//the actual name
	private ArrayList<String> _voices;
	private final ArrayList<String> _voiceSample = new ArrayList<String>(Arrays.asList("Vanilla","Chocolate","Strawberry","Banana","Orange","Apple"));

	//the displayed name
	private ArrayList<String> _voiceName;

	private PreviewAudioTask _previewTask;

	@FXML
	private Text _message;

	@FXML
	private TextArea _searchResults;

	@FXML
	private TextArea _chosenText;

	@FXML
	private Label _wordLimit;

	@FXML
	private ChoiceBox<String> _voiceSelection;

	@FXML
	private Button _mainMenuBtn;

	/**
	 * Initialise the searchResults TextArea and also the number of lines displayed to user.
	 */
	public void setUp(String term, String searchResults) {
		_term = term;
		_sourceString = searchResults.trim();
		_wordLimit.setTextFill(Color.GREEN);

		_message.setText("Search results for " + _term + ": " );
		_searchResults.setText(_sourceString);

		//generate list of different voices
		listOfVoices();
		_voiceSelection.setItems(FXCollections.observableArrayList(_voiceName));
		_voiceSelection.getSelectionModel().selectFirst();

	}

	@FXML
	private void searchToChosen() {
		String highlightedText = _searchResults.getSelectedText();

		if (highlightedText.trim().isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "No text selected", "Please select some text.");
		} else {
			_chosenText.appendText(highlightedText);
			updateCount();
		}
	}

	@FXML
	private void create() {

		// kill the current task if there is one
		if (_previewTask != null) {
			_previewTask.cancel();
		}

		if (!_chosenText.getText().equals("") && (_manipulator.countWords(_chosenText.getText())<41)) {	
			//creates the audio
			//need to change to have voice type input

			if (_manipulator.countWords(_chosenText.getText())<4){
				Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Short Audio Creation",
						"A creation of this audio may not work as intended. Confirm creation?").getAlert();
				if (alert.getResult() == ButtonType.OK) {
					int index = _voiceSelection.getSelectionModel().getSelectedIndex();
					_mainApp.displayCreateAudioNamingScene(_term, _chosenText.getText(), _voices.get(index));
				} else {
					return;
				}
			} else {
				int index = _voiceSelection.getSelectionModel().getSelectedIndex();
				_mainApp.displayCreateAudioNamingScene(_term, _chosenText.getText(), _voices.get(index));
			}
		} else if (_chosenText.getText().equals("")){
			new AlertMaker(AlertType.ERROR, "Error", "No text selected", "Please select some text.");
			return;
		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Too much text selected", "Please remove some text.");
			return;
		}


		//creates the audio
		//need to change to have voice type input
		int index = _voiceSelection.getSelectionModel().getSelectedIndex();
		// remove all special characters
		String chosenText = _chosenText.getText().replaceAll("[^0-9 a-z\\.A-Z]", "");
		
		// clear the chosen text so if user comes back...
		_chosenText.clear();
		 
		_mainApp.displayCreateAudioNamingScene(_term, chosenText, _voices.get(index));


	}

	@FXML
	private void reset() {
		_searchResults.setText(_sourceString);
	}

	@FXML
	private void preview() {

		// kill the current task if there is one
		if (_previewTask != null) {
			_previewTask.cancel();
		}

		String chosenText = _chosenText.getText().trim().replaceAll("[^0-9 a-z\\.A-Z]", "");

		// Error handling
		if (chosenText.isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "No text chosen", "Please choose/enter some text.");
			return;
		} 

		int wordNumber = _manipulator.countWords(chosenText.trim());

		if (wordNumber > 40) {
			new AlertMaker(AlertType.ERROR, "Error", "Too much text", "Exceeded maximum of 40 words.");
			return;
		}


		int index = _voiceSelection.getSelectionModel().getSelectedIndex();
		_previewTask = new PreviewAudioTask(chosenText,_voices.get(index));
		new Thread(_previewTask).start();


	}



	@FXML
	private void mainMenu() {
		Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
				"Any unfinished progress will be lost").getAlert();
		if (alert.getResult() == ButtonType.OK) {
			_mainApp.displayMainMenuScene();
		}
	}

	/**
	 * choice box of possible voices
	 * @return an ArrayList of voices
	 */
	private void listOfVoices(){
		_voices = new ArrayList<String>();
		_voiceName = new ArrayList<String>();

		Process source;
		try {
			source = new ProcessBuilder("bash","-c","echo \"(voice.list)\" | festival -i"
					+ " | grep \"festival> (\" | cut -d \"(\" -f2 | cut -d \")\" -f1").start();
			InputStream stdout = source.getInputStream();
			StringManipulator manipulator = new StringManipulator();
			String lineOfVoices = manipulator.inputStreamToString(stdout);

			if (manipulator.countWords(lineOfVoices)>0) {
				String[] voicesOrder=lineOfVoices.split(" ");

				for (int i=0; i <voicesOrder.length;i++) {
					_voices.add(voicesOrder[i]);
					// add names of voices
					_voiceName.add(_voiceSample.get(i));
				}

			} else {
				//_voices.add(_voiceSample.get(0));
				//_voices.add(lineOfVoices);
				new AlertMaker(AlertType.ERROR, "Error", "No available voices", "Please install some festival voices");
			}


		} catch (IOException e) {
			new AlertMaker(AlertType.ERROR, "Error", "Cannot preview", "Could not preview with this voice.");
		}

	}

	@FXML
	private void selectReset() {
		_chosenText.setText("");
		updateCount();
	}

	private void updateCount() {
		String content=_chosenText.getText();
		int wordNumber = _manipulator.countWords(content.trim());
		String wordCount = String.valueOf(wordNumber);

		if (_chosenText.getText().equals("")) {
			_wordLimit.setTextFill(Color.GREEN);
			_wordLimit.setText("You're Good! (0)");
		} else if (wordNumber ==40){
			_wordLimit.setTextFill(Color.DARKRED); 
			_wordLimit.setText("At the limit!");
		} else if (wordNumber > 40) {
			_wordLimit.setTextFill(Color.RED); 
			_wordLimit.setText("Over the limit :( ("+wordCount+")");
		} else if (wordNumber > 30) {
			_wordLimit.setTextFill(Color.ORANGE);
			_wordLimit.setText("Near the limit ("+wordCount+")");
		} else {
			_wordLimit.setTextFill(Color.GREEN);
			_wordLimit.setText("You're Good! ("+wordCount+")");
		}


	}

	@FXML
	private void editCount() {
		if (_chosenText.getText().equals(" ")) {
			_chosenText.setText("");
		}
		updateCount();
	}


}
