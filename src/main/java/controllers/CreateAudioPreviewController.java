package main.java.controllers;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import main.java.tasks.PreviewAudioTask;

public class CreateAudioPreviewController extends Controller{

	private String _term;
	private String _previewText;
	
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
		
		_previewTextArea.setText(previewText);
		
		// auto play with current voice
		replay();
	}
	
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
		
		_task = new PreviewAudioTask(_previewText);
		new Thread(_task).start();

	}
	
	@FXML
	private void save() {
		// cancel current preview task before saving
		_task.cancel();
		
		_mainApp.displayCreateAudioNamingScene(_term, _previewText);
		
	}
	
	@FXML
	private void back() {
		Platform.runLater(() -> {
			_mainApp.displayPreviousScene(_previousScene);
		});
		
	}
	


}
