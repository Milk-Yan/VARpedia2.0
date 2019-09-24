package main.java.controllers;

import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import main.java.application.AlertMaker;
import main.java.tasks.PreviewAudioTask;

import java.util.ArrayList;

public class CreateAudioPreviewController extends Controller{

	private final String _defaultChoice="Please select a voice";
	private String _term;
	private String _previewText;
	private String[] _voices= new String[]{"akl_nz_jdt_diphone","akl_nz_cw_cg_cg"};
	
	private Scene _previousScene;
	
	private PreviewAudioTask _task;
	
	
	@FXML
	private TextArea _previewTextArea;
	
	@FXML
	private ChoiceBox<String> _voiceSelection;
	
	@FXML
	private Button _replayBtn;
	
	@FXML
	private Button _saveBtn;
	
	@FXML
	private Button _backBtn;
	
	
	public void setUp(String term, String previewText, Scene searchResultsScene) {
		_term = term;
		_previewText = previewText;
		_previousScene = searchResultsScene;
		_previewTextArea.setWrapText(true);
		_previewTextArea.setText(previewText);
		_previewTextArea.setEditable(false);

		//generate list of different voices
		_voiceSelection.setItems(FXCollections.observableArrayList(listOfVoices()));
		_voiceSelection.getSelectionModel().selectFirst();
		//gets the selected choice not currently appropriate
//		_voiceSelection.getSelectionModel().getSelectedItem();
		// auto play with current voice
		replay();
	}

	//not using lol. might use it ill leave it here for now otherwise REMOVE
	@FXML
	private void changeVoice() {
		// this will be using the choice box, but we'll probably need to 
		// change the way the audio is created as well (I smell enums)
	}

	@FXML
	private void replay() {
		// kill the current task and start a new one

		if (_task != null) {
			_task.cancel();
		}

		if (_voiceSelection.getSelectionModel().getSelectedItem().equals(_defaultChoice)) {
			_task = new PreviewAudioTask(_previewText);
			new Thread(_task).start();
		} else {
			_task = new PreviewAudioTask(_previewText,_voiceSelection.getSelectionModel().getSelectedItem());
			new Thread(_task).start();
		}
	}
	
	@FXML
	private void save() {
		// cancel current preview task before saving
		_task.cancel();

		//check if a voice is chosen
		if (_voiceSelection.getSelectionModel().getSelectedItem().equals(_defaultChoice)){
			new AlertMaker(Alert.AlertType.ERROR, "Error", "A voice has yet to be chosen", "Please choose a voice.");
		} else {
			//creates the audio
			//need to change to have voice type input
			_mainApp.displayCreateAudioNamingScene(_term, _previewText, _voiceSelection.getSelectionModel().getSelectedItem());
		}
	}
	
	@FXML
	private void back() {
		Platform.runLater(() -> {
			_mainApp.displayPreviousScene(_previousScene);
		});
		
	}

	/**
	 * choice box of possible voices
	 * @return an ArrayList of voices
	 */
	private ArrayList<String> listOfVoices(){
		ArrayList<String> voices= new ArrayList<>();
		voices.add(_defaultChoice);
		//add voices soon TM these are voices currently used on my pc
		voices.add("kal_diphone");
		voices.add("ked_diphone");
		voices.add("don_diphone");
		voices.add("rab_diphone");

		//for image
//		for (String voice : _voices){
//			voices.add(voice);
//		}

		return voices;
	}
	
	



}
