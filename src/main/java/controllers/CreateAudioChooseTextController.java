package main.java.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import main.java.application.AlertMaker;
import main.java.application.StringManipulator;
import main.java.tasks.PreviewAudioTask;

public class CreateAudioChooseTextController extends Controller {

	private String _term;
	private String _sourceString;

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

	/**
	 * Initialise the searchResults TextArea and also the number of lines displayed to user.
	 */
	public void setUp(String term, String searchResults) {
		_term = term;
		_sourceString = searchResults.trim();

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
		}
	}

	@FXML
	private void create() {
		// kill the current task if there is one
		if (_previewTask != null) {
			_previewTask.cancel();
		}


		//creates the audio
		//need to change to have voice type input
		int index = _voiceSelection.getSelectionModel().getSelectedIndex();
		_mainApp.displayCreateAudioNamingScene(_term, _chosenText.getText(), _voices.get(index));

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

		String chosenText = _chosenText.getText();

		// Error handling
		if (chosenText.trim().isEmpty()) {
			new AlertMaker(AlertType.ERROR, "Error", "No text chosen", "Please choose/enter some text.");
			return;
		} 

		StringManipulator _manipulator = new StringManipulator();
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
		_mainApp.displayMainMenuScene();
	}

	/**
	 * choice box of possible voices
	 * @return an ArrayList of voices
	 */
	private void listOfVoices(){
		_voices = new ArrayList<String>();
		_voiceName = new ArrayList<String>();
		//_voices.add(_defaultChoice);
		//_voiceName.add(_defaultChoice);

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


}
